package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "categories")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(length = 128, nullable = false, unique = true)
	private String name;
	@Column(length = 64, nullable = false, unique = true)
	private String alias;
	@Column(length = 128, nullable = false)
	private String image;
	private boolean enabled;

	@OneToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

	@OneToMany(mappedBy = "parent")
	@OrderBy("name asc")
	private Set<Category> children = new HashSet<>();

	public Category() {}

	public Category(int id) {
		this.id = id;
	}

	public Category(String name) {
		this.name = name;
		this.alias = name;
		this.image = "default.png";
	}

	public static Category formCategory(Category category) {
		Category cat = new Category();
		cat.setId(category.getId());
		cat.setName(category.getName());
		return cat;
	}

	public static Category formCategory(int id, String name) {
		Category cat = new Category();
		cat.setId(id);
		cat.setName(name);
		return cat;
	}

	public static Category full(Category category) {
		Category cat = new Category();
		cat.setId(category.getId());
		cat.setName(category.getName());
		cat.setEnabled(category.isEnabled());
		cat.setAlias(category.getAlias());
		cat.setImage(category.getImage());
		return cat;
	}

	public static Category full(Category category, String name) {
		Category cat = Category.full(category);
		cat.setName(name);
		return cat;
	}

	public Category(String name, Category parent) {
		this(name);
		this.parent = parent;

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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}

	@Transient
	public String getImagePath() {
		if (id == null || image == null) {
			return "/images/default-user.png";
		} else {
			//return Constants.S3_BASE_URI+ "/category-image/" + this.id + "/" + this.image;
			return "/category-image/"+this.id+"/"+this.image;
		}
	}
}
