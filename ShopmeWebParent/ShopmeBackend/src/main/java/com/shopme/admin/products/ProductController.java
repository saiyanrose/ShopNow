package com.shopme.admin.products;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImages;

@Controller
public class ProductController {
	private static final Logger LOGGER=LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductService productService;

	@Autowired
	private BrandService brandService;

	@GetMapping("/products")
	public String allProducts(Model model) {
		List<Product> products = productService.findAllProduct();
		LOGGER.info("Products || allproducts");
		model.addAttribute("products", products);
		return "products/products";
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		LOGGER.info("Products || newProduct page called.");
		List<Brand> listBrands = brandService.findAll();
		LOGGER.info("Products || brandService.findAll()");
		Product product = new Product();
		Integer numberOfExistingExtraImages=product.getImages().size();
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
			@RequestParam(name="detailName",required = false) String[] detailName,@RequestParam(name="detailValue",required = false)String[] detailValue,
			@RequestParam(name="imageIds",required=false)String[] imageIds,@RequestParam(name="imageName",required=false)String[] imageName,
			@RequestParam(name="detailIds",required = false) String[] detailIds)
			throws IOException {
		LOGGER.info("Products || saveProducts called.");
		saveMainImage(file, product);//set mainImage
		setExistingExtraImages(imageIds,imageName,product);
		setNewExtraImage(img, product);
		setProductDetails(detailIds,detailName,detailValue,product);
		Product saveProduct = productService.save(product);
		LOGGER.info("Products || saveProducts||productService.save(product)");
		saveUplodedImage(file, img, saveProduct);
		deleteExtraImagesRemoveFromForm(product);
		redirectAttributes.addFlashAttribute("message", "Product save successfully!");
		LOGGER.info("Products || saveProducts||Product save successfully!");
		return "redirect:/products";
	}

	private void deleteExtraImagesRemoveFromForm(Product product) {
		LOGGER.info("Products || deleteExtraImagesRemoveFromForm called");
		String extraImageDir="../product-images/" + product.getId() + "/extras";
		LOGGER.info("Products || deleteExtraImagesRemoveFromForm extraImageDir "+extraImageDir);
		Path dirPath=Paths.get(extraImageDir); 
		try {
			Files.list(dirPath).forEach(file->{
				String filename=file.toFile().getName();
				if(!product.containsImageName(filename)) {
					try {
						Files.delete(file);
						LOGGER.info("Deleted extra image: "+filename);
					}catch(IOException e) {
						LOGGER.error("Colud not delete extra images "+filename);
					}
				}
			});
		}catch(IOException ex) {
			LOGGER.error("Colud not list directory "+dirPath);
		}
		
	}

	private void setExistingExtraImages(String[] imageIds, String[] imageName, Product product) {
		LOGGER.error("Product||setExistingExtraImages called");
		if(imageIds==null || imageIds.length==0) return;
		Set<ProductImages>product_images=new HashSet<>();
		for(int count=0;count<imageIds.length;count++) {
			Integer id=Integer.parseInt(imageIds[count]);
			String name=imageName[count];
			product_images.add(new ProductImages(id, name, product));
		}
		product.setImages(product_images);
	}

