package com.shopme.common.entity;

import java.util.Objects;

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

	public Setting(String key) {
		this.key = key;
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

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Setting other = (Setting) obj;
		return Objects.equals(key, other.key);
	}
	
	

}
