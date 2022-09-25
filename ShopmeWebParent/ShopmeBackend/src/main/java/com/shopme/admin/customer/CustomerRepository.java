package com.shopme.admin.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.Customer;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {

	@Query("SELECT c FROM Customer c where c.email=:email")
	public Customer findCustomerByEmail(@Param("email") String email);

	@Query("SELECT c FROM Customer c where c.verificationCode=?1")
	public Customer findByVerificationCode(String code);

	@Query("UPDATE Customer c SET c.enabled=true WHERE c.id=?1")
	public void enabled(Integer id);
	
	@Query("UPDATE Customer c SET c.enabled=?2 WHERE c.id=?1")
	@Modifying
	public void enabled(Integer id,Boolean status);
	
	public Long countById(Integer id);

	@Query("SELECT c FROM Customer c WHERE CONCAT(c.email,' ',c.firstname,' ',c.lastname,' ',c.addressLine1,' ',"
			+ "c.addressLine2,' ',c.city,' ',c.state,' ',c.postalCode,' ',c.country.name) LIKE %?1%")
	public Page<Customer> findAll(String keyword,Pageable pageable);
}
