package com.shopme.admin.products;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.shopme.admin.brands.BrandService;
import com.shopme.admin.exceptions.ProductNotFoundException;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImages;

@Controller
public class ProductController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;
	
	@Autowired
	private FileUploadUtil fileUploadUtil;

	@GetMapping("/products")
	public String allProducts(Model model) {
		List<Product> products = productService.findAllProduct().parallelStream().collect(Collectors.toList());
		model.addAttribute("products", products);
		model.addAttribute("pageTitle", "Products - Shopnow Admin");
		return "products/products";
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.findAll().parallelStream().collect(Collectors.toList());

		Product product = new Product();

		Integer numberOfExistingExtraImages = product.getImages().size();
		product.setEnabled(true);
		product.setInStock(true);

		model.addAttribute("product", product);
		model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProducts(Product product, RedirectAttributes redirectAttributes,
			@RequestParam("fileImage") MultipartFile file, @RequestParam("extraImage") MultipartFile[] img,
			@RequestParam(name = "detailName", required = false) String[] detailName,
			@RequestParam(name = "detailValue", required = false) String[] detailValue,
			@RequestParam(name = "imageIds", required = false) String[] imageIds,
			@RequestParam(name = "imageName", required = false) String[] imageName,
			@RequestParam(name = "detailIds", required = false) String[] detailIds){

		saveMainImage(file, product);

		setExistingExtraImages(imageIds, imageName, product);

		setNewExtraImage(img, product);

		setProductDetails(detailIds, detailName, detailValue, product);

		Product saveProduct = productService.save(product);
		
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try {
			CompletableFuture<Void> saveUplodedImageFuture=CompletableFuture.runAsync(() -> {
				try {
					fileUploadUtil.saveUplodedImage(file, img, saveProduct);
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}, executorService);
			
			CompletableFuture<Void> deleteExtraImagesRemoveFromFormFuture=saveUplodedImageFuture.thenRunAsync(() -> {				
					fileUploadUtil.deleteExtraImagesRemoveFromForm(product);				
			}, executorService);
			
			deleteExtraImagesRemoveFromFormFuture.join();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			executorService.shutdown();
		}		

		redirectAttributes.addFlashAttribute("message", "Product save successfully!");

		return "redirect:/products";
	}
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String enableStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean status,
			RedirectAttributes redirectAttributes) {		
		productService.checkEnabledStatus(id, status);
		
		String enabled = status ? "Enabled" : "Disabled";
		String message = "the product id " + id + " has been " + enabled;
		
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/products";
	}

	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) throws IOException {		
		try {
			productService.deleteProduct(id);
			
			String extraImageDir = "../product-images/" + id + "/extras";
			
			String productImageDir = "../product-images/" + id;
			
			ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			try {
				CompletableFuture<Void> removeDirFuture=CompletableFuture.runAsync(() -> {
					try {
						FileUploadUtil.removeDir(extraImageDir);
					} catch (IOException e) {					
						e.printStackTrace();
					}
				}, executorService);
				
				CompletableFuture<Void> removeDirFuture2=removeDirFuture.thenRunAsync(() -> {				
					try {
						FileUploadUtil.removeDir(productImageDir);
					} catch (IOException e) {						
						e.printStackTrace();
					}				
				}, executorService);	
				
				removeDirFuture2.join();				
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				executorService.shutdown();
			}			
			redirectAttributes.addFlashAttribute("message", "Product delete successfully with id: " + id);
			
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());			
		}
		return "redirect:/products";
	}

	@GetMapping("products/edit/{id}")
	public String editproduct(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {			
		CompletableFuture<List<Brand>> brandsFuture = CompletableFuture.supplyAsync(() -> brandService.listAllBrand());
		try {
			Product product = productService.getById(id);
			List<Brand> brands=null;
			try {
				brands = brandsFuture.get();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			} catch (ExecutionException e) {				
				e.printStackTrace();
			}
			Integer numberOfExistingExtraImages = product.getImages().size();
			
			model.addAttribute("product", product);
			model.addAttribute("listBrands", brands);
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
			return "products/product_form";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());			
			return "redirect:/products";
		}
	}

	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {		
		try {
			Product product = productService.getById(id);
			model.addAttribute("product", product);
			return "products/product_detail_modal";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			LOGGER.info("Products ||viewProductDetails " + e.getMessage());
			return "redirect:/products";
		}
	}
	
	private void setExistingExtraImages(String[] imageIds, String[] imageName, Product product) {
		LOGGER.error("Product||setExistingExtraImages called");

		if (imageIds == null || imageIds.length == 0)
			return;

		Set<ProductImages> product_images = new HashSet<>();

		for (int count = 0; count < imageIds.length; count++) {
			Integer id = Integer.parseInt(imageIds[count]);
			String name = imageName[count];
			product_images.add(new ProductImages(id, name, product));
		}
		product.setImages(product_images);
	}

	private void setProductDetails(String[] detailIds, String[] detailName, String[] detailValue, Product product) {
		LOGGER.info("Products || setProductDetails called.");

		if (detailName == null || detailName.length == 0)
			return;

		for (int count = 0; count < detailName.length; count++) {
			String name = detailName[count];
			String value = detailValue[count];
			Integer id = Integer.parseInt(detailIds[count]);
			if (id != 0) {
				product.addDetail(id, name, value);
			} else if (!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name, value);
			}

		}

	}	

	private void setNewExtraImage(MultipartFile[] img, Product product) {
		LOGGER.info("Products || setNewExtraImage called.");
		if (img.length > 0) {
			for (MultipartFile file : img) {
				if (!file.isEmpty()) {
					String filename = StringUtils.cleanPath(file.getOriginalFilename()).replace(" ", "");
					if (!product.containsImageName(filename)) {
						product.addExtraImage(filename);
					}
				}
			}
		}

	}

	private void saveMainImage(MultipartFile file, Product product) {
		LOGGER.info("Products || saveMainImage called.");
		if (!file.isEmpty()) {
			String filename = StringUtils.cleanPath(file.getOriginalFilename()).replace(" ", "");
			product.setMainImage(filename);
		}

	}

}
