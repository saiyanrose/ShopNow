package com.shopme;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.shopme.common.entity.EmailSettingBag;

public class Utility {

	public static String getSiteUrl(HttpServletRequest request) {
		String siteUrl=request.getRequestURL().toString();
		return siteUrl.replace(request.getServletPath(),"");
	}
	
	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settingBag) {
		JavaMailSenderImpl mailSender=new JavaMailSenderImpl();
		mailSender.setHost(settingBag.getHost());
		mailSender.setPort(Integer.parseInt(settingBag.getPort()));
		mailSender.setUsername(settingBag.getUsername());
		mailSender.setPassword(settingBag.getPassword());
		
		Properties mailProperties=new Properties();
		mailProperties.setProperty("mail.smtp.auth",settingBag.getSmtpAuth());
		mailProperties.setProperty("mail.smtp.starttls.enable",settingBag.getSmtpSecured());
		
		mailSender.setJavaMailProperties(mailProperties);
		return mailSender;
	}
}
