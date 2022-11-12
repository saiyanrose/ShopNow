package com.shopme.admin.user;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.User;

@Controller
public class AccountController {

	@Autowired
	private UserService service;
	
	//@AuthenticationPrincipal argument should be resolved to the current user rather than a user that might be edited on a form.
	@GetMapping("/account")
	public String viewDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser,Model model) {
		String email=loggedUser.getUsername();
		User user =service.getByEmail(email);
		model.addAttribute("user",user);
		return "account/account_form";
	}
	
	@PostMapping("/account/update")
	public String saveUserAccount(User user, RedirectAttributes redirectAttributes,@RequestParam("image") MultipartFile multiPartfile,@AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {
		if(!multiPartfile.isEmpty()) {
			String filename=StringUtils.cleanPath(multiPartfile.getOriginalFilename()).replace(" ","");						
			user.setPhotos(filename);			
			User saveUser=service.updateAccount(user);
			String uploadDir="user-photos/" +saveUser.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.main(uploadDir, filename, multiPartfile);
		}else {
			if(user.getPhotos().isEmpty()) user.setPhotos(null);					
				service.updateAccount(user);						
		}
		redirectAttributes.addFlashAttribute("message", "User update Successfully.");
		return "redirect:/account";
	}

}
