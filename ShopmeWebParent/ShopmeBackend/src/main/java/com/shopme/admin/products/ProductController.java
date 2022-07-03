package com.shopme.admin.products;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.brands.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;

@Controller
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private BrandService brandService;

	@GetMapping("/products")
	public String allProducts(Model model) {
		List<Product> products=productService.findAllProduct();
		model.addAttribute("products",products);
		return "products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand>listBrands=brandService.findAll();
		Product product=new Product();
		product.setEnabled(true);
		product.setInStock(true);
		model.addAttribute("product",product);
		model.addAttribute("listBrands",listBrands);
		model.addAttribute("pageTitle","Create New Product");
		return "product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProducts(Product product,RedirectAttributes redirectAttributes) {
		productService.save(product);
		redirectAttributes.addFlashAttribute("message","Product save successfully!");
		return "redirect:/products";
	}
}
