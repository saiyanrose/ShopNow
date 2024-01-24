package com.shopme.admin.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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
import com.shopme.admin.exceptions.UserNotFoundException;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService service;

	@GetMapping("/users")
	public String listAll(Model model) {
		LOGGER.info("List of Users page called.");
		return listByPage(1, model, "firstname", "asc", null);
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@RequestParam(required = false, name = "sortField",defaultValue = "firstname") String sortField,
			@RequestParam(required = false, name = "sortDir",defaultValue = "asc") String sortDir,
			@RequestParam(required = false, name = "keyword") String keyword) {

		Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);

		List<User> listUsers = page
		        .getContent()
		        .parallelStream()
		        .collect(Collectors.toList());

		long startCount = (pageNum - 1) * UserService.User_Per_Page + 1;
		long endCount = startCount + UserService.User_Per_Page - 1;

		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		String reverseSort = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("startCount", startCount);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPage", page.getTotalPages());
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSort", reverseSort);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageTitle", "Users - ShopNow Admin");
		return "users/users";
	}

	@GetMapping("/users/new")
	public String newUser(Model model) {
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Future<List<Role>> listRole = executorService.submit(() -> service.listRole());

		List<Role> allRoles = new ArrayList<>();

		try {
			allRoles=listRole.get()
					.parallelStream().collect(Collectors.toList());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}finally {
			executorService.shutdown();
		}
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRole", allRoles);
		model.addAttribute("pageTitle", "Create New User");
		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multiPartfile) throws IOException {
		if (!multiPartfile.isEmpty()) {
			String filename = StringUtils.cleanPath(multiPartfile.getOriginalFilename()).replace(" ", "");

			user.setPhotos(filename);
			User saveUser = service.save(user);			

			String uploadDir = "user-photos/" + saveUser.getId();
			
			ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			
			try {
				CompletableFuture<Void> cleanDirFuture = CompletableFuture.runAsync(() -> {
				    FileUploadUtil.cleanDir(uploadDir);
				}, executorService);

				CompletableFuture<Void> mainFuture = cleanDirFuture.thenRunAsync(() -> {
				    FileUploadUtil.main(uploadDir, filename, multiPartfile);
				}, executorService);
				
				mainFuture.join();

			}finally {				
				executorService.shutdown();
			}			
		} else {
			if (user.getPhotos().isEmpty()) {
				user.setPhotos(null);
			}
			service.save(user);
		}
		redirectAttributes.addFlashAttribute("message", "User Saved Successfully.");
		return "redirect:/users";
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model)
			throws InterruptedException, ExecutionException {
		List<Role> allRoles = new ArrayList<>();
		User user = new User();
		
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try{
			Future<List<Role>> listRole = executorService.submit(() -> service.listRole());
			allRoles = listRole.get().parallelStream().collect(Collectors.toList());
		}catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		finally {
			executorService.shutdown();
		}		
		try {			
			user =service.get(id);
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
			model.addAttribute("listRole", allRoles);
			return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());			
			return "redirect:/users";
		}
	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model)
			throws IOException {		
		try {
			service.delete(id);	
			
			String deleteDir = "user-photos/" + id;
			
			Thread thread=new Thread(()->{				
				try {
					FileUploadUtil.removeDir(deleteDir);
				} catch (IOException e) {					
					e.printStackTrace();
				}
			});
			thread.start();						

			redirectAttributes.addFlashAttribute("message", "User Delete Successfully.");
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());			
		}
		return "redirect:/users";
	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String checkEnabledUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model, @PathVariable(name = "status") boolean enabled) {		
		
		service.checkEnabledStatus(id, enabled);		
		
		String status = enabled ? "Enabled" : "Disabled";
		String message = "the user id " + id + " has been " + status;
		
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/users";
	}

}
