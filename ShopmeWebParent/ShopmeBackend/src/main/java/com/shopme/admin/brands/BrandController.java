package com.shopme.admin.brands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.exceptions.BrandNotFoundException;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {
	private static final Logger LOGGER=LoggerFactory.getLogger(BrandController.class);	
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/brands")
	public String Brands(Model model) {
		LOGGER.info("Brand page called.");
		return brandByPage(1, model, "name","asc", null);		
	}
	
	@GetMapping("/brands/new")
	public String newBrand(Model model) {		
		ExecutorService executorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Future<List<Category>>listCategory=executorService.submit(()->categoryService.allCategoryForForm());	
		
		model.addAttribute("brand",new Brand());
		
		try {
			model.addAttribute("listCategory",listCategory.get());
		} catch (InterruptedException e) {			
			e.printStackTrace();
		} catch (ExecutionException e) {			
			e.printStackTrace();
		}finally {
			executorService.shutdown();
		}
		
		model.addAttribute("pageTitle","Create New brand");
		return "brands/brand_form";
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String brandByPage(@PathVariable("pageNum")int pageNum,Model model,
			@RequestParam(required=false,name="sortField",defaultValue = "id")String sortField,
			@RequestParam(required=false,name="sortDir",defaultValue = "asc")String sortDir,
			@RequestParam(required=false,name="keyword")String keyword) {		
		
		Page<Brand>listByPage=brandService.listByPage(pageNum,sortField,sortDir,keyword);
		
		List<Brand>listBrands=listByPage.getContent()
								.parallelStream()
								.collect(Collectors.toList());		
		
		long startCount=(pageNum-1)*5+1;		
		long endCount=startCount + 5-1;	
		
		if(endCount>listByPage.getTotalElements()) {
			endCount=listByPage.getTotalElements();
		}
		String reverseSort=sortDir.equals("asc") ? "desc" : "asc" ;
		
		model.addAttribute("startCount",startCount);
		model.addAttribute("pageNum",pageNum);
		model.addAttribute("endCount",endCount);
		model.addAttribute("totalItems",listByPage.getTotalElements());
		model.addAttribute("totalPage",listByPage.getTotalPages());
		model.addAttribute("brands",listBrands);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSort",reverseSort);
		model.addAttribute("keyword",keyword);
		model.addAttribute("pageTitle","Brands - Shopnow Admin");
		return "brands/brands";
	}
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand,@RequestParam("image")MultipartFile file,Model model,RedirectAttributes attributes){		
		if(!file.isEmpty()) {
			String filename=StringUtils.cleanPath(file.getOriginalFilename()).replace(" ","");
			
			brand.setLogo(filename);
			Brand saveBrand=brandService.save(brand);
			
			String uploadDir="../brand-logos/" +saveBrand.getId();
			
			ExecutorService executorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			try {
				CompletableFuture<Void> cleanDirFuture = CompletableFuture.runAsync(() -> {
				    FileUploadUtil.cleanDir(uploadDir);
				}, executorService);

				CompletableFuture<Void> mainFuture = cleanDirFuture.thenRunAsync(() -> {
				    FileUploadUtil.main(uploadDir, filename, file);
				}, executorService);
				
				mainFuture.join();

			}finally {				
				executorService.shutdown();
			}			
		}else {
			brand.setLogo(null);
			brandService.save(brand);
		}
		attributes.addFlashAttribute("message","brand saved successfully!");		
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(Model model,@PathVariable("id")int id,RedirectAttributes redirectAttributes) throws BrandNotFoundException {
		
		try {
			Brand editBrand=brandService.edit(id);
			
			ExecutorService executorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			Future<List<Category>>listCategory=executorService.submit(()->categoryService.allCategoryForForm());	
			
			model.addAttribute("brand",editBrand);
			try {
				model.addAttribute("listCategory",listCategory.get());
			} catch (InterruptedException e) {				
				e.printStackTrace();
			} catch (ExecutionException e) {				
				e.printStackTrace();
			}finally {
				executorService.shutdown();
			}
			model.addAttribute("pageTitle","Edit Brand(ID: "+id+")");
			
			return "brands/brand_form";
		}catch(BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message1",e.getMessage());			
			return "redirect:/brands";
		}		
	}
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(Model model,@PathVariable("id")int id,RedirectAttributes redirectAttributes)  throws BrandNotFoundException, IOException{
		
		try {
			brandService.delete(id);
			
			String brandDir="../brand-logos/" + id;
			
			Thread thread=new Thread(()->{
				try {
					FileUploadUtil.removeDir(brandDir);
				} catch (IOException e) {					
					e.printStackTrace();
				}
			});
			thread.start();	
			
			redirectAttributes.addFlashAttribute("message","Brand Delete Successfully!");			
		}catch(BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message1",e.getMessage());			
			return "redirect:/brands";
		}
		return "redirect:/brands";
	}
}
