package com.shopme.admin.brands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandRestController {
	@Autowired
	private BrandService brandService;

	@PostMapping("/brands/check_unique")
	public String brandUnique(@RequestParam("id")int id,@RequestParam("name")String name) {
		return brandService.checkUnique(id,name);
	}
}
