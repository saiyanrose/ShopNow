package com.shopme.admin.user;

import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.products.ProductRepository;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private EntityManager entityManager;

	@Test
	public void createProductTest() {
		Brand brand=entityManager.find(Brand.class,3);
		Category category=entityManager.find(Category.class,4);
		Product product=new Product();
		product.setName("MSI Gaming Katana GF66");
		product.setShortDescription("Intel 11th Gen. i5-11400H, 40CM FHD 144Hz Gaming Laptop (16GB/512GB NVMe SSD/Windows 10 Home/Nvidia RTX 3050Ti 4GB GDDR6/ Black/2.25Kg), 11UD-876IN");
		product.setFullDescription("Processor: 11th Generation Intel Core i5-11400H Up To 4.50 GHz."
				+ "Display: 40CM FHD (1920*1080), 144Hz 45%NTSC IPS-Level Panel."
				+ "Memory & Storage: 8GBx2 DDR4 3200MHz RAM, expandable to 64 GB | Storage: 512GB NVMe PCIe Gen3x4 SSD."
				+ "Dedicated Graphics: NVIDIA GeForce RTX 3050Ti GDDR6 4GB.");
		product.setBrand(brand);
		product.setCategory(category);
		product.setPrice(1000);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		product.setAlias("msi_gaming_katana_GF66");
		Product saveProduct=productRepository.save(product);
	}
}
