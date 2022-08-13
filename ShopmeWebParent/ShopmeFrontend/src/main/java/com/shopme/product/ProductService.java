package com.shopme.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;

@Service
public class ProductService {

	public static final int Product_per_page=10;
	
	@Autowired
	private ProductRepository productRepository;
	
	public Page<Product>listByCategory(int pageNum,Integer categoryId){			
		Pageable pageable=PageRequest.of(pageNum - 1,Product_per_page);		
		return productRepository.listByCategory(categoryId, pageable);
	}
	
	public Product getProduct(String alias) throws ProductNotFoundException {
		Product product=productRepository.findByAlias(alias);
		if(product==null) {
			throw new  ProductNotFoundException("No Product Available with name" +alias);
		}
		return product;
	}
}
