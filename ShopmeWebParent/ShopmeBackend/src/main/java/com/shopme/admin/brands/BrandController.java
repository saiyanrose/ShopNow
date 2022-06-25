package com.shopme.admin.brands;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/brands")
	public String Brands(Model model) {
		return brandByPage(1, model, "name","asc", null);		
	}
	
	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		List<Category>listCategory=categoryService.allCategoryForForm();
		model.addAttribute("brand",new Brand());
		model.addAttribute("listCategory",listCategory);
		model.addAttribute("pageTitle","Create New brand");
		return "brand_form";
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String brandByPage(@PathVariable("pageNum")int pageNum,Model model,@Param("sortField")String sortField,
			@Param("sortDir")String sortDir,@Param("keyword")String keyword) {
		Page<Brand>listByPage=brandService.listByPage(pageNum,sortField,sortDir,keyword);
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
		return "brands";
	}
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand,@RequestParam("image")MultipartFile file,Model model,RedirectAttributes attributes) throws IOException {
		if(!file.isEmpty()) {
			String filename=StringUtils.cleanPath(file.getOriginalFilename()).replace(" ","");
			brand.setLogo(filename);
			Brand saveBrand=brandService.save(brand);
			String uploadDir="../brand-logos/" +saveBrand.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.main(uploadDir, filename, file);
		}else {
			brandService.save(brand);
		}
		attributes.addFlashAttribute("message","brand saved successfully!");
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editBrand(Model model,@PathVariable("id")int id,RedirectAttributes redirectAttributes) throws BrandNotFoundException {
		try {
			Brand editBrand=brandService.edit(id);
			List<Category>listCategory=categoryService.allCategoryForForm();
			model.addAttribute("brand",editBrand);
			model.addAttribute("listCategory",listCategory);
			model.addAttribute("pageTitle","Edit Brand(ID: "+id+")");
			return "brand_form";
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
			FileUploadUtil.removeDir(brandDir);
			redirectAttributes.addFlashAttribute("message","Brand Delete Successfully!");
		}catch(BrandNotFoundException e) {
			redirectAttributes.addFlashAttribute("message1",e.getMessage());
			return "redirect:/brands";
		}
		return "redirect:/brands";
	}
}
