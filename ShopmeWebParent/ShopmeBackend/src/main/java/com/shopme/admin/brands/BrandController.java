package com.shopme.admin.brands;

import java.io.IOException;
import java.util.List;

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
import com.shopme.admin.user.CategoryService;
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
		return brandByPage(1, model, "id","asc", null);		
	}
	
	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		LOGGER.info("Brands || newBrand page called.");
		List<Category>listCategory=categoryService.allCategoryForForm();
		LOGGER.info("Brands || categoryService.allCategoryForForm()");
		model.addAttribute("brand",new Brand());
		model.addAttribute("listCategory",listCategory);
		model.addAttribute("pageTitle","Create New brand");
		return "brands/brand_form";
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String brandByPage(@PathVariable("pageNum")int pageNum,Model model,@RequestParam(required=false,name="sortField")String sortField,
			@RequestParam(required=false,name="sortDir")String sortDir,@RequestParam(required=false,name="keyword")String keyword) {
		
		if(sortDir==null) {
			sortDir="asc";
		}
		if(sortField==null) {
			sortField="id";
		}
		
		Page<Brand>listByPage=brandService.listByPage(pageNum,sortField,sortDir,keyword);
		LOGGER.info("Brands || brandService.listByPage()");
		List<Brand>listBrands=listByPage.getContent();		
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
		return "brands/brands";
	}
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand,@RequestParam("image")MultipartFile file,Model model,RedirectAttributes attributes) throws IOException {
		LOGGER.info("Brands ||saveBrand called.");
		if(!file.isEmpty()) {
			String filename=StringUtils.cleanPath(file.getOriginalFilename()).replace(" ","");
			LOGGER.info("Brands ||saveBrand || filename "+filename);
			brand.setLogo(filename);
			Brand saveBrand=brandService.save(brand);
			LOGGER.info("Brands ||saveBrand||brandService.save()");
			String uploadDir="../brand-logos/" +saveBrand.getId();
			LOGGER.info("Brands ||saveBrand|| upload directory "+uploadDir);
			FileUploadUtil.cleanDir(uploadDir);
			LOGGER.info("Brands ||saveBrand|| clean directory.");
			FileUploadUtil.main(uploadDir, filename, file);
			LOGGER.info("Brands ||saveBrand|| FileUploadUtil.main()");
		}else {
			brandService.save(brand);
		}
		attributes.addFlashAttribute("message","brand saved successfully!");
		LOGGER.info("Brands ||saveBrand|| brand saved successfully!");
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(Model model,@PathVariable("id")int id,RedirectAttributes redirectAttributes) throws BrandNotFoundException {
		LOGGER.info("Brands ||editBrand called");
		try {
			Brand editBrand=brandService.edit(id);
			LOGGER.info("Brands ||editBrand ||brandService.edit(id)");
			List<Category>listCategory=categoryService.allCategoryForForm();
			LOGGER.info("Brands ||editBrand ||categoryService.allCategoryForForm()");
			model.addAttribute("brand",editBrand);
			model.addAttribute("listCategory",listCategory);
			model.addAttribute("pageTitle","Edit Brand(ID: "+id+")");
			return "brands/brand_form";
		}catch(BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message1",e.getMessage());
			LOGGER.info("Brands ||editBrand "+e.getMessage());
			return "redirect:/brands";
		}		
	}
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(Model model,@PathVariable("id")int id,RedirectAttributes redirectAttributes)  throws BrandNotFoundException, IOException{
		LOGGER.info("Brands ||deleteBrand called");
		try {
			brandService.delete(id);
			LOGGER.info("Brands ||deleteBrand ||brandService.delete(id)");
			String brandDir="../brand-logos/" + id;
			LOGGER.info("Brands ||deleteBrand"+brandDir);
			FileUploadUtil.removeDir(brandDir);
			LOGGER.info("Brands ||deleteBrand|| FileUploadUtil.removeDir(brandDir)");
			redirectAttributes.addFlashAttribute("message","Brand Delete Successfully!");
			LOGGER.info("Brands ||deleteBrand|| Brand Delete Successfully!");
		}catch(BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message1",e.getMessage());
			LOGGER.info("Brands ||deleteBrand"+e.getMessage());
			return "redirect:/brands";
		}
		return "redirect:/brands";
	}
}
