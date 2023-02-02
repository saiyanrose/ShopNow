package com.shopme.admin.customer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@Controller
public class CustomerController {
	private static final Logger LOGGER=LoggerFactory.getLogger(CustomerController.class);
	
	@Autowired
	private CustomerService customerService;

	@GetMapping("/customers")
	public String allCustomers(Model model) {
		LOGGER.info("Customer || allCustomers page called.");
		return listByPage(model,1,"id","asc",null);
	}

	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(Model model,@PathVariable(name="pageNum") int pageNum,
			@RequestParam(required=false,name="sortField") String sortField,
			@RequestParam(required=false,name="sortDir") String sortDir,
			@RequestParam(required=false,name="keyword") String keyword) {		
		
		if(sortDir==null) {
			sortDir="asc";
		}
		if(sortField==null) {
			sortField="id";
		}
		
		Page<Customer>page=customerService.findCustomerByPage(pageNum,sortField,sortDir,keyword);
		LOGGER.info("Customer || listByPage.");
		List<Customer>listCustomers=page.getContent();		
		
		long startCount=(pageNum-1)*CustomerService.CUSTOMER_PER_PAGE+1;
		
		long endCount=startCount + CustomerService.CUSTOMER_PER_PAGE-1;
		String reverseSort=sortDir.equals("asc") ? "desc" : "asc" ;
		if(endCount>page.getTotalElements()) {
			endCount=page.getTotalElements();
		}
		
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("pageNum",pageNum);
		model.addAttribute("startCount",startCount);
		model.addAttribute("endCount",endCount);
		model.addAttribute("totalItems",page.getTotalElements());
		model.addAttribute("totalPage",page.getTotalPages());
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSort",reverseSort);
		model.addAttribute("keyword",keyword);
		model.addAttribute("listCustomers",listCustomers);
		return "customers/customers";
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String customerStatusSet(@PathVariable("id")Integer id,@PathVariable("status")Boolean status,
			RedirectAttributes redirectAttributes) {		
		LOGGER.info("Customer || customerStatusSet called.");
		customerService.setEnabledDisabledCustomer(id,status);
		LOGGER.info("Customer || customerService.setEnabledDisabledCustomer");
		String enable="";
		if(status) {
			enable="enabled";
		}else {
			enable="disabled";
		}		
		redirectAttributes.addFlashAttribute("message","Customer with id "+id+ " " +enable+ " successfully!");
		LOGGER.info("Customer with id "+id+ " " +enable+ " successfully!");
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable("id")Integer id,RedirectAttributes redirectAttributes) {
		LOGGER.info("Customer||deleteCustomer called.");
		try {
			customerService.deleteCustomer(id);
			LOGGER.info("Customer with id "+id+" delete successfully!");
			redirectAttributes.addFlashAttribute("message","Customer with id "+id+" delete successfully!");
		}catch(NoCustomerFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			LOGGER.info("Customer || deleteCustomer "+e.getMessage());
		}		
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/edit/{id}")
	public String editFormCustomer(@PathVariable("id")Integer id,Model model,RedirectAttributes redirectAttributes) {
		LOGGER.info("Customer || editFormCustomer page called.");
		try {
			Customer customer=customerService.findById(id);
			List<Country>listCountries=customerService.findCountries();
			model.addAttribute("customer",customer);
			model.addAttribute("listCountries",listCountries);
			model.addAttribute("pageTitle","Edit Customer (ID: "+id+")");
			return "customers/editCustomer";			
		}catch(NoCustomerFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			LOGGER.info("Customer || editFormCustomer "+e.getMessage());
			return "redirect:/customers";
		}			
	}
	
	@PostMapping("/customers/save")
	public String saveCustomerAfterEdit(Customer customer,RedirectAttributes redirectAttributes) {	
		LOGGER.info("Customer || saveCustomerAfterEdit page called.");
		customerService.save(customer);
		LOGGER.info("Customer save successfully!");
		redirectAttributes.addFlashAttribute("message","Customer save successfully!");
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/detail/{id}")
	public String customerDetail(@PathVariable("id")Integer id,Model model,RedirectAttributes redirectAttributes) {
		LOGGER.info("Customer||customerDetail modal called");
		try {
			Customer customer=customerService.findById(id);
			model.addAttribute("customer",customer);
			return "customers/customer_modal";
		}catch (NoCustomerFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			LOGGER.info("Customer||customerDetail "+e.getMessage());
			return "redirect:/customers";
		}
	}
}
