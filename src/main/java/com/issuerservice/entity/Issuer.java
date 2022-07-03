package com.issuerservice.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@Table(name="issuer")
@IdClass(IssuerComposite.class)
public class Issuer {
	@Id
	@Column(name="isbn")
	public String isbn;
	
	@Id
	@Column(name="cust_id")
	public String custId;
	
	@Column(name="no_of_copies")
	public long noOfCopies;
	
	
	

}
