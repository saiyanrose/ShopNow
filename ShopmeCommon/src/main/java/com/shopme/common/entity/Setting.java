package com.shopme.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
public class Setting {
	@Id
	@Column(name = "`key`", nullable = false, length = 128)
	private String key;

	@Column(nullable = false, length = 1024)
	private String value;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 45)
	private SettingCategory settingCategory;

	public Setting() {

	}

	public Setting(String key, String value, SettingCategory settingCategory) {
		this.key = key;
		this.value = value;
		this.settingCategory = settingCategory;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public SettingCategory getSettingCategory() {
		return settingCategory;
	}

	public void setSettingCategory(SettingCategory settingCategory) {
		this.settingCategory = settingCategory;
	}

}
