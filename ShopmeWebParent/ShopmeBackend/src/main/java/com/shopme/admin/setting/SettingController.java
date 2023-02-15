package com.shopme.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.currency.CurrencyRepository;
import com.shopme.admin.order.OrdersController;
import com.shopme.common.entity.Constants;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.GeneralSettingBag;
import com.shopme.common.entity.Setting;

@Controller
public class SettingController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SettingController.class);
	
	@Autowired
	private SettingService settingService;

	@Autowired
	private CurrencyRepository currencyRepository;

	@GetMapping("/settings")
	public String listAll(Model model) {
		LOGGER.info("settings||listAll called.");
		List<Setting> listSettings = settingService.listAllSettings();
		List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();

		for (Setting setting : listSettings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		model.addAttribute("S3_BASE_URI",Constants.S3_BASE_URI);
		model.addAttribute("listCurrencies", listCurrencies);
		return "setting/settings";
	}

	@PostMapping("/setings/save_general")
	public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
			HttpServletRequest request, RedirectAttributes ra) throws IOException {
		LOGGER.info("settings||saveGeneralSettings called.");
		GeneralSettingBag settingBag=settingService.getGeneralSettingBag();		
		saveSiteLogo(multipartFile, settingBag);
		LOGGER.info("settings||saveSiteLogo called.");
		saveCurrencySymbol(request,settingBag);
		LOGGER.info("settings||saveCurrencySymbol called.");
		updateSettingValuesFromForm(request,settingBag.list());
		LOGGER.info("settings||updateSettingValuesFromForm called.");
		ra.addFlashAttribute("message","General settings have been saved!");
		LOGGER.info("General settings have been saved!");
		return "redirect:/settings";
	}

	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
		if (!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());	
			LOGGER.info("settings||saveSiteLogo filename "+filename);
			String value = "/site-logo/" + filename;
			settingBag.updateSiteLogo(value);
			LOGGER.info("settings||settingBag.updateSiteLogo");
			String uploadDir = "../site-logo/";
			LOGGER.info("settings||saveSiteLogo||uploadDir "+uploadDir);
			FileUploadUtil.cleanDir(uploadDir);
			LOGGER.info("settings||saveSiteLogo||FileUploadUtil.cleanDir");
			FileUploadUtil.main(uploadDir, filename, multipartFile);
			LOGGER.info("settings||saveSiteLogo||FileUploadUtil.main");
		}
	}
	
	private void saveCurrencySymbol(HttpServletRequest request,GeneralSettingBag generalSettingBag) {
		Integer currencyId=Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Optional<Currency>findById=currencyRepository.findById(currencyId);		
		if(findById.isPresent()) {
			Currency currency=findById.get();			
			generalSettingBag.updateCurrencySymbol(currency.getSymbol());
		}
	}
	
	private void updateSettingValuesFromForm(HttpServletRequest request,List<Setting> listSetting) {
		for(Setting setting : listSetting) {
			String value=request.getParameter(setting.getKey());			
			if(value!=null) {
				setting.setValue(value);
			}
		}		
		settingService.saveSetting(listSetting);
	}
	
	@PostMapping("/setings/save_mail_server")
	public String saveMailServerSetting(HttpServletRequest request,HttpServletResponse response,RedirectAttributes redirectAttributes) {
		LOGGER.info("settings||saveMailServerSetting called");
		List<Setting>mailServerSetting=settingService.mailServerSetting();
		updateSettingValuesFromForm(request, mailServerSetting);
		redirectAttributes.addFlashAttribute("message","Settings have been saved.");
		LOGGER.info("MailServerSetting have been saved.");
		return "redirect:/settings";
	}
	
	@PostMapping("/setings/save_mailTemplates")
	public String saveMailTemplatesSetting(HttpServletRequest request,HttpServletResponse response,RedirectAttributes redirectAttributes) {
		LOGGER.info("settings||saveMailTemplatesSetting called");
		List<Setting>mailTemplateSetting=settingService.mailTemplatesSetting();		
		updateSettingValuesFromForm(request, mailTemplateSetting);
		redirectAttributes.addFlashAttribute("message","Settings have been saved.");
		LOGGER.info("saveMailTemplatesSetting have been saved.");
		return "redirect:/settings";
	}
	
	@PostMapping("/settings/save_payment")
	public String savepayment(HttpServletRequest request,RedirectAttributes redirectAttributes) {
		LOGGER.info("settings||savepayment called");
		List<Setting>paymentSettings=settingService.getPaymentSettingBag();
		updateSettingValuesFromForm(request, paymentSettings);
		
		redirectAttributes.addFlashAttribute("message","Payment settings have been saved.");
		LOGGER.info("Payment settings have been saved.");
		return "redirect:/settings";
	}
}
