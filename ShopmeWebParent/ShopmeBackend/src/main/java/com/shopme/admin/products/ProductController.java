package com.shopme.admin.products;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String enableStatus(@PathVariable(name = "id") Integer id,@PathVariable(name = "status") boolean status,RedirectAttributes redirectAttributes) {
		productService.checkEnabledStatus(id,status);
		String enabled=status ? "Enabled" : "Disabled";
		String message="the user id " +id+ "has been "+enabled;
		redirectAttributes.addFlashAttribute("message",message);
		return "redirect:/products";
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id,RedirectAttributes redirectAttributes,Model model) {
		try {
			productService.deleteProduct(id);
			redirectAttributes.addFlashAttribute("message","Product delete successfully with "+id);
		}catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/products";
	}
}
