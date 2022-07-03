package com.issuerservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.issuerservice.service.IssuerService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@RequestMapping("/issuer")
public class IsserController {
	
	@Autowired
	IssuerService issuerser;
	
	
	@GetMapping("/fetch/{isbn}")
	public long availablebooks(@PathVariable("isbn") String isbn) {
		return issuerser.findavailableBooks(isbn);
	}
	
	
	@PostMapping("/add/{isbn}/{noOfCopies}")
	public String issueBooks(@PathVariable("isbn") String isbn,@PathVariable("noOfCopies") Long noOfCopies) {
		return issuerser.issueBooks(isbn,noOfCopies);
	}
	
	
	
	
	@DeleteMapping("/cancel/{isbn}/{custId}")
	public String cancelIssuedBook(@PathVariable("isbn") String isbn,@PathVariable("custId") String custId) {
		String result=issuerser.cancelIssuedBook(isbn,custId);
		return result;
	}
	
	
}
