package com.shopme.admin.order;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.setting.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Orders;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CountryRepository countryRepository;

	public Page<Orders> allOrders(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort=Sort.by(sortField);
		sort=sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable=PageRequest.of(pageNum-1,5,sort);
		if(keyword!=null) {
			return orderRepository.findAll(keyword,pageable);
		}
		return orderRepository.findAll(pageable);		
	}
	
	public Orders get(Integer id) throws OrderNotFoundException {
		try {
			return orderRepository.findById(id).get();
		}catch (NoSuchElementException e) {
			throw new OrderNotFoundException("order not found with id: "+id);
		}		
	}
	
	public void deleteOrder(Integer id) throws OrderNotFoundException {
		Orders orders=get(id);
		if(orders!=null) {
			orderRepository.deleteById(id);
		}else {
			throw new OrderNotFoundException("order not found with id: "+id);
		}
	}

	public Orders getOrder(Integer id) throws OrderNotFoundException {		
		try {
			return orderRepository.findById(id).get();
		}catch (NoSuchElementException e) {
			throw new OrderNotFoundException("order not found with id: "+id);
		}		
	}

	public List<Country> getCountries() {		
		return countryRepository.findAllByOrderByNameAsc();
	}

	public void save(Orders orderInForm) {
		Orders orderInDB = orderRepository.findById(orderInForm.getId()).get();
		orderInForm.setOrderTime(orderInDB.getOrderTime());
		orderInForm.setCustomer(orderInDB.getCustomer());
		orderRepository.save(orderInForm);		
	}
}
