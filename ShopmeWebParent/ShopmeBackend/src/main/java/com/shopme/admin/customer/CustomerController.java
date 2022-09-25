package com.shopme.admin.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@Controller
public class CustomerController {
	
	@Autowired
	private CustomerService customerService;

	@GetMapping("/customers")
	public String allCustomers(Model model) {
		return listByPage(model,1,"firstname","asc",null);
	}

	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(Model model,@PathVariable(name="pageNum") int pageNum,
			@Param("sortField") String sortField,
			@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {		
		Page<Customer>page=customerService.findCustomerByPage(pageNum,sortField,sortDir,keyword);		
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
		return "customers";
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String customerStatusSet(@PathVariable("id")Integer id,@PathVariable("status")Boolean status,
			RedirectAttributes redirectAttributes) {		
		customerService.setEnabledDisabledCustomer(id,status);
		String enable="";
		if(status) {
			enable="enabled";
		}else {
			enable="disabled";
		}
		redirectAttributes.addFlashAttribute("message","Customer with id "+id+ " " +enable+ " successfully!");
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable("id")Integer id,RedirectAttributes redirectAttributes) {
		try {
			customerService.deleteCustomer(id);
			redirectAttributes.addFlashAttribute("message","Customer with id "+id+" delete successfully!");
		}catch(NoCustomerFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
		}
		
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/edit/{id}")
	public String editFormCustomer(@PathVariable("id")Integer id,Model model,RedirectAttributes redirectAttributes) {
		try {
			Customer customer=customerService.findById(id);
			List<Country>listCountries=customerService.findCountries();
			model.addAttribute("customer",customer);
			model.addAttribute("listCountries",listCountries);
			model.addAttribute("pageTitle","Edit Customer (ID: "+id+")");
			return "editCustomer";
			
		}catch(NoCustomerFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/customers";
		}			
	}
	
	@PostMapping("/customers/save")
	public String saveCustomerAfterEdit(Customer customer,RedirectAttributes redirectAttributes) {	
		customerService.save(customer);
		redirectAttributes.addFlashAttribute("message","Customer save successfully!");
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/detail/{id}")
	public String customerDetail(@PathVariable("id")Integer id,Model model,RedirectAttributes redirectAttributes) {
		try {
			Customer customer=customerService.findById(id);
			model.addAttribute("customer",customer);
			return "customer_modal";
		}catch (NoCustomerFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/customers";
		}
	}
}
