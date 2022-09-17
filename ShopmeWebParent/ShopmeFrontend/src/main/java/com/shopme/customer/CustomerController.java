package com.shopme.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		List<Country>allCountries=customerService.allCountries();
		model.addAttribute("countries",allCountries);
		model.addAttribute("pageTitle","Registration Form");
		model.addAttribute("customer",new Customer());
		return "register/registration_form";
	}
}
