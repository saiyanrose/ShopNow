package com.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.Utility;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.EmailSettingBag;
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
}
