package com.shopme.customer;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.EmailSettingBag;
import com.shopme.settings.SettingService;

@Controller
public class ForgetPasswordController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SettingService settingService;

	@GetMapping("/forget_password")
	public String showRequestForm(Model model) {
		model.addAttribute("pageTitle","Forgot Password");
		return "customer/forget_password_form";
	}

	@PostMapping("/forgot-password")
	public String processReqForm(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		try {
			String token = customerService.updateResetPasswordToken(email);			
			String siteUrl=Utility.getSiteUrl(request) + "/reset_password?token="+ token;
			System.out.println(siteUrl);
			sendEmail(siteUrl, email);			
			model.addAttribute("message","We have sent a reset password link to your email."+"Please check your email.");
			model.addAttribute("pageTitle","Forgot Password");
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		return "customer/forget_password_form";
	}

	private void sendEmail(String siteUrl,String email) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		String toAddress=email.replace(" ","").trim();
		String subject="Here's the link to reset your password";
		String content="<p>Hello</p>"
						+"<p>You have requested to reset your password.</p>"
						+"<p>Click the link below to reset your password:</p>"
						+"<p><a href=\""+ siteUrl + "\">Change my password</a></p>";
		
		MimeMessage sendMessage=mailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(sendMessage);
		helper.setFrom(emailSettings.getFromAddress(),emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		helper.setText(content,true);
		mailSender.send(sendMessage);
	}
	
	@GetMapping("/reset_password")
	public String showResetForm(@Param("token")String token,Model model) {
		Customer customer=customerService.getByResetPasswordToken(token);
		model.addAttribute("pageTitle","Reset Password");
		if(customer!=null) {
			model.addAttribute("token",token);
		}else {
			model.addAttribute("message","Invalid Token");
			model.addAttribute("pageTitle","Invalid Token");
			return "message";
		}
		return "customer/reset_password_form";
	}
	
	@PostMapping("/reset-password")
	public String resetPassword(HttpServletRequest request,Model model) {
		String token=request.getParameter("token");
		String password=request.getParameter("password");
		try {			
			customerService.updatePassword(token, password);
			model.addAttribute("message","Reset your password");
			model.addAttribute("message","Password change successfully");
			return "message";
		} catch (CustomerNotFoundException e) {
			model.addAttribute("message",e.getMessage());
			model.addAttribute("pageTitle","Invalid Token");
			return "message";
		}
		
	}
}
