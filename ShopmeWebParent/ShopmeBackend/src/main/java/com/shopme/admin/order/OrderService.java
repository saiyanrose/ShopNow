package com.shopme.admin.order;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Orders;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	public Page<Orders> allOrders(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort=Sort.by(sortField);
		sort=sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable=PageRequest.of(pageNum-1,5,sort);
		if(keyword!=null) {
			return orderRepository.findAll(keyword,pageable);
		}
		return orderRepository.findAll(pageable);		
	}
}
