package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {
	
	public static final int User_Per_Page=4;

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder encoder;
	
	public List<User> listUsers() {
		
		return (List<User>) repository.findAll();
	}
	
	public Page<User>listByPage(int pageNum,String sortField,String sortDir,String keyword){
		Sort sort=Sort.by(sortField);
		sort=sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable=PageRequest.of(pageNum-1,User_Per_Page,sort);
		if(keyword!=null) {
			return repository.findAll(keyword, pageable);
		}
		return repository.findAll(pageable);
	}
	
	public List<Role>listRole(){
		return (List<Role>) roleRepository.findAll();
	}

	public User save(User user) {
		boolean isUpdating=(user.getId()!=null);
		if(isUpdating) {
			User existinguser=repository.findById(user.getId()).get();
			if(user.getPassword().isEmpty()) {
				user.setPassword(existinguser.getPassword());
			}else {
				encodePassword(user);
			}
		}else {
			encodePassword(user);
		}
		
		return repository.save(user);
	}
	
	private void encodePassword(User user) {
		String encodedPassword=encoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	
	public boolean isEmailUnique(String email,Integer id) {
		User userEmail=repository.getUserByEmail(email);		
		if(userEmail==null) return true;//unique
		boolean isCreatingNew=(id==null);//create new user
		if(isCreatingNew) {
			if(userEmail!=null) return false;
		}else {
			if(userEmail.getId()!=id) {
				return false;
			}
		}
		return true;
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return repository.findById(id).get();
		}catch(NoSuchElementException e) {
			throw new UserNotFoundException("No Such User is present.");
		}		
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById=repository.countById(id);
		if(countById==null || countById==0) {
			throw new UserNotFoundException("No Such User is present.");
		}
		repository.deleteById(id);
	}
	
	public void checkEnabledStatus(Integer id,boolean enabled) {
		repository.updateEnabledStatus(id, enabled);
	}
	
	public User getByEmail(String email) {
		return repository.getUserByEmail(email);
	}
	
	public User updateAccount(User userInForm) {		
		User userAccount=repository.findById(userInForm.getId()).get();		
		if(!userInForm.getPassword().isEmpty()) {
			userAccount.setPassword(userInForm.getPassword());
			encodePassword(userAccount);
		}
		if(userInForm.getPhotos()!=null) {
			userAccount.setPhotos(userInForm.getPhotos());			
		}
		userAccount.setFirstname(userInForm.getFirstname());
		userAccount.setLastname(userInForm.getLastname());
		return repository.save(userAccount);
	}
}
