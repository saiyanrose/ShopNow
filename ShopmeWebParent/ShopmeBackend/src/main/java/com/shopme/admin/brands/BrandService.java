package com.shopme.admin.brands;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

	public Brand save(Brand brand) {		
		return brandsRepository.save(brand);
	}

	public Brand edit(int id) throws BrandNotFoundException {
		try {
			return brandsRepository.findById(id).get();
		}catch(NoSuchElementException e) {
			throw new BrandNotFoundException("Could Not Found Brand with id "+id);
		}	
		
	}

	public void delete(int id) throws BrandNotFoundException {
		Long count=brandsRepository.countById(id);
		if(count==null || count==0) {
			throw new BrandNotFoundException("Could Not Delete Brand with id "+id);
		}
		brandsRepository.deleteById(id);
		
	}

	public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort=Sort.by(sortField);
		sort=sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable=PageRequest.of(pageNum-1,5,sort);
		if(keyword!=null) {
			return brandsRepository.findAll(keyword, pageable);
		}
		return brandsRepository.findAll(pageable);
	}

	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew=(id==null || id==0);
		Brand findBrand=brandsRepository.findByName(name);
		if(isCreatingNew) {
			if(findBrand!=null)return "Duplicate";
		}else {
			if(findBrand!=null && findBrand.getId()!=id) {
				return "Duplicate";
			}
		}
		return "OK";
	}
}
