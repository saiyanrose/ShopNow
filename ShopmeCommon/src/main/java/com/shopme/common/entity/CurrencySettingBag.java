package com.shopme.common.entity;

import java.util.List;

public class CurrencySettingBag extends SettingBag{

	public CurrencySettingBag(List<Setting> listSetting) {
		super(listSetting);		
	}
	
	public String getSymbol() {
		return super.getValue("CURRENCY_SYMBOL");
	}
	
	public String getSymbolPosition() {
		return super.getValue("CURRENCY_SYMBOL_POSITION");
	}
	
	public String getDecimalPointType() {
		return super.getValue("DECIMAL_POINT_TYPE");
	}
	
	public String getThousandsPointType() {
		return super.getValue("THOUSAND_POINT_TYPE");
	}
	
	public int getDecimalDigits() {
		return Integer.parseInt(super.getValue("DECIMAL_DIGITS"));
	}

}
