package com.shopme.admin.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.States;

public interface StateRepository extends CrudRepository<States, Integer> {

	public List<States>findAllByOrderByNameAsc();
}
