package com.shopme.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.GeneralSettingBag;
import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@Service
public class SettingService {

	@Autowired
	private SettingRepository settingRepository;
	
	public List<Setting>listAllSettings(){
		return (List<Setting>) settingRepository.findAll();
	}
	
	public GeneralSettingBag getGeneralSettingBag() {
		List<Setting> generalSettings=new ArrayList<Setting>();
		List<Setting>settings=settingRepository.findBySettingCategory(SettingCategory.GENERAL);
		List<Setting>currencysettings=settingRepository.findBySettingCategory(SettingCategory.CURRENCY);
		generalSettings.addAll(settings);
		generalSettings.addAll(currencysettings);
		return new GeneralSettingBag(generalSettings);
	}
	
	public void saveSetting(Iterable<Setting> setting) {
		settingRepository.saveAll(setting);
	}
	
	public List<Setting> mailServerSetting(){
		return settingRepository.findBySettingCategory(SettingCategory.MAIL_SERVER);
	}
	
	public List<Setting> mailTemplatesSetting(){
		return settingRepository.findBySettingCategory(SettingCategory.MAIL_TEMPLATES);
	}
}
