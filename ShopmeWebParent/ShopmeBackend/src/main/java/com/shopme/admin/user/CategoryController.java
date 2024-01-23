package com.shopme.admin.user;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import com.shopme.admin.exceptions.CategoryNotFoundException;
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
	public String listByPage(@PathVariable("pageNum")int pageNum,Model model,
			@RequestParam(required=false,name="sortField",defaultValue = "id") String sortField,
			@RequestParam(required=false,name="sortDir",defaultValue = "asc") String sortDir,
			@RequestParam(required=false,name="keyword") String keyword) {		
		
		CategoryPageInfo categoryPageInfo=new CategoryPageInfo();		
		
		ExecutorService executorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Future<List<Category>>categories=
				executorService.submit(()->
					categoryService.findAllCategory(categoryPageInfo,pageNum,sortDir,keyword,sortField)
		);				
		
		String reverseSort=sortDir.equals("asc") ? "desc" : "asc";
		
		try {
			model.addAttribute("categories",categories.get());
		} catch (InterruptedException | ExecutionException e) {			
			e.printStackTrace();
		}finally {
			executorService.shutdown();
		}
		
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("keyword",keyword);
		model.addAttribute("pageNum",pageNum);
		model.addAttribute("totalElements",categoryPageInfo.getTotalElements());
		model.addAttribute("totalPage",categoryPageInfo.getTotalPages());
		model.addAttribute("reverseSort",reverseSort);
		model.addAttribute("pageTitle","Categories - ShopNow Admin");
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
		ExecutorService executorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Future<List<Category>>formCategory=
				executorService.submit(()->
				categoryService.allCategoryForForm()
		);			
		
		model.addAttribute("category",new Category());
		
		try {
			model.addAttribute("formCategory",formCategory.get());
		} catch (InterruptedException e) {			
			e.printStackTrace();
		} catch (ExecutionException e) {			
			e.printStackTrace();
		}finally {
			executorService.shutdown();
		}
		
		model.addAttribute("pageTitle","Create New Category");
		
		return "categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category,@RequestParam("file")MultipartFile multipartFile,RedirectAttributes redirectAttributes){		
		if(!multipartFile.isEmpty()) {
			String filename=StringUtils.cleanPath(multipartFile.getOriginalFilename()).replace(" ","");
			
			category.setImage(filename);
			
			Category savedCategory=categoryService.saveCategory(category);
			
			String uploadDir="../category-image/" +savedCategory.getId();
			

			ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			
			try {
				CompletableFuture<Void> cleanDirFuture = CompletableFuture.runAsync(() -> {
				    FileUploadUtil.cleanDir(uploadDir);
				}, executorService);

				CompletableFuture<Void> mainFuture = cleanDirFuture.thenRunAsync(() -> {
				    FileUploadUtil.main(uploadDir, filename, multipartFile);
				}, executorService);
				
				mainFuture.join();

			}finally {				
				executorService.shutdown();
			}			
		}else {			
				if(category.getImage().isEmpty()) {
					category.setImage(null);											
				}
			categoryService.saveCategory(category);				
		}
		redirectAttributes.addFlashAttribute("message", "Category Saved Successfully.");		
		return "redirect:/categories";		
	}
	
	@GetMapping("/categories/category/edit/{id}")
	public String editCategory(RedirectAttributes redirectAttributes,Model model,@PathVariable("id")int id) {		
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try {				
			Future<Category>category=executorService.submit(()->categoryService.getCategory(id));		
			Future<List<Category>>categoryForForm=executorService.submit(()->categoryService.allCategoryForForm());			
			
			model.addAttribute("formCategory",categoryForForm.get());
			model.addAttribute("category",category.get());
			model.addAttribute("pageTitle","Edit Category with id "+id);
			return "categories/category_form";
		}catch(Exception e) {			
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/categories";
		}finally {
			executorService.shutdown();
		}		
	}
	
	@GetMapping("/categories/category/delete/{id}")
	public String deleteCategory(RedirectAttributes redirectAttributes,Model model,@PathVariable("id")int id){		
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try {
			categoryService.deleteCategory(id);			
			String deleteDir="../category-image/" +id;
			executorService.submit(()->{
				try {
					FileUploadUtil.removeDir(deleteDir);
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}) ;	
			
			redirectAttributes.addFlashAttribute("message", "Category Delete Successfully.");
		}catch(CategoryNotFoundException e) {			
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}finally {
			executorService.shutdown();
		}
		return "redirect:/categories";
	}
}
