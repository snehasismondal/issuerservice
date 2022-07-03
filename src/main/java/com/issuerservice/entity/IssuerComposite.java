package com.issuerservice.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;

public class IssuerComposite implements Serializable {
	
	public IssuerComposite() {
		super();
	}


	public IssuerComposite(String isbn, String custId) {
		this.isbn = isbn;
		this.custId = custId;
	}


	private String isbn;
	
	
	private String custId;
	

}
