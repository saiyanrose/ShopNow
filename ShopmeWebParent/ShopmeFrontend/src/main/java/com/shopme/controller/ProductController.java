package com.shopme.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.product.ProductService;

@Controller
public class ProductController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;
	
	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
		return viewCategory(alias, model, 1);		
	}

	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategory(@PathVariable("category_alias") String alias, Model model,
			@PathVariable("pageNum") int pageNum) {
		Category category = categoryService.getCategory(alias);		
		model.addAttribute("pageTitle", category.getName());

		if (category == null) {
			return "error/404";
		}

		List<Category> categoryParent = categoryService.getParent(category);		
		model.addAttribute("categoryParent", categoryParent);

		Page<Product> listByCategory = productService.listByCategory(pageNum, category.getId());
		
		List<Product> listProducts = listByCategory.getContent();
		
		long startCount = (pageNum - 1) * ProductService.Product_per_page + 1;// 0

		long endCount = startCount + ProductService.Product_per_page + 1;// 11

		if (endCount > listByCategory.getTotalElements()) {
			endCount = listByCategory.getTotalElements();
		}
		model.addAttribute("startCount", startCount);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", listByCategory.getTotalElements());
		model.addAttribute("totalPage", listByCategory.getTotalPages());
		model.addAttribute("listProducts",listProducts);
		model.addAttribute("category",category);
		return "product_by_category";
	}
}
