package com.shopme.common.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true, length = 256, nullable = false)
	private String name;

	@Column(unique = true, length = 256, nullable = false)
	private String alias;

	@Column(length = 512, nullable = false)
	private String shortDescription;

	@Column(length = 4000, nullable = false)
	private String fullDescription;

	@Column(name = "created_time")
	private Date createdTime;

	@Column(name = "updated_time")
	private Date updatedTime;

	private boolean enabled;

	@Column(name = "in_stock")
	private boolean inStock;

	private float cost;
	private float price;

	@Column(name = "discount_percent")
	private float discountPrice;

	private float length;
	private float width;
	private float heigth;
	private float weight;

	@Column(name = "main_image", nullable = false)
	private String mainImage;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)//key on other side
	private Set<ProductImages> images = new HashSet<>();
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne
	@JoinColumn(name = "brand_id")
	private Brand brand;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)//key on other side
	private List<ProductDetails> details = new ArrayList<>();
	
	public Product() {}

	public Product(String name) {		
		this.name = name;
	}

	public Product(Integer id) {
		this.id = id;
	}

	public Set<ProductImages> getImages() {
		return images;
	}

	public void setImages(Set<ProductImages> images) {
		this.images = images;
	}

	public String getMainImage() {
		return mainImage;
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isInStock() {
		return inStock;
	}

	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(float discountPrice) {
		this.discountPrice = discountPrice;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeigth() {
		return heigth;
	}

	public void setHeigth(float heigth) {
		this.heigth = heigth;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public void addExtraImage(String imageName) {
		this.images.add(new ProductImages(imageName, this));
	}

	@Transient
	public String getMainImagePath() {
		if (id == null || mainImage == null)
			return "/images/image-thumbnail.png";
		else {
			//return Constants.S3_BASE_URI+"/product-images/" + this.id + "/" + this.mainImage;
			return "/product-images/" + this.id + "/" + this.mainImage;
		}
		
	}

	public List<ProductDetails> getDetails() {
		return details;
	}

	public void setDetails(List<ProductDetails> details) {
		this.details = details;
	}

	public void addDetail(String name, String value) {
		this.details.add(new ProductDetails(name, value, this));
	}

	public void addDetail(Integer id, String name, String value) {
		this.details.add(new ProductDetails(id, name, value, this));
	}

	public boolean containsImageName(String filename) {
		Iterator<ProductImages> product_images = images.iterator();
		while (product_images.hasNext()) {
			ProductImages img = product_images.next();
			if (img.getName().equals(filename)) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public float getDiscountPrize() {
		if (discountPrice > 0) {
			return price * ((100 - discountPrice) / 100);
		}
		return this.price;
	}

}
