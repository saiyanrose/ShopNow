package com.shopme.admin.setting;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.States;

public interface StateRepository extends CrudRepository<States, Integer> {

	public List<States>findAllByOrderByNameAsc();
	
	@Query("SELECT s FROM States s WHERE s.country.id=?1")
	public List<States>findByCountryId(Integer countryId);
}
