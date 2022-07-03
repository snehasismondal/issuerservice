package com.issuerservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.issuerservice.entity.Issuer;

public interface IssuerRepository extends JpaRepository<Issuer, String> {
	@Modifying
	@Query("DELETE FROM Issuer where isbn=?1 and cust_id=?2 ")
	int deleteBookForCustomer(String isbn, String custId);
	@Query("FROM Issuer where isbn=?1 and cust_id=?2 ")
	Issuer findByIdForCustomer(String isbn, String custId);

}
