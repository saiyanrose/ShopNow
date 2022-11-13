package com.shopme.address;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;
import com.shopme.security.CustomerOauthUser;

@Controller
public class AddressController {

	@Autowired
	private AddressService addressService;
	
	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/address_book")
	public String addressBookList(Model model,HttpServletRequest request) {
		Customer customer=getAuthenticatedCustomer(request);
		List<Address>listAddress= addressService.listAddressBook(customer);
		
		boolean userPrimaryAsDefault=true;
		for(Address ad:listAddress) {
			if(ad.isDefaultShipping()) {
				userPrimaryAsDefault=false;
				break;
			}
		}
		model.addAttribute("pageTitle","Address Book");
		model.addAttribute("listAddress",listAddress);
		model.addAttribute("customer",customer);
		model.addAttribute("userPrimaryAsDefault",userPrimaryAsDefault);
		return "address_book/addressess";
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request){
		String email=emailOfAuthenticatedCustomer(request);		
		return customerService.getCustomerbyEmail(email);
	}
	
	private String emailOfAuthenticatedCustomer(HttpServletRequest request) {
		Object principal=request.getUserPrincipal();
		
		if(principal==null) return null;
		
		String customerEmail=null;
		if(principal instanceof UsernamePasswordAuthenticationToken || 
				principal instanceof RememberMeAuthenticationToken) {
			
			customerEmail=request.getUserPrincipal().getName();
		}else if(principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oAuth2AuthenticationToken=(OAuth2AuthenticationToken) principal;
			CustomerOauthUser customerOauthUser=(CustomerOauthUser) oAuth2AuthenticationToken.getPrincipal();
			customerEmail=customerOauthUser.getEmail();
		}		
		return customerEmail;
	}
	
	@GetMapping("/address_book/new")
	public String newAddressBook(Model model,HttpServletRequest request) {
		Customer customer=getAuthenticatedCustomer(request);
		List<Country>allCountry=customerService.allCountries();
		
		model.addAttribute("pageTitle","New Address");
		model.addAttribute("countries",allCountry);
		model.addAttribute("addressBook",new Address());
		return "address_book/addressess_new";
	}
	
	@PostMapping("/address/save")
	public String saveNewAddressBook(Address address,HttpServletRequest request,RedirectAttributes redirectAttributes) {
		Customer customer=getAuthenticatedCustomer(request);
		
		address.setCustomer(customer);
		addressService.saveAddress(address);
		
		redirectAttributes.addFlashAttribute("message","Address save successfully.");
		return "redirect:/address_book";
	}
	
	@GetMapping("/address/edit/{id}")
	public String editAddress(HttpServletRequest request,@PathVariable("id")Integer id,Model model) {
		Customer customer=getAuthenticatedCustomer(request);
		List<Country>allCountry=customerService.allCountries();
		
		Address address=addressService.get(id,customer.getId());
		
		model.addAttribute("addressBook",address);
		model.addAttribute("countries",allCountry);
		model.addAttribute("heading","Edit Address (ID:" +id+")");
		model.addAttribute("pageTitle","Edit Address");
		
		return "address_book/addressess_new";
	}
	
	@GetMapping("/address/delete/{id}")
	public String deleteAddress(@PathVariable("id")Integer id,RedirectAttributes redirectAttributes,HttpServletRequest request) {
		
		Customer customer=getAuthenticatedCustomer(request);
		addressService.delete(id,customer.getId());
		
		redirectAttributes.addFlashAttribute("message","address deleted successfully.");
		return "redirect:/address_book";
	}
}
