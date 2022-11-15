package com.shopme.admin.products;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.brands.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@RestController
public class ProductRestController {

	@Autowired
	private BrandService brandService;
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/brands/{id}/categories")
	public List<CategoryDto>listCategoriesByBrand(@PathVariable("id")Integer id) throws BrandNotFoundRestException{
		List<CategoryDto>categoryDtos=new ArrayList<>();
		try {
			Brand brand=brandService.getById(id);			
			Set<Category>categories=brand.getCategories();
			for(Category cat:categories) {
				CategoryDto categoryDto=new CategoryDto(cat.getId(),cat.getName());
				categoryDtos.add(categoryDto);
			}
			return categoryDtos;
		}catch (Exception ex) {
			throw new BrandNotFoundRestException();
		}
		
	}
	
	@PostMapping("/products/check_unique")
	public String checkUnique(@RequestParam("id")Integer id,@RequestParam("name")String name) {
		return productService.checkunique(id, name);
	}
	
}
