package com.shopme.admin.user;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {
	private static final Logger LOGGER=LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService service;

	@GetMapping("/users")
	public String listAll(Model model) {
		LOGGER.info("Users In Admin Panel.");
		return listByPage(1,model,"firstname","asc",null);
	}
	

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name="pageNum") int pageNum,Model model,@RequestParam(required=false,name="sortField") String sortField
			,@RequestParam(required=false,name="sortDir") String sortDir,@RequestParam(required=false,name="keyword") String keyword) {
		LOGGER.info("Users In Admin Panel");
		if(sortDir==null) {
			sortDir="asc";
		}
		if(sortField==null) {
			sortField="id";
		}
		
		Page<User>page=service.listByPage(pageNum,sortField,sortDir,keyword);
		LOGGER.info("Users In Admin Panel: call users by page");
		List<User>listUsers=page.getContent();		
		long startCount=(pageNum-1)*UserService.User_Per_Page+1;
			
		long endCount=startCount + UserService.User_Per_Page-1;
		
		if(endCount>page.getTotalElements()) {
			endCount=page.getTotalElements();
		}
		String reverseSort=sortDir.equals("asc") ? "desc" : "asc" ;
		model.addAttribute("startCount",startCount);
		model.addAttribute("pageNum",pageNum);
		model.addAttribute("endCount",endCount);
		model.addAttribute("totalItems",page.getTotalElements());
		model.addAttribute("totalPage",page.getTotalPages());
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSort",reverseSort);
		model.addAttribute("keyword",keyword);
		model.addAttribute("pageTitle","Users");
		return "users/users";
	}

	@GetMapping("/users/new")
	public String newUser(Model model) {
		LOGGER.info("Users In Admin Panel:Create new user from page called.");
		List<Role> listRole = service.listRole();
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRole", listRole);
		model.addAttribute("pageTitle", "Create New User");
		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,@RequestParam("image") MultipartFile multiPartfile) throws IOException {
		LOGGER.info("Users In Admin Panel:save user");
		
		if(!multiPartfile.isEmpty()) {			
			String filename=StringUtils.cleanPath(multiPartfile.getOriginalFilename()).replace(" ","");			
			LOGGER.info("Users In Admin Panel:user photo name"+filename);			
			
			user.setPhotos(filename);
			User saveUser=service.save(user);
			
			String uploadDir="user-photos/" +saveUser.getId();
			LOGGER.info("Users In Admin Panel:user photo directory"+uploadDir);
			
			FileUploadUtil.cleanDir(uploadDir);
			LOGGER.info("Users In Admin Panel:clean directory before store user photo.");
			
			FileUploadUtil.main(uploadDir, filename, multiPartfile);
			LOGGER.info("Users In Admin Panel:upload user photo successfully.");
		}else {			
			if(user.getPhotos().isEmpty()) {				
				user.setPhotos(null);				
			}
			service.save(user);
		}
		redirectAttributes.addFlashAttribute("message", "User Saved Successfully.");
		return "redirect:/users";
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		LOGGER.info("Users In Admin Panel:update or edit user page");
		try {
			User user = service.get(id);
			List<Role> listRole = service.listRole();
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
			model.addAttribute("listRole", listRole);
			return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			LOGGER.info("Users In Admin Panel:update or edit user page"+e.getMessage());
			return "redirect:/users";
		}

	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) throws IOException {
		LOGGER.info("Users In Admin Panel:delete user");
		try {
			service.delete(id);
			LOGGER.info("Users In Admin Panel:delete user from db.");
			
			String deleteDir="user-photos/" +id;
			FileUploadUtil.removeDir(deleteDir);
			LOGGER.info("Users In Admin Panel:delete user directory delete.");
			
			redirectAttributes.addFlashAttribute("message", "User Delete Successfully.");
			
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			LOGGER.info("Users In Admin Panel:could not delete user."+e.getMessage());
		}
		return "redirect:/users";
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String checkEnabledUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model,@PathVariable(name="status") boolean enabled) {
		LOGGER.info("Users In Admin Panel:user enable/disable method called.");
		service.checkEnabledStatus(id, enabled);		
		String status=enabled ? "Enabled" : "Disabled";
		String message="the user id " +id+ " has been "+status;
		redirectAttributes.addFlashAttribute("message",message);
		LOGGER.info("Users In Admin Panel:user enable/disable: "+message);
		return "redirect:/users";
	}	
	
}
