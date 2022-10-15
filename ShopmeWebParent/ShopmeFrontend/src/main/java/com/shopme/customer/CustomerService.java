package com.shopme.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.AuthenticationType;
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
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		customer.setCreatedTime(new Date());
		String randomCode=RandomString.make(64);
		customer.setVerificationCode(randomCode);
		System.out.println("Verification code: "+customer.getVerificationCode());
		customerRepository.save(customer);
	}	
	
	private void encodePassword(Customer customer) {
		String encodedPassword=passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}

	public void update(Customer customer) {
		Customer customerForSave=customerRepository.findById(customer.getId()).get();
		if(customerForSave.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
			if(customer.getPassword().isEmpty()) {
				customer.setPassword(customerForSave.getPassword());
			}else {
				encodePassword(customer);
			}
		}else {
			customer.setPassword(customerForSave.getPassword());
		}
		customer.setEnabled(true);
		customer.setCreatedTime(customerForSave.getCreatedTime());
		customer.setVerificationCode(customerForSave.getVerificationCode());
		customer.setAuthenticationType(customerForSave.getAuthenticationType());
		customerRepository.save(customer);
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
	
	public void updateAuthenticationType(Customer customer,AuthenticationType authenticationType) {
		if(!customer.getAuthenticationType().equals(authenticationType)) {
			customerRepository.updateAuthenticationType(customer.getId(),authenticationType);
		}
	}
	
	public Customer getCustomerbyEmail(String email) {
		return customerRepository.findByEmail(email);
	}

	public void addCustomerUponOauthLogin(String name, String email,String code) {		
		Customer customer=new Customer();
		customer.setEmail(email);
		setName(name, customer);
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(AuthenticationType.GOOGLE);
		customer.setPassword("");
		customer.setAddressLine1("");
		customer.setAddressLine2("");
		customer.setCity("");
		customer.setState("");
		customer.setPhoneNumber("");
		customer.setPostalCode("");
		customer.setCountry(countryRepository.findByCode(code));
		customerRepository.save(customer);		
	}
	
	private void setName(String name,Customer customer) {
		String[] nameArray=name.split(" ");
		if(nameArray.length<2) {
			customer.setFirstname(name);
			customer.setLastname("");
		}else {
			String firstname=nameArray[0];
			customer.setFirstname(firstname);
			
			String lastname=name.replace(firstname,"");
			customer.setLastname(lastname);
		}
	}
}
