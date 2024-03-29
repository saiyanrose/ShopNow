package com.shopme.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "product_images")
public class ProductImages {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	public ProductImages() {}

	public ProductImages(String name, Product product) {
		this.name = name;
		this.product = product;
	}

	public ProductImages(Integer id, String name, Product product) {
		this.id = id;
		this.name = name;
		this.product = product;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getImagePath() {
		if (id == null && id == 0) {
			return "/images/image-thumbnail.png";
		}else {
			//return Constants.S3_BASE_URI+"/product-images/" + product.getId() + "/extras/" + this.name;
			return "/product-images/" + product.getId() + "/extras/" + this.name;
		}
		
	}

}
