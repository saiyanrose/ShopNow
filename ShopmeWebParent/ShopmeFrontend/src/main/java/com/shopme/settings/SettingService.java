package com.shopme.settings;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Currency;
import com.shopme.common.entity.CurrencySettingBag;
import com.shopme.common.entity.EmailSettingBag;
import com.shopme.common.entity.PaymentSettingBag;
import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@Service
public class SettingService {

	@Autowired
	private SettingRepository settingRepository;	
	
	@Autowired
	private CurrencyRepository currencyRepository;
	
	public List<Setting> getGeneralSettingBag() {
		
		return settingRepository.findByTwoCategory(SettingCategory.GENERAL,SettingCategory.CURRENCY);
	}
	
	public EmailSettingBag getEmailSettings() {
		List<Setting>mailSettings=settingRepository.findBySettingCategory(SettingCategory.MAIL_SERVER);
		mailSettings.addAll(settingRepository.findBySettingCategory(SettingCategory.MAIL_TEMPLATES));
		return new EmailSettingBag(mailSettings);
	}
	
	public CurrencySettingBag getCurrencySettings() {
		List<Setting>settings=settingRepository.findBySettingCategory(SettingCategory.CURRENCY);		
		return new CurrencySettingBag(settings);
	}	
	
	public PaymentSettingBag getPaymentSettings() {
		List<Setting>settings=settingRepository.findBySettingCategory(SettingCategory.PAYMENT);		
		return new PaymentSettingBag(settings);
	}
	
	public String getCurrencyCode() {
		Setting setting= settingRepository.findByKey("CURRENCY_ID");
		Integer currencyId=Integer.parseInt(setting.getValue());
		Currency currency=currencyRepository.findById(currencyId).get();
		return currency.getCode();
	}
	
}
