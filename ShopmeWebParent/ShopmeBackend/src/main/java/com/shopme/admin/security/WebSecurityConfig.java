package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
	public shopmeUserDetailService detailService() {
		return new shopmeUserDetailService();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(detailService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}
	
	

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
		.antMatchers("/users/**").hasAuthority("Admin")
		.antMatchers("/categories/**","/brands/**").hasAnyAuthority("Admin","Editor")
		.antMatchers("/products/new","/products/delete/**").hasAnyAuthority("Admin","Editor")
		.antMatchers("/products/edit","/products/save","/products/check_unique").hasAnyAuthority("Admin","Editor","Salesperson")
		.antMatchers("/products","/products/","/products/detail/**","/products/page/**").hasAnyAuthority("Admin","Editor","Salesperson","Shipper")
		.antMatchers("/products/**").hasAnyAuthority("Admin","Editor")
		.antMatchers("/orders","/orders/","/orders/page/**","/orders/detail/**").hasAnyAuthority("Admin","Salesperson","Shipper")
		.antMatchers("/customers/**","/orders/**").hasAnyAuthority("Admin","Salesperson")
		.antMatchers("/orders_shipper/update/**").hasAuthority("Shipper")
		.anyRequest().authenticated()
		.and()
		.formLogin()
		.loginPage("/login")
		.usernameParameter("email")
		.permitAll()
		.and().logout().permitAll()
		.and().rememberMe().key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ")
		.tokenValiditySeconds(14*24*60*60)
		.userDetailsService(detailService());	
		http.headers().frameOptions().sameOrigin();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**","/js/**","/webjars/**");
	}	
}
