package com.shopme.admin.products;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product,Integer> {
	public Product findByName(String name);
}