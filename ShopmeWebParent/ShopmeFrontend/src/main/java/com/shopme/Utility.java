package com.shopme;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.shopme.common.entity.CurrencySettingBag;
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
	
	public static String formatCurrency(float amount,CurrencySettingBag currencySettingBag) {
		String symbol=currencySettingBag.getSymbol();
		String symbolPosition=currencySettingBag.getSymbolPosition();
		String decimalPointType=currencySettingBag.getDecimalPointType();
		String thousandpointType=currencySettingBag.getThousandsPointType();
		int decimalDigit=currencySettingBag.getDecimalDigits();		
		
		String pattern=symbolPosition.equals("Before price") ? symbol : "";
		pattern+="###,###";
		
		if(decimalDigit>0) {
			pattern+=".";
			for(int count=1;count<=decimalDigit;count++) {
				pattern+="#";
			}
		}
		
		pattern+=symbolPosition.equals("After price") ? symbol : "";
		
		char thousandSeperator=thousandpointType.equals("COMMA") ? ',' : '.';
		char decimalSeperator=decimalPointType.equals("COMMA") ? ',' : '.';
		
		DecimalFormatSymbols decimalFormatSymbols=DecimalFormatSymbols.getInstance();
		decimalFormatSymbols.setDecimalSeparator(decimalSeperator);
		decimalFormatSymbols.setGroupingSeparator(thousandSeperator);
		DecimalFormat decimalFormat=new DecimalFormat(pattern,decimalFormatSymbols);	
		
		return decimalFormat.format(amount);
		
	}
}
