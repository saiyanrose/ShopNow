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
	public String shippingUnique(@RequestParam(required = false,name="id")Integer id,@RequestParam(name="country",required=false)Integer countryId
			,@RequestParam(name="states",required = false)String state) {		
		return shippingrateService.shippingUnique(id,countryId,state);
	}
	
	@PostMapping("/get_shipping_cost")
	public String getShippingCost(Integer productId,Integer countryId,String state)throws ShippingRateNotFoundException {
		float shippingCost=shippingrateService.calculateShippingRate(productId, countryId, state);
		return String.valueOf(shippingCost);
	}
}
