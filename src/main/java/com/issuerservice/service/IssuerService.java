package com.issuerservice.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.bookservice.entity.Books;
import com.issuerservice.authclient.IssuerAuthclient;
import com.issuerservice.entity.Issuer;
import com.issuerservice.repository.IssuerRepository;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import static com.issuerservice.config.ActiveMQConfig.ISSUER_EVENT_QUEUE;
import static com.issuerservice.config.ActiveMQConfig.ISSUER_FAILED_EVENT_QUEUE;
import static com.issuerservice.config.ActiveMQConfig.ISSUER_CANCEL_EVENT_QUEUE;

import java.util.List;
import java.util.Random;


@Service
public class IssuerService {

	 @Autowired
	JmsTemplate jmstemplate;
	@Autowired
	 KafkaTemplate kafkaTemplate;
	 
	 @Autowired
	 RestTemplate restTemplate;
	@Autowired
	EurekaClient eurekaClient;
	
	@Autowired
	IssuerRepository issuerRepo;
	
	@Autowired
	IssuerAuthclient authToken;
	//@Autowired
	//Issuer issuerB;
	
	 //@Autowired 
	 //Books books=new Books();
	 
	
	@Value("${bookservice.loadbalancer.instance}")
	String bookserviceInstance;
	
	@Value("${issuer.instance}")
	String issuerserviceInstance;
	
	
	public IssuerService(KafkaTemplate<String,Books> kafkaTemplate) {
		this.kafkaTemplate=kafkaTemplate;
	}
	
	
	public long findavailableBooks(String id) {
		System.out.println("bookserviceInstance :"+bookserviceInstance);
		//InstanceInfo instance = eurekaClient.getNextServerFromEureka(bookserviceInstance, false);
		//System.out.println("Book Service Instance :"+instance.getHomePageUrl());
		//String bookservice="bookservice";
		//String bookservice=instance.getHomePageUrl();
		String url="http://"+bookserviceInstance+"/books/fetch/"+id;
		HttpHeaders headers = new HttpHeaders();
		
		String oauthurl="http://"+bookserviceInstance+"/oauth/token";
		String oauthToken=authToken.getOAuthToken(oauthurl);
		System.out.println("oauthToken: "+oauthToken);
		headers.add("Authorization","Bearer "+oauthToken );
		
		HttpEntity<String> request = new HttpEntity<String>(headers);		
		
		//String url="http://localhost:1000/books/fetch/"+id;
		Books books= restTemplate.getForObject(url, Books.class);
		
		return books.getTotalCopies()-books.getIssuedCopies();
		
		
	}
	
	public String fallbackDeleteBooks(Exception ex) {
		return "There is some issues while deleting the available issued book. Please check the Books Load Balancer once.";
	}
	
	@CircuitBreaker(name="backup",fallbackMethod="fallbackIssueBooks")
	public String issueBooks(String id,Long noOfCopies) {
		//InstanceInfo instance = eurekaClient.getNextServerFromEureka(issuerserviceInstance, false);
		//System.out.println("Issuer Service Instance :"+instance.getHomePageUrl());

		//String issuerservice=instance.getHomePageUrl();
		//String url=issuerservice+"issuer/fetch/"+id;
		//String url="http://"+issuerserviceInstance+"/issuer/fetch/"+id;
		//Long avlBooks= new RestTemplate().getForObject(url, Long.class);
		Long avlBooks= findavailableBooks(id);
		String message="The Book with ISBN no:"+id+" has been issued with quantity :"+noOfCopies+" .";
		if (avlBooks-noOfCopies>0) {
			Issuer issuerB=new Issuer();
			issuerB.setIsbn(id);
			
			issuerB.setCustId("Customer_Smart");
			issuerB.setNoOfCopies(noOfCopies);
			issuerRepo.save(issuerB);
			Books books=new Books();
			books.setIsbn(id);
			books.setIssuedCopies(noOfCopies);//IssuedCopy Increase Purpose
			jmstemplate.convertAndSend( ISSUER_EVENT_QUEUE,books);
			//kafkaTemplate("issuertopic",books);
			
		}else {
			message="Sufficient books of id :"+id+" is not available at this momemnt";
		}
		return message;
	}
	
	public String fallbackIssueBooks(Exception ex) {
		return "There is some issues while Issuing the Books. Please check the Books Load Balancer once.";
	}
	
	@JmsListener(destination=ISSUER_FAILED_EVENT_QUEUE)
	public void BookIssuedIncreaseFailed(@Payload Books books) {
		issuerRepo.deleteById(books.getIsbn());// Increase in Book Service fails, Book's entry gets deleted from Issuer Table
		
}
@Transactional
@CircuitBreaker(name="backup",fallbackMethod="fallbackDeleteBooks")
	public String cancelIssuedBook(String isbn, String custId) {
		Issuer issList= issuerRepo.findByIdForCustomer(isbn,custId);
		if(issList!=null) {
		 issuerRepo.deleteBookForCustomer(isbn,custId);
		 Books books=new Books();
			books.setIsbn(isbn);
			
			books.setIssuedCopies(issList.getNoOfCopies());// To decrease the Count
		 jmstemplate.convertAndSend( ISSUER_CANCEL_EVENT_QUEUE,books);
		}else {
			return "Book :"+isbn+" is not available for Customer :"+custId;
		}
		return "Book :"+isbn+" is cancelled for Customer :"+custId;
		
	}
	

}