	private void setProductDetails(String[] detailIds,String[] detailName, String[] detailValue, Product product) {
		LOGGER.info("Products || setProductDetails called.");
		if(detailName==null || detailName.length==0)return;
		for (int count=0;count<detailName.length;count++) {
			String name=detailName[count];
			String value=detailValue[count];
			Integer id=Integer.parseInt(detailIds[count]);
			if(id!=0) {
				product.addDetail(id,name,value);
			}
			else if(!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name,value);
			}
			
		}
		
	}

	private void saveUplodedImage(MultipartFile file, MultipartFile[] img, Product saveProduct) throws IOException {
		LOGGER.info("Products || saveUplodedImage called");
		if (!file.isEmpty()) {
			String filename = StringUtils.cleanPath(file.getOriginalFilename()).replace(" ", "");
			LOGGER.info("Products || saveUplodedImage filename "+filename);
			String uploadDir = "../product-images/" + saveProduct.getId();
			LOGGER.info("Products || saveUplodedImage uploadDir "+uploadDir);
			FileUploadUtil.cleanDir(uploadDir);
			LOGGER.info("Products || saveUplodedImage cleanDir()");
			FileUploadUtil.main(uploadDir, filename, file);
			LOGGER.info("Products || saveUplodedImage FileUploadUtil.main()");
		}
		if (img.length > 0) {
			String uploadDir = "../product-images/" + saveProduct.getId() + "/extras";
			LOGGER.info("Products || saveUplodedImage extra uploadDir "+uploadDir);
			for (MultipartFile extraFile : img) {
				if (extraFile.isEmpty())continue;
				
				String filename = StringUtils.cleanPath(extraFile.getOriginalFilename()).replace(" ", "");
				LOGGER.info("Products || saveUplodedImage extra cleanPath "+uploadDir);
				FileUploadUtil.main(uploadDir, filename, extraFile);
				LOGGER.info("Products || saveUplodedImage extra FileUploadUtil.main");
			}
		}

	}

	private void setNewExtraImage(MultipartFile[] img, Product product) {
		LOGGER.info("Products || setNewExtraImage called.");
		if (img.length > 0) {
			for (MultipartFile file : img) {
				if (!file.isEmpty()) {
					String filename = StringUtils.cleanPath(file.getOriginalFilename()).replace(" ", "");
					LOGGER.info("Products || setNewExtraImage filename "+filename);
					if(!product.containsImageName(filename)) {
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
			LOGGER.info("Products || saveMainImage filename "+filename);
			product.setMainImage(filename);
		}

	}

	@GetMapping("/products/{id}/enabled/{status}")
	public String enableStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean status,
			RedirectAttributes redirectAttributes) {
		LOGGER.info("Products || enableStatus called");
		productService.checkEnabledStatus(id, status);
		String enabled = status ? "Enabled" : "Disabled";
		String message = "the product id " + id + " has been " + enabled;
		LOGGER.info("Products || enableStatus "+message);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/products";
	}

	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) throws IOException {
		LOGGER.info("Products || deleteProduct called");
		try {
			productService.deleteProduct(id);
			LOGGER.info("Products || deleteProduct||productService.deleteProduct(id)");
			String extraImageDir = "../product-images/" + id + "/extras";
			LOGGER.info("Products ||deleteProduct||extraImageDir");
			String productImageDir = "../product-images/" + id;
			LOGGER.info("Products ||deleteProduct||productImageDir");
			FileUploadUtil.removeDir(extraImageDir);
			FileUploadUtil.removeDir(productImageDir);
			LOGGER.info("Products ||deleteProduct||FileUploadUtil.removeDir()");
			redirectAttributes.addFlashAttribute("message", "Product delete successfully with " + id);
			LOGGER.info("Products ||deleteProduct||Product delete successfully");
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			LOGGER.info("Products ||deleteProduct "+e.getMessage());
		}
		return "redirect:/products";
	}
	
	@GetMapping("products/edit/{id}")
	public String editproduct(@PathVariable("id")Integer id,Model model,RedirectAttributes redirectAttributes) {
		LOGGER.info("Products ||editproduct page called.");
		try {
			Product product=productService.getById(id);
			List<Brand>brands=brandService.listAllBrand();
			Integer numberOfExistingExtraImages=product.getImages().size();
			model.addAttribute("product",product);
			model.addAttribute("listBrands",brands);
			model.addAttribute("pageTitle","Edit Product (ID: " +id+")");
			model.addAttribute("numberOfExistingExtraImages",numberOfExistingExtraImages);
			return "products/product_form";
		}catch(ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			LOGGER.info("Products ||editproduct "+e.getMessage());
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable("id")Integer id,Model model,RedirectAttributes redirectAttributes) {
		LOGGER.info("Products ||viewProductDetails modal called");
		try {
			Product product=productService.getById(id);			
			model.addAttribute("product",product);				
			return "products/product_detail_modal";
		}catch(ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			LOGGER.info("Products ||viewProductDetails "+e.getMessage());
			return "redirect:/products";
		}
	}
}
