package com.shopme.admin.brands;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;

public interface BrandsRepository extends PagingAndSortingRepository<Brand,Integer> {
	
	public Long countById(Integer id);
	
	@Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
	public Page<Brand>findAll(String keyword,Pageable pageable);
	
	
}
