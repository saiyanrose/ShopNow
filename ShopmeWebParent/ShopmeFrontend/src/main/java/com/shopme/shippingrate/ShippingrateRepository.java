package com.shopme.shippingrate;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

public interface ShippingrateRepository extends CrudRepository<ShippingRate,Integer> {

	public ShippingRate findByCountryAndStates(Country country,String state);
}
