package com.issuerservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableDiscoveryClient
//@EnableEurekaClient
public class IssuerserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IssuerserviceApplication.class, args);
		
		Logger logger
	     = LoggerFactory.getLogger(IssuerserviceApplication.class);
		
		logger.info("Issuer Service is Up and Running ");
	}
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
