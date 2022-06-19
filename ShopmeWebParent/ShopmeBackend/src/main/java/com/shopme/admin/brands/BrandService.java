package com.shopme.admin.brands;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Brand;

@Service
@Transactional
public class BrandService {

	@Autowired
	private BrandsRepository brandsRepository;
	
	public List<Brand>listAllBrand(){
		return (List<Brand>) brandsRepository.findAll();
	}
}
