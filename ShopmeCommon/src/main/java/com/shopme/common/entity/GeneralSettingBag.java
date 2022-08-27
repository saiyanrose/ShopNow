package com.shopme.common.entity;

import java.util.List;

public class GeneralSettingBag extends SettingBag{

	public GeneralSettingBag(List<Setting> listSetting) {
		super(listSetting);		
	}
	
	public void updateCurrencySymbol(String value) {
		super.update("CURRENCY_SYMBOL", value);
	}
	
	public void updateSiteLogo(String value) {
		super.update("SITE_LOGO", value);
	}

}
