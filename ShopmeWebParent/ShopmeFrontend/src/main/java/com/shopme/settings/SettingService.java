package com.shopme.settings;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.EmailSettingBag;
import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@Service
public class SettingService {

	@Autowired
	private SettingRepository settingRepository;	
	
	public List<Setting> getGeneralSettingBag() {
		
		return settingRepository.findByTwoCategory(SettingCategory.GENERAL,SettingCategory.CURRENCY);
	}
	
	public EmailSettingBag getEmailSettings() {
		List<Setting>mailSettings=settingRepository.findBySettingCategory(SettingCategory.MAIL_SERVER);
		mailSettings.addAll(settingRepository.findBySettingCategory(SettingCategory.MAIL_TEMPLATES));
		return new EmailSettingBag(mailSettings);
	}
	
	
}
