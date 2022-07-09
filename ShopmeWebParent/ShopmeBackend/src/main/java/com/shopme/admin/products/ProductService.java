package com.shopme.admin.products;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.user.UserNotFoundException;
import com.shopme.common.entity.Product;

@Service
@Transactional
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	public List<Product> findAllProduct() {
		return (List<Product>) productRepository.findAll();
	}
	
	public Product save(Product product) {
		if(product.getId()==null) {
			product.setCreatedTime(new Date());
		}
		if(product.getAlias()==null || product.getAlias().isEmpty()) {
			String defaultAlias=product.getName().replaceAll(" ","-");
			product.setAlias(defaultAlias);
			
		}else {
			product.getAlias().replaceAll(" ","-");
		}
		product.setUpdatedTime(new Date());
		return productRepository.save(product);
	}
	
	public String checkunique(Integer id,String name) {
		boolean isCreatingnew=(id==null || id==0 );
		Product productByName=productRepository.findByName(name);
		if(isCreatingnew) {
			if(productByName!=null) return "Duplicate";
		}else {
			if(productByName!=null && productByName.getId()!=id) return "Duplicate";
		}
		return "OK";
	}

	public void checkEnabledStatus(Integer id, boolean status) {
		
		productRepository.updateEnabledStatus(id, status);
	}

	public void deleteProduct(Integer id) throws ProductNotFoundException {
		Long countById=productRepository.countById(id);
		if(countById==null || countById==0) {
			throw new ProductNotFoundException("No Such Product is present.");
		}
		productRepository.deleteById(id);		
	}
}
