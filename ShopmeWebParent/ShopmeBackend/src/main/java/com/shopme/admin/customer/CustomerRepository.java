package com.shopme.admin.customer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Integer>{

	@Query("SELECT c FROM Customer c where c.email=?1")
	public Customer findByEmail(String email);
	
	@Query("SELECT c FROM Customer c where c.verificationCode=?1")
	public Customer findByVerificationCode(String code);
	
	@Query("UPDATE Customer c SET c.enabled=true WHERE c.id=?1")
	public void enabled(Integer id);
}
