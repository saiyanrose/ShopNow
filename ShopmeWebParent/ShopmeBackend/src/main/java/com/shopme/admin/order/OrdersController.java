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

import com.shopme.admin.customer.CustomerService;
import com.shopme.admin.setting.SettingService;
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
	
	
}
