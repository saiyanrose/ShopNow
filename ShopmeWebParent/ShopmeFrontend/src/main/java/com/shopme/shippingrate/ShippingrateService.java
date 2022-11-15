package com.shopme.shippingrate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;

@Service
public class ShippingrateService {

	@Autowired
	private ShippingrateRepository shippingrateRepository;
	
	public ShippingRate getShippingRateForCustomer(Customer customer) {
		String state=customer.getState();
		if(state==null || state.isEmpty()) {
			state=customer.getCity();
		}
		return shippingrateRepository.findByCountryAndStates(customer.getCountry(),state);
	}
	
	public ShippingRate getShippingRateForAddress(Address address) {
		String state=address.getState();
		if(state==null || state.isEmpty()) {
			state=address.getCity();
		}
		return shippingrateRepository.findByCountryAndStates(address.getCountry(),state);
	}
}
