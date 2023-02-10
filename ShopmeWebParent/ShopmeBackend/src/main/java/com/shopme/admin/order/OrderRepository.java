package com.shopme.admin.order;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.OrderDetails;
import com.shopme.common.entity.Orders;

public interface OrderRepository extends PagingAndSortingRepository<Orders,Integer> {

	@Query("SELECT o FROM Orders o WHERE CONCAT('#',o.id) LIKE %?1% OR"
			+" CONCAT(o.firstName,' ',o.lastName) LIKE %?1% OR"
			+" o.firstName LIKE %?1% OR"
			+" o.lastName LIKE %?1% OR o.addressLine1 LIKE %?1% OR"
			+" o.addressLine2 LIKE %?1% OR o.city LIKE %?1% OR"
			+" o.state LIKE %?1% OR o.country LIKE %?1% OR"
			+" o.orderStatus LIKE %?1% OR o.paymentMethod LIKE %?1% OR o.customer.email LIKE %?1%")
	Page<Orders> findAll(String keyword, Pageable pageable);

	@Query("SELECT NEW com.shopme.common.entity.Orders(o.id,o.orderTime,o.productCost,o.subTotal,"
			+ "o.total) FROM Orders o WHERE o.orderTime BETWEEN ?1 AND ?2 ORDER BY o.orderTime ASC")
	List<Orders>findByOrderTimeBetween(Date startTime,Date endTime);
	
	@Query("SELECT NEW com.shopme.common.entity.OrderDetails(d.product.category.name,d.quantity,"
			+ "d.productCost,d.shippingCost,d.subTotal) FROM OrderDetails d WHERE d.orders.orderTime BETWEEN ?1 AND ?2")
	List<OrderDetails>findWithCategoryAndTimeBetween(Date startTime,Date endTime);
	
	@Query("SELECT NEW com.shopme.common.entity.OrderDetails(d.quantity,d.product.name,"
			+ "d.productCost,d.shippingCost,d.subTotal) FROM OrderDetails d WHERE d.orders.orderTime BETWEEN ?1 AND ?2")
	List<OrderDetails>findWithProductAndTimeBetween(Date startTime,Date endTime);

}
