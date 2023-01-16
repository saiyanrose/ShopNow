package com.shopme.admin.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.customer.CustomerService;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Orders;
import com.shopme.common.entity.Setting;

@Controller
public class OrdersController {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/orders")
	public String Orders(Model model,HttpServletRequest request) {
		return listByPage(model,1,"id","asc",null,request);
	}
	
	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(Model model,@PathVariable(name="pageNum") int pageNum,
			@RequestParam(required=false,name="sortField") String sortField,
			@RequestParam(required=false,name="sortDir") String sortDir,
			@RequestParam(required=false,name="keyword") String keyword,HttpServletRequest request) {	
		
		if(sortDir==null) {
			sortDir="asc";
		}
		if(sortField==null) {
			sortField="id";
		}
		
		Page<Orders>ordersBypage=orderService.allOrders(pageNum,sortField,sortDir,keyword);		
		List<Orders>listOrders=ordersBypage.getContent();	
		loadCurrency(request);
		
		long startCount=(pageNum-1)*CustomerService.CUSTOMER_PER_PAGE+1;
		
		long endCount=startCount + CustomerService.CUSTOMER_PER_PAGE-1;
		String reverseSort=sortDir.equals("asc") ? "desc" : "asc" ;
		if(endCount>ordersBypage.getTotalElements()) {
			endCount=ordersBypage.getTotalElements();
		}
		
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("pageNum",pageNum);
		model.addAttribute("startCount",startCount);
		model.addAttribute("endCount",endCount);
		model.addAttribute("totalItems",ordersBypage.getTotalElements());
		model.addAttribute("totalPage",ordersBypage.getTotalPages());
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSort",reverseSort);
		model.addAttribute("keyword",keyword);
		model.addAttribute("listOrders",listOrders);
		return "orders/orders";
	}

	private void loadCurrency(HttpServletRequest request) {
		List<Setting> currencySetting=settingService.getCurrencySettingBag();
		for(Setting setting:currencySetting) {
			request.setAttribute(setting.getKey(),setting.getValue());
		}
	}
	
	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id")Integer id,RedirectAttributes redirectAttributes,
			Model model,HttpServletRequest request) {
		
		try {
			Orders orders=orderService.get(id);
			loadCurrency(request);
			model.addAttribute("order",orders);
			return "orders/orders_detail_modal";
		}catch (OrderNotFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/orders";
		}
	}
	
	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id")Integer id,RedirectAttributes redirectAttributes) {
		try {
			orderService.deleteOrder(id);
			redirectAttributes.addFlashAttribute("message","Order deleted successfully.");
			return "redirect:/orders";
		}catch (OrderNotFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/orders";
		}
	}
	
	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id,Model model,RedirectAttributes redirectAttributes) {
		try {
			Orders orders=orderService.getOrder(id);
			List<Country>listCountries=orderService.getCountries();
			model.addAttribute("orders",orders);
			model.addAttribute("countries",listCountries);
			model.addAttribute("pageTitle","Edit Order (Id:"+id+")");
			return "orders/editOrder_form";
		}catch (OrderNotFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/orders";
		}
		
	}
	
	
}
