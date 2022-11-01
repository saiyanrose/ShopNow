package com.shopme.admin.customer;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.setting.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.User;

@Service
@Transactional
public class CustomerService {
	public static final int CUSTOMER_PER_PAGE = 10;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private PasswordEncoder encoder;

	public Page<Customer> findCustomerByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, CUSTOMER_PER_PAGE);
		if (keyword != null) {
			return customerRepository.findAll(keyword, pageable);
		}
		return customerRepository.findAll(pageable);
	}

	public void setEnabledDisabledCustomer(Integer id, Boolean status) {
		customerRepository.enabled(id, status);
	}

	public void deleteCustomer(Integer id) throws NoCustomerFoundException {
		Long customerId = customerRepository.countById(id);
		if (customerId == null || customerId == 0) {
			throw new NoCustomerFoundException("No Customer found with id: " + id);
		}
		customerRepository.deleteById(id);
	}

	public Customer findById(Integer id) throws NoCustomerFoundException {
		Long customerId = customerRepository.countById(id);
		if (customerId == null || customerId == 0) {
			throw new NoCustomerFoundException("No Customer found with id: " + id);
		}
		return customerRepository.findById(id).get();		
	}

	public List<Country> findCountries() {
		List<Country> country = (List<Country>) countryRepository.findAll();
		return country;
	}

	public String checkUnique(Integer id, String email) {
		boolean isCreatingnew = (id == null || id == 0);
		Customer customerByEmail = customerRepository.findCustomerByEmail(email);
		if (isCreatingnew) {
			if (customerByEmail != null)
				return "Duplicate";
		} else {
			if (customerByEmail != null && customerByEmail.getId() != id)
				return "Duplicate";
		}
		return "OK";
	}
	
	private void encodePassword(Customer customer) {
		String encodedPassword=encoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}

	public void save(Customer customer) {
		Customer customerForSave=customerRepository.findById(customer.getId()).get();
		if(customer.getPassword().isEmpty()) {
			customer.setPassword(customerForSave.getPassword());
		}else {
			encodePassword(customer);
		}
		customer.setEnabled(true);
		customer.setCreatedTime(customerForSave.getCreatedTime());
		customer.setVerificationCode(customerForSave.getVerificationCode());
		customer.setAuthenticationType(customerForSave.getAuthenticationType());
		customer.setResetPasswordToken(customerForSave.getResetPasswordToken());
		customerRepository.save(customer);
	}

}
