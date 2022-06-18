package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repository;
	
	//for test spring data jpa
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateUser() {
		Role adminRole=entityManager.find(Role.class,5);
		User user=new User("sunil@gmail.com","sonu","sunil","sikiligar");
		user.addRole(adminRole);
		user.setEnabled(true);
		User saveUser=repository.save(user);
		assertThat(saveUser.getId()).isGreaterThan(0);
	}
	
	//list of all users
	@Test
	public void testListAllUser() {
		Iterable<User>listUsers=repository.findAll();
		listUsers.forEach(user->System.out.println(user));
	}
	
	//by id
	@Test
	public void testUserById() {
		User user=repository.findById(1).get();
		System.out.println(user);
		assertThat(user).isNotNull();
	}
	
	//update user
	@Test
	public void testUpdateUserById() {
		User user=repository.findById(1).get();
		user.setEnabled(true);
		repository.save(user);
		System.out.println(user);
		
	}
	
	//getUserByEmail
	@Test
	public void testGetByEmail() {
		String email="piyushmalik08@gmail.com";
		User user=repository.getUserByEmail(email);
		assertThat(user).isNotNull();
	}
	
	//enabled disabled
	@Test
	public void testEnabledStatus() {
		Integer id=1;
		repository.updateEnabledStatus(id, true);
	}
	
	//pagination
	@Test
	public void pageTest() {
		int pageNumber=0;
		int pageSize=1;
		Pageable pageable=PageRequest.of(pageNumber, pageSize);		
		Page<User> page=repository.findAll(pageable);
		System.out.println("page " +page);
		List<User>content=page.getContent();		
		content.forEach(user->System.out.println(user));
		assertThat(content.size()).isEqualTo(pageSize);
	}
	
	//search
	@Test
	public void searchTest() {
		String keyword ="chanchal";
		int pageNumber=0;
		int pageSize=1;
		Pageable pageable=PageRequest.of(pageNumber, pageSize);		
		Page<User> page=repository.findAll(keyword,pageable);		
		List<User>content=page.getContent();
		content.forEach(user->System.out.println(user));
		assertThat(content.size()).isGreaterThan(0);
	}
	
}
