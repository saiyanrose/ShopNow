package com.shopme.admin.shippingrate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingrateRestController {

	@Autowired
	private ShippingrateService shippingrateService;
	
	@PostMapping("/shipping/check_unique")
	public String shippingUnique(@RequestParam(required = false,name="id")Integer id,@RequestParam("country")Integer countryId
			,@RequestParam("states")String state) {		
		return shippingrateService.shippingUnique(id,countryId,state);
	}
}
