package com.shopme.admin.shippingrate;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.setting.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@Service
@Transactional
public class ShippingrateService {

	@Autowired
	private ShippingrateRepository shippingrateRepository;
	
	@Autowired
	private CountryRepository countryRepository;

	public Page<ShippingRate> listShipping(Integer pageNum, String sortField, String sortDir, String keyword) {		
		Sort sort=Sort.by(sortField);
		sort=sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable=PageRequest.of(pageNum-1,5,sort);
		if(keyword!=null) {
			return shippingrateRepository.findAll(keyword, pageable);
		}
		return shippingrateRepository.findAll(pageable);		
	}
	
	public ShippingRate getShippingRate(int id) throws ShippingRateNotFoundException {
		try {
			return shippingrateRepository.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with id " + id);
		}
	}

	public void codSupport(int id, boolean enabled) throws ShippingRateNotFoundException {
		ShippingRate shippingRate=getShippingRate(id);
		if(shippingRate!=null) {
			shippingrateRepository.updateCODSupport(id, enabled);
		}else {
			throw new ShippingRateNotFoundException("Shipping rate not available with id "+id);
		}
	}

	public void deleteShippingRate(int id) throws ShippingRateNotFoundException {
		ShippingRate shippingRate=getShippingRate(id);
		if(shippingRate!=null) {
			shippingrateRepository.deleteById(id);;
		}else {
			throw new ShippingRateNotFoundException("Shipping rate not available with id "+id);
		}		
	}
	
	public List<Country>allCountries(){
		return countryRepository.findAllByOrderByNameAsc();
	}

	public String shippingUnique(Integer id, Integer countryId, String state) {		
		boolean isCreatingNew = (id == null || id == 0);
		ShippingRate shippingRate=shippingrateRepository.findByCountryAndState(countryId,state);
		if (isCreatingNew) {
			if (shippingRate != null) {
				return "Duplicate";
			}
		} else {
			if (shippingRate != null && shippingRate.getId() != id) {
				return "Duplicate";
			}			
		}
		return "OK";
	}

	public void save(ShippingRate shippingRate) {		
		shippingrateRepository.save(shippingRate);
	}
	
}
