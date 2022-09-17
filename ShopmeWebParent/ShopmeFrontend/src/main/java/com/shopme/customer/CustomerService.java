package com.shopme.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.settings.CountryRepository;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private CountryRepository countryRepository;
	
	public List<Country> allCountries(){
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public boolean checkEmailUnique(String email) {
		Customer customer=customerRepository.findByEmail(email);
		return customer==null;
	}
}
