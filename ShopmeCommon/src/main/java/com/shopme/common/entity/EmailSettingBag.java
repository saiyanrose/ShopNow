package com.shopme.common.entity;

import java.util.List;

public class EmailSettingBag extends SettingBag{

	public EmailSettingBag(List<Setting> listSetting) {
		super(listSetting);		
	}
	
	public String getHost() {
		return super.getValue("MAIL_HOST");
	}
	
	public String getUsername() {
		return super.getValue("MAIL_USERNAME");
	}
	
	public String getPassword() {
		return super.getValue("MAIL_PASSWORD");
	}
	
	public String getSmtpAuth() {
		return super.getValue("MAIL_AUTH");
	}
	
	public String getSmtpSecured() {
		return super.getValue("MAIL_SECURED");
	}
	
	public String getFromAddress() {
		return super.getValue("MAIL_FROM");
	}
	
	public String getPort() {
		return super.getValue("MAIL_PORT");
	}
	
	public String getSenderName() {
		return super.getValue("MAIL_SENDERNAME");
	}
	
	public String getVerifySubject() {
		return super.getValue("CUSTOMER_VERIFY_SUBJECT");
	}
	
	public String getVerifyContent() {
		return super.getValue("CUSTOMER_VERIFY_CONTENT");
	}

}
