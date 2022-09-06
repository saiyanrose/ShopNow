package com.shopme.admin.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.setting.CountryRepository;
import com.shopme.admin.setting.StateRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.States;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepositoryTest {

	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private StateRepository stateRepository;
	
	@Test
	public void createCountryTest() {
		Country country=new Country("China","CN");
		countryRepository.save(country);
	}
	
	@Test
	public void createStateTest() {
		Country country=countryRepository.findById(1).get();
		States states=new States("Yunnan", country);
		stateRepository.save(states);
	}
}
