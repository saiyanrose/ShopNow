package com.shopme.common.entity;

import java.util.List;

public class PaymentSettingBag  extends SettingBag{

	public PaymentSettingBag(List<Setting> listSetting) {
		super(listSetting);		
	}

	public String getUrlId() {
		return super.getValue("PAYPAL_API_BASE_URL");
	}
	
	public String getClientId() {
		return super.getValue("PAYPAL_API_CLIENT_ID");
	}
	
	public String getSecretId() {
		return super.getValue("PAYPAL_API_CLIENT_SECRET");
	}
}
