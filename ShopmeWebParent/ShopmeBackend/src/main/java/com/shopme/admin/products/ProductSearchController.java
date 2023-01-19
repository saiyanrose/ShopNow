package com.shopme.admin.products;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopme.admin.customer.CustomerService;
import com.shopme.common.entity.Product;

@Controller
public class ProductSearchController {
	@Autowired
	private ProductService productService;

	@GetMapping("/orders/search_product")
	public String showSearchProductpage() {
		return "orders/search_product";
	}
	
	@PostMapping("/orders/search_product")
	public String searchProducts(String keyword) {
		return "redirect:/orders/search_product/page/1?keyword="+keyword;
	}
	
	@GetMapping("/orders/search_product/page/{pageNum}")
	public String searchProductBypage(@RequestParam(required=false,name="keyword") String keyword,
			@PathVariable(name="pageNum") int pageNum,Model model) {
		Page<Product>products=productService.searchProducts(pageNum,keyword);
		List<Product>listProducts=products.getContent();		
		
		long startCount=(pageNum-1)*CustomerService.CUSTOMER_PER_PAGE+1;		
		long endCount=startCount + CustomerService.CUSTOMER_PER_PAGE-1;
		
		if(endCount>products.getTotalElements()) {
			endCount=products.getTotalElements();
		}		
		model.addAttribute("pageNum",pageNum);
		model.addAttribute("keyword",keyword);
		model.addAttribute("startCount",startCount);
		model.addAttribute("endCount",endCount);
		model.addAttribute("totalItems",products.getTotalElements());
		model.addAttribute("totalPage",products.getTotalPages());
		model.addAttribute("listProducts",listProducts);
		return "orders/search_product"; 
	}	
	
}
