package com.shopme.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.User;

public class shopmeUserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user=repository.getUserByEmail(email);
		
		if(user!=null) {
			return new ShopmeUserDetails(user);
		}else {
			throw new UsernameNotFoundException("User Not Found: " +email);
		}		
	}

}
