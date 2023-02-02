package com.shopme.admin.user;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOGGER=LoggerFactory.getLogger(CategoryController.class);
	
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/categories")
	public String category(Model model) {
		LOGGER.info("List of Categories page called.");
		return listByPage(1, model,"id","asc",null);
	}
	
	@GetMapping("/categories/{pageNum}")
	public String listByPage(@PathVariable("pageNum")int pageNum,Model model,@RequestParam(required=false,name="sortField") String sortField,@RequestParam(required=false,name="sortDir") String sortDir,@RequestParam(required=false,name="keyword") String keyword) {
		if(sortField==null || sortField.isEmpty()) {
			sortField="id";
		}		
		CategoryPageInfo categoryPageInfo=new CategoryPageInfo();	
		
		if(sortDir==null || sortDir.isEmpty()) {
			sortDir="asc";
		}		
		
		List<Category>categories=categoryService.findAllCategory(categoryPageInfo,pageNum,sortDir,keyword,sortField);		
		LOGGER.info("list of categories from categoryservice.");
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
		LOGGER.info("Category Controller || categoryEnableStatus");
		String status=enabled? "Enabled" : "Disabled";
		String message="the category id "+id+ " has been "+status;
		LOGGER.info("Category Controller || categoryEnableStatus "+message);
		redirectAttributes.addFlashAttribute("message",message);
		return "redirect:/categories";		
	}
	
	@GetMapping("/new")
	public String newCategory(Model model) {
		LOGGER.info("Category Controller || newCategory page.");
		List<Category>formCategory=categoryService.allCategoryForForm();
		model.addAttribute("category",new Category());
		model.addAttribute("formCategory",formCategory);
		model.addAttribute("pageTitle","Create New Category");
		return "categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category,@RequestParam("file")MultipartFile multipartFile,RedirectAttributes redirectAttributes) throws IOException {
		LOGGER.info("Category Controller || saveCategory called.");
		if(!multipartFile.isEmpty()) {
			String filename=StringUtils.cleanPath(multipartFile.getOriginalFilename()).replace(" ","");
			LOGGER.info("Category Controller || saveCategory || filename "+filename);
			category.setImage(filename);
			Category savedCategory=categoryService.saveCategory(category);
			String uploadDir="../category-image/" +savedCategory.getId();
			LOGGER.info("Category Controller || saveCategory || uploadDir "+uploadDir);
			FileUploadUtil.cleanDir(uploadDir);
			LOGGER.info("Category Controller || saveCategory || clean dir method");
			FileUploadUtil.main(uploadDir, filename, multipartFile);
			LOGGER.info("Category Controller || saveCategory || upload category image");
		}else {			
				if(category.getImage().isEmpty()) {
					category.setImage(null);											
			}
			categoryService.saveCategory(category);		
			LOGGER.info("Category Controller || saveCategory || save category method called.");
		}
		redirectAttributes.addFlashAttribute("message", "Category Saved Successfully.");		
		return "redirect:/categories";
		
	}
	
	@GetMapping("/categories/category/edit/{id}")
	public String editCategory(RedirectAttributes redirectAttributes,Model model,@PathVariable("id")int id) {
		LOGGER.info("Category Controller || editCategory called.");
		try {			
			Category category=categoryService.getCategory(id);
			LOGGER.info("Category Controller || editCategory || get category from service.");
			List<Category>categoryForForm=categoryService.allCategoryForForm();
			LOGGER.info("Category Controller || editCategory || get all category from service.");
			model.addAttribute("formCategory",categoryForForm);
			model.addAttribute("category",category);
			model.addAttribute("pageTitle","Edit Category with id "+id);
			return "categories/category_form";
		}catch(Exception e) {
			LOGGER.info("Category Controller || editCategory "+e.getMessage());
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/categories";
		}		
	}
	
	@GetMapping("/categories/category/delete/{id}")
	public String deleteCategory(RedirectAttributes redirectAttributes,Model model,@PathVariable("id")int id) throws IOException {
		LOGGER.info("Category Controller || deleteCategory called.");
		try {
			categoryService.deleteCategory(id);
			LOGGER.info("Category Controller || deleteCategory from service.");
			String deleteDir="../category-image/" +id;
			FileUploadUtil.removeDir(deleteDir);
			LOGGER.info("Category Controller || deleteCategory || delete directory.");
			redirectAttributes.addFlashAttribute("message", "Category Delete Successfully.");
		}catch(CategoryNotFoundException e) {
			LOGGER.info("Category Controller || deleteCategory" +e.getMessage());
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/categories";
	}
}
