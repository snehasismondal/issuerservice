package com.issuerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ActiveMQConfig {
	public static final String ISSUER_EVENT_QUEUE="issuerevent";
	public static final String ISSUER_FAILED_EVENT_QUEUE="issuer_failed_event";
	public static final String  ISSUER_CANCEL_EVENT_QUEUE="issuer_cancelled_event";

	//serializes msz content to json from text msz
	@Bean
	public MessageConverter meesageConverter() {
		
		MappingJackson2MessageConverter converter= new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		converter.setObjectMapper(objectMapper());
		return converter;
		
	}
	
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper= new ObjectMapper();
		return mapper;
	}
}
