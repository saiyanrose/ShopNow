package com.shopme;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;

@Controller
public class MainController {

	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("")
	public String viewHomePage(Model model) {
		List<Category>category=categoryService.listNoChildrenCategory();
		model.addAttribute("listCategory", category);
		model.addAttribute("pageTitle","Shopnow");
		return "index";
	}
	
	@GetMapping("/login")
	public String viewLoginPage(Model model) {
		Authentication authentication=SecurityContextHolder.getContext().getAuthentication();		
		if(authentication==null || authentication instanceof AnonymousAuthenticationToken) {
			model.addAttribute("pageTitle","Login");
			return "login";
		}
		
		return "redirect:/";
	}
}
