package com.shopme.admin.user;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.setting.SettingRepository;
import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingReposiotryTest {

	@Autowired
	SettingRepository settingRepository;
	
	@Test
	public void createGeneralSettings() {
		//Setting sitename=new Setting("SITE_NAME","Shopnow", SettingCategory.GENERAL);		
		Setting sitelogo=new Setting("SITE_LOGO","shopnow.png", SettingCategory.GENERAL);
		Setting sitecopyright=new Setting("COPYRIGHT","Copyright (C) 2022 Shopnow Ltd.", SettingCategory.GENERAL);
		//Setting savesite=settingRepository.save(sitename);
		settingRepository.saveAll(List.of(sitelogo,sitecopyright));
	}
	
	@Test
	public void currencySettingTest() {
		Setting currencyId=new Setting("CURRENCY_ID","1",SettingCategory.CURRENCY);
		Setting symbol=new Setting("CURRENCY_SYMBOL","$",SettingCategory.CURRENCY);
		Setting symbolPosition=new Setting("CURRENCY_SYMBOL_POSITION","before",SettingCategory.CURRENCY);
		Setting decimalPointType=new Setting("DECIMAL_POINT_TYPE","POINT",SettingCategory.CURRENCY);
		Setting decimalDigit=new Setting("DECIMAL_DIGITS","2",SettingCategory.CURRENCY);
		Setting thousandPointType=new Setting("THOUSAND_POINT_TYPE","COMMA",SettingCategory.CURRENCY);
		settingRepository.saveAll(List.of(currencyId,symbol,symbolPosition,decimalDigit,decimalPointType,thousandPointType));
	}
	
	@Test
	public void testListSettingByCategory() {
		List<Setting>listSetting=settingRepository.findBySettingCategory(SettingCategory.GENERAL); 
		listSetting.forEach(System.out::println);
	}
}
