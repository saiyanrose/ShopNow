package com.shopme.admin.user;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.brands.BrandsRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {

	@Autowired
	private BrandsRepository brandsRepository;
	
	@Test
	public void createBrandTest() {
		Category laptop=new Category(4);
		Brand acer=new Brand("MSI");
		acer.getCategories().add(laptop);
		Brand saveBrand=brandsRepository.save(acer);		
	}
	
	@Test
	public void createBrand2Test() {
		Category phone=new Category(6);
		Brand apple=new Brand("Apple");
		apple.getCategories().add(phone);
		Brand saveBrand=brandsRepository.save(apple);		
	}
	
	@Test
	public void createBrand3Test() {
		Category camera=new Category(6);
		Brand samsung=new Brand("JBL");
		samsung.getCategories().add(camera);
		Brand saveBrand=brandsRepository.save(samsung);		
	}
	
	@Test
	public void addCategoryInBrandstest() {
		Brand brand=brandsRepository.findById(1).get();
		System.out.println(brand.getName());
		Category category=new Category(4);
		brand.getCategories().add(category);
		Brand saveBrand=brandsRepository.save(brand);
	}
}
