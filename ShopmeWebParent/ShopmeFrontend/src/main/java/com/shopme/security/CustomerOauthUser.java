package com.shopme.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomerOauthUser implements OAuth2User {
	
	private OAuth2User oAuth2User;
	private String fullname;
	
	public CustomerOauthUser(OAuth2User user) {
		this.oAuth2User=user;
	}

	@Override
	public Map<String, Object> getAttributes() {		
		return oAuth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {		
		return oAuth2User.getAuthorities();
	}

	@Override
	public String getName() {		
		return oAuth2User.getAttribute("name");
	}
	
	public String getEmail() {
		return oAuth2User.getAttribute("email");
	}
	
	public String getFullName() {
		return fullname!=null ? fullname : oAuth2User.getAttribute("name");
	}
	
	public void setFullName(String fullname) {
		this.fullname=fullname;
	}

}
