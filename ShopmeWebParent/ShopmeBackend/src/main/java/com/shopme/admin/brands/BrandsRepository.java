package com.shopme.admin.brands;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Brand;

public interface BrandsRepository extends PagingAndSortingRepository<Brand,Integer> {

}
