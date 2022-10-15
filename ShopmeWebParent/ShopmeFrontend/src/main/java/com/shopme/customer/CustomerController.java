package com.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.EmailSettingBag;
import com.shopme.security.CustomerOauthUser;
import com.shopme.security.CustomerUserDetails;
import com.shopme.settings.SettingService;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		List<Country>allCountries=customerService.allCountries();
		model.addAttribute("countries",allCountries);
		model.addAttribute("pageTitle","Registration Form");
		model.addAttribute("customer",new Customer());
		return "register/registration_form";
	}
	
	@PostMapping("/create_customer")
	public String registerCustomer(Customer customer,Model model,HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		customerService.registerCustomer(customer);
		sendVerificationEmail(request,customer);
		model.addAttribute("pageTitle","Registration Succeeded");
		return "register/register_success";
	}

	private void sendVerificationEmail(HttpServletRequest request, Customer customer) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings=settingService.getEmailSettings();
		
		JavaMailSenderImpl mailSender=Utility.prepareMailSender(emailSettings);
		String toAddress=customer.getEmail().replace(" ","").trim();
		String subject=emailSettings.getVerifySubject().replace(" ","").trim();
		String content=emailSettings.getVerifyContent();
		
		MimeMessage sendMessage=mailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(sendMessage);
		helper.setFrom(subject,emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		content=content.replace("[[name]]",customer.getFullName());		
		String verifyUrl=Utility.getSiteUrl(request) + "/verify?code=" +customer.getVerificationCode();
		content=content.replace("[[URL]]",verifyUrl);
		helper.setText(content,true);
		
		mailSender.send(sendMessage);
		System.out.println("verify url: "+verifyUrl);
	}
	
	@GetMapping("/verify")
	public String customerVerification(@Param("code")String code,Model model) {
		boolean verify=customerService.verify(code);
		if(verify) {
			model.addAttribute("pageTitle","Verification Succeeded");
		}else {
			model.addAttribute("pageTitle","Verification Failed");
		}
		
		return "register/"+(verify ? "verify_success" : "verify_fail");
	}
	
	@GetMapping("/customer")
	public String viewAccountDetails(Model model,HttpServletRequest request) {
		model.addAttribute("pageTitle","Your Account Details");	
		String email=emailOfAuthenticatedCustomer(request);
		Customer customer=customerService.getCustomerbyEmail(email);
		List<Country>allCountries=customerService.allCountries();
		model.addAttribute("customer", customer);
		model.addAttribute("countries", allCountries);
		return "customer/account_form";
	}
	
	private String emailOfAuthenticatedCustomer(HttpServletRequest request) {
		Object principal=request.getUserPrincipal();
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
	
	@PostMapping("/update_customer")
	public String updateAccountDetails(Model model,Customer customer,RedirectAttributes redirectAttributes,HttpServletRequest  request) {
		customerService.update(customer);
		redirectAttributes.addFlashAttribute("message","Your account details has been updated");
		updateNameInAccountDetails(request,customer);
		return "redirect:/customer";
	}

	private void updateNameInAccountDetails(HttpServletRequest request, Customer customer) {		
		Object principal=request.getUserPrincipal();
		String fullname=customer.getFirstname()+" "+customer.getLastname();
		if(principal instanceof UsernamePasswordAuthenticationToken || 
				principal instanceof RememberMeAuthenticationToken) {
			
			CustomerUserDetails customerUserDetails=getCustomerUserDetails(principal);
			Customer customer2=customerUserDetails.getCustomer();
			customer2.setFirstname(customer.getFirstname());
			customer2.setLastname(customer.getLastname());
			
		}else if(principal instanceof OAuth2AuthenticationToken) {
			
			OAuth2AuthenticationToken oAuth2AuthenticationToken=(OAuth2AuthenticationToken) principal;
			CustomerOauthUser customerOauthUser=(CustomerOauthUser) oAuth2AuthenticationToken.getPrincipal();
			customerOauthUser.setFullName(fullname);
		}		
	}
	
	private CustomerUserDetails getCustomerUserDetails(Object principal) {
		CustomerUserDetails customerUserDetails=null;
		if(principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken authenticationToken=(UsernamePasswordAuthenticationToken) principal;
			customerUserDetails= (CustomerUserDetails) authenticationToken.getPrincipal();
		}else if(principal instanceof RememberMeAuthenticationToken){
			RememberMeAuthenticationToken token=(RememberMeAuthenticationToken) principal;
			customerUserDetails=(CustomerUserDetails) token.getPrincipal();
		}
		return customerUserDetails;
	}
}
