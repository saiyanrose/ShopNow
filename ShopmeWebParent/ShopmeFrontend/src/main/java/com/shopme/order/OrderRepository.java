package com.shopme.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders,Integer> {

	@Query("SELECT o FROM Orders o JOIN o.orderDetails od JOIN od.product p "
			+ "WHERE o.customer.id=?2 "
			+ "AND(p.name LIKE %?1% OR o.orderStatus LIKE %?1%)")
	public Page<Orders>findAll(String keyword,Integer customerId,Pageable pageable);
	
	@Query("SELECT o FROM Orders o WHERE o.customer.id=?1")
	public Page<Orders>findAll(Integer customerId,Pageable pageable);
}
