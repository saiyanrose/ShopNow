package com.shopme.admin.user;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryPageInfo;
import com.shopme.common.entity.Category;

@Controller
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/categories")
	public String category(Model model) {
		
		return listByPage(1, model,"id","asc",null);
	}
	
	@GetMapping("/categories/{pageNum}")
	public String listByPage(@PathVariable("pageNum")int pageNum,Model model,@RequestParam(required=false,name="sortField") String sortField,@RequestParam(required=false,name="sortDir") String sortDir,@RequestParam(required=false,name="keyword") String keyword) {
			
		if(sortField==null && sortField.isEmpty()) {
			sortField="id";
		}
		
		CategoryPageInfo categoryPageInfo=new CategoryPageInfo();		
		if(sortDir==null || sortDir.isEmpty()) {
			sortDir="asc";
		}		
		List<Category>categories=categoryService.findAllCategory(categoryPageInfo,pageNum,sortDir,keyword,sortField);		
		String reverseSort=sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("categories",categories);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("keyword",keyword);
		model.addAttribute("pageNum",pageNum);
		model.addAttribute("totalElements",categoryPageInfo.getTotalElements());
		model.addAttribute("totalPage",categoryPageInfo.getTotalPages());
		model.addAttribute("reverseSort",reverseSort);
		return "categories/categories";
	}
	
	@GetMapping("/category/{id}/enabled/{status}")
	public String categoryEnableStatus(@PathVariable("id")int id,
			@PathVariable("status")boolean enabled,RedirectAttributes redirectAttributes,Model model) {
		categoryService.checkEnabledStatus(id, enabled);
		String status=enabled? "Enabled" : "Disabled";
		String message="the category id "+id+ " has been "+status;
		redirectAttributes.addFlashAttribute("message",message);
		return "redirect:/categories";		
	}
	
	@GetMapping("/new")
	public String newCategory(Model model) {
		List<Category>formCategory=categoryService.allCategoryForForm();
		model.addAttribute("category",new Category());
		model.addAttribute("formCategory",formCategory);
		model.addAttribute("pageTitle","Create New Category");
		return "categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category,@RequestParam("file")MultipartFile multipartFile,RedirectAttributes redirectAttributes) throws IOException {
		if(!multipartFile.isEmpty()) {
			String filename=StringUtils.cleanPath(multipartFile.getOriginalFilename()).replace(" ","");
			category.setImage(filename);
			Category savedCategory=categoryService.saveCategory(category);
			String uploadDir="../category-image/" +savedCategory.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.main(uploadDir, filename, multipartFile);
		}else {			
				if(category.getImage().isEmpty()) {
					category.setImage(null);	
					//categoryService.saveCategory(category);							
			}
			categoryService.saveCategory(category);	
		}
		redirectAttributes.addFlashAttribute("message", "Category Saved Successfully.");
		return "redirect:/categories";
		
	}
	
	@GetMapping("/categories/category/edit/{id}")
	public String editCategory(RedirectAttributes redirectAttributes,Model model,@PathVariable("id")int id) {
		try {			
			Category category=categoryService.getCategory(id);
			//System.out.println(category);
			List<Category>categoryForForm=categoryService.allCategoryForForm();
			//System.out.println(categoryForForm);
			model.addAttribute("formCategory",categoryForForm);
			model.addAttribute("category",category);
			model.addAttribute("pageTitle","Edit Category with id "+id);
			return "categories/category_form";
		}catch(Exception e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/categories";
		}		
	}
	
	@GetMapping("/categories/category/delete/{id}")
	public String deleteCategory(RedirectAttributes redirectAttributes,Model model,@PathVariable("id")int id) {
		try {
			categoryService.deleteCategory(id);
			redirectAttributes.addFlashAttribute("message", "Category Delete Successfully.");
		}catch(CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/categories";
	}
}
