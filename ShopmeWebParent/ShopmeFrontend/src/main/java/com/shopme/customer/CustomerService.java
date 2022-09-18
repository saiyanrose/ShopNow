package com.shopme.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.settings.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private CountryRepository countryRepository;
	
	public List<Country> allCountries(){
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public boolean checkEmailUnique(String email) {
		Customer customer=customerRepository.findByEmail(email);
		return customer==null;
	}
	
	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		String randomCode=RandomString.make(64);
		customer.setVerificationCode(randomCode);
		System.out.println("Verification code: "+customer.getVerificationCode());
		customerRepository.save(customer);
	}

	private void encodePassword(Customer customer) {
		String encodePassword=passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodePassword);		
	}
	
	public boolean verify(String verificationCode) {
		Customer customer=customerRepository.findByVerificationCode(verificationCode);
		if(customer==null || customer.isEnabled()) {
			return false;
		}else {
			customerRepository.enabled(customer.getId());
			return true;
		}		
	}
}
