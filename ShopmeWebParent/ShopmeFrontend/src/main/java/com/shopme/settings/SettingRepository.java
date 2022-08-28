package com.shopme.settings;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting,String> {

	public List<Setting> findBySettingCategory(SettingCategory settingCategory);
	
	@Query("SELECT s FROM Setting s WHERE s.settingCategory = ?1 OR  s.settingCategory= ?2")
	public List<Setting> findByTwoCategory(SettingCategory catOne,SettingCategory catTwo);
}
