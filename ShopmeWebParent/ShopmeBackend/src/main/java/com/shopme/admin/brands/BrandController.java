package com.shopme.admin.brands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.admin.user.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/brands")
	public String Brands(Model model) {
		List<Brand>allBrands=brandService.listAllBrand();
		model.addAttribute("brands",allBrands);
		return "brands";
	}
	
	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		List<Category>listCategory=categoryService.allCategoryForForm();
		model.addAttribute("brand",new Brand());
		model.addAttribute("listCategory",listCategory);
		model.addAttribute("pageTitle","Create New brand");
		return "brand_form";
	}
}
