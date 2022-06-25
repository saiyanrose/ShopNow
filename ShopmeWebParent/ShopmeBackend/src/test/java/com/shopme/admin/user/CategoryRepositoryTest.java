package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.category.CategoryRepository;
import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {

	@Autowired
	CategoryRepository repository;
	
	@Test
	public void categoryTest() {
		//add category
		Category category=new Category("Electronics");
		Category saveCategory=repository.save(category);
		assertThat(saveCategory.getId()).isGreaterThan(0);
	}
	
	@Test
	public void subCategoryTest() {
		Category category=new Category(2);
		Category subCategory=new Category("headphones", category);
		//Category components=new Category("Smart Phones", category);
		//Category saveCategory=repository.save(subCategory);
		repository.save(subCategory);
	}
	
	@Test
	public void childrenCategoryTest() {
		Category category=repository.findById(1).get();
		System.out.println(category.getName());
		Set<Category>child=category.getChildren();
		for(Category subCategory:child) {
			System.out.println(subCategory.getName());
		}
	}
	@Test
	public void primeCategoryTest() {
		List<Category> category=(List<Category>) repository.findAll();		
		for(Category category2:category) {
			if(category2.getParent()==null) {
				System.out.println(category2.getName());
				Set<Category>child=category2.getChildren();
				for(Category subCategory:child) {
					System.out.println("--"+subCategory.getName());
					parentChildren(subCategory,1);
				}
			}
		}
	}
	@Test
	public void parentChildren(Category parent,int sublevel) {
		int newLevel=sublevel+1;
		Set<Category>children=parent.getChildren();		
		for(Category subCategory:children) {			
			for(int i=0;i<newLevel;i++) {
				System.out.print("--");				
			}
			System.out.println(subCategory.getName());
			parentChildren(subCategory,newLevel);
		}
		
	}
	
	@Test
	public void checkEnabledStatus() {
		int id=1;
		repository.updateEnabledStatus(id,true);
	}
	/*
	@Test
	public void listRootCategories() {
		List<Category>rootCategories=repository.listRootCategories();
		for(Category c:rootCategories) {
			System.out.println(c.getName());
		}
	}
	*/
}
