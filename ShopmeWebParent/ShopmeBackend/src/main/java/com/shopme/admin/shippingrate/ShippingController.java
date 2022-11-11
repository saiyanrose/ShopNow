package com.shopme.admin.shippingrate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@Controller
public class ShippingController {

	@Autowired
	private ShippingrateService shippingrateService;
	
	@GetMapping("/shipping")
	public String allShippingRates(Model model) {
		return shippingRatesByPage(1,"id","asc",model,null);
	}

	@GetMapping("/shipping/page/{pageNum}")
	public String shippingRatesByPage(@PathVariable("pageNum")int pageNum,@RequestParam(required = false,name="sortField")String sortField,
			@RequestParam(required=false,name="sortDir")String sortDir,Model model,@RequestParam(required = false,name="keyword")String keyword) {
		
		if(sortDir==null) {
			sortDir="asc";
		}
		if(sortField==null) {
			sortField="id";
		}
		
		Page<ShippingRate>shippingAllByPage=shippingrateService.listShipping(pageNum,sortField,sortDir,keyword);
		List<ShippingRate>listShipping=shippingAllByPage.getContent();
		
		long startCount=(pageNum-1)*5+1;		
		long endCount=startCount + 5-1;			
		if(endCount>shippingAllByPage.getTotalElements()) {
			endCount=shippingAllByPage.getTotalElements();
		}
		
		String reverseSort=sortDir.equals("asc") ? "desc" : "asc" ;
		
		model.addAttribute("startCount",startCount);
		model.addAttribute("pageNum",pageNum);
		model.addAttribute("endCount",endCount);
		model.addAttribute("totalItems",shippingAllByPage.getTotalElements());
		model.addAttribute("totalPage",shippingAllByPage.getTotalPages());
		model.addAttribute("listShipping",listShipping);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSort",reverseSort);
		model.addAttribute("keyword",keyword);
		return "shipping_rates/shipping_rates";		
	}
	
	@GetMapping("/shipping/{id}/enabled/{status}")
	public String updateCOD(@PathVariable("id") int id,@PathVariable("status")boolean enabled,RedirectAttributes redirectAttributes) {
		try {
			shippingrateService.codSupport(id,enabled);
			String status=enabled==true ? "Enabled" : "Disabled";
			redirectAttributes.addFlashAttribute("message","shipping rate with id "+id+" "+status);
			return "redirect:/shipping";
		}catch (ShippingRateNotFoundException e) {
			redirectAttributes.addFlashAttribute("message1",e.getMessage());
			return "redirect:/shipping";
		}
	}
	
	@GetMapping("/shipping/page/delete/{id}")
	public String deleteShippingRate(@PathVariable("id")int id,RedirectAttributes redirectAttributes) {
		try {
			shippingrateService.deleteShippingRate(id);
			redirectAttributes.addFlashAttribute("message","shipping rate deleted successfully with id "+id);
			return "redirect:/shipping";
		}catch(ShippingRateNotFoundException e) {
			redirectAttributes.addFlashAttribute("message1",e.getMessage());
			return "redirect:/shipping";
		}		
	}
	
	@GetMapping("/shipping_rates/new")
	public String newShippingRate(Model model) {
		List<Country>allCountry=shippingrateService.allCountries();
		model.addAttribute("countries",allCountry);
		model.addAttribute("shippingRate",new ShippingRate());
		model.addAttribute("pageTitle","New Shipping Rate");
		return "shipping_rates/shipping_rate_form";
	}
	
	@GetMapping("/shipping/page/edit/{id}")
	public String editShippingRate(Model model,@PathVariable("id")int id,RedirectAttributes redirectAttributes){
		try {
			ShippingRate shippingRate=shippingrateService.getShippingRate(id);
			List<Country>allCountry=shippingrateService.allCountries();
			model.addAttribute("countries",allCountry);
			model.addAttribute("shippingRate", shippingRate);
			model.addAttribute("pageTitle","Edit Shipping Rate ("+id+")");
			return "shipping_rates/shipping_rate_form";
		}catch (ShippingRateNotFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/shipping";
		}		
	}
	
	@PostMapping("/shipping/save")
	public String saveShippingRate(RedirectAttributes redirectAttributes,ShippingRate shippingRate) {
		shippingrateService.save(shippingRate);
		redirectAttributes.addFlashAttribute("message","shipping rate has been save successfully.");
		return "redirect:/shipping";
	}
}
