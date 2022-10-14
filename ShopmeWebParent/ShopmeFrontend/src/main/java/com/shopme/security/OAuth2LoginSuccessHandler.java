package com.shopme.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
	
	@Autowired
	@Lazy
	private CustomerService customerService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		
		CustomerOauthUser oauthUser=(CustomerOauthUser) authentication.getPrincipal();	
		
		String name=oauthUser.getName();
		String email=oauthUser.getEmail();
		String countryCode=request.getLocale().getCountry();
		
		System.out.println("OAuth2LoginSuccessHandler: "+name+" "+email+" "+countryCode);
		
		Customer customer=customerService.getCustomerbyEmail(email);
		
		if(customer==null) {
			customerService.addCustomerUponOauthLogin(name,email,countryCode);
		}else {
			customerService.updateAuthenticationType(customer,AuthenticationType.GOOGLE);
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
