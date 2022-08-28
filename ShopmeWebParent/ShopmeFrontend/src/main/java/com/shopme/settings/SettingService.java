package com.shopme.settings;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@Service
public class SettingService {

	@Autowired
	private SettingRepository settingRepository;	
	
	public List<Setting> getGeneralSettingBag() {
		
		return settingRepository.findByTwoCategory(SettingCategory.GENERAL,SettingCategory.CURRENCY);
	}	
	
	
}
