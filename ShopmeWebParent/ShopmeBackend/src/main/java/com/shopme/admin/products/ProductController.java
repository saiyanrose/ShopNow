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
		model.addAttribute("products", products);
		return "products";
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.findAll();
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		return "product_form";
	}

	@PostMapping("/products/save")
	public String saveProducts(Product product, RedirectAttributes redirectAttributes,
			@RequestParam("fileImage") MultipartFile file, @RequestParam("extraImage") MultipartFile[] img,
			@RequestParam(name="detailName",required = false) String[] detailName,@RequestParam(name="detailValue",required = false)String[] detailValue,
			@RequestParam(name="imageIds",required=false)String[] imageIds,@RequestParam(name="imageName",required=false)String[] imageName,
			@RequestParam(name="detailIds",required = false) String[] detailIds)
			throws IOException {
		saveMainImage(file, product);
		setExistingExtraImages(imageIds,imageName,product);
		setNewExtraImage(img, product);
		setProductDetails(detailIds,detailName,detailValue,product);
		Product saveProduct = productService.save(product);
		saveUplodedImage(file, img, product);
		deleteExtraImagesRemoveFromForm(product);
		redirectAttributes.addFlashAttribute("message", "Product save successfully!");
		return "redirect:/products";
	}

	private void deleteExtraImagesRemoveFromForm(Product product) {
		String extraImageDir="../product-images/" + product.getId() + "/extras";
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
		if (!file.isEmpty()) {
			String filename = StringUtils.cleanPath(file.getOriginalFilename()).replace(" ", "");
			String uploadDir = "../product-images/" + saveProduct.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.main(uploadDir, filename, file);
		}
		if (img.length > 0) {
			String uploadDir = "../product-images/" + saveProduct.getId() + "/extras";
			for (MultipartFile extraFile : img) {
				if (extraFile.isEmpty())continue;
				
				String filename = StringUtils.cleanPath(extraFile.getOriginalFilename()).replace(" ", "");
				FileUploadUtil.main(uploadDir, filename, extraFile);
			}
		}

	}

	private void setNewExtraImage(MultipartFile[] img, Product product) {
		if (img.length > 0) {
			for (MultipartFile file : img) {
				if (!file.isEmpty()) {
					String filename = StringUtils.cleanPath(file.getOriginalFilename()).replace(" ", "");
					if(!product.containsImageName(filename)) {
						product.addExtraImage(filename);
					}
				}
			}
		}

	}

	private void saveMainImage(MultipartFile file, Product product) {
		if (!file.isEmpty()) {
			String filename = StringUtils.cleanPath(file.getOriginalFilename()).replace(" ", "");
			product.setMainImage(filename);
		}

	}

	@GetMapping("/products/{id}/enabled/{status}")
	public String enableStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean status,
			RedirectAttributes redirectAttributes) {
		productService.checkEnabledStatus(id, status);
		String enabled = status ? "Enabled" : "Disabled";
		String message = "the user id " + id + "has been " + enabled;
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
			FileUploadUtil.removeDir(extraImageDir);
			FileUploadUtil.removeDir(productImageDir);
			redirectAttributes.addFlashAttribute("message", "Product delete successfully with " + id);
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/products";
	}
	
	@GetMapping("products/edit/{id}")
	public String editproduct(@PathVariable("id")Integer id,Model model,RedirectAttributes redirectAttributes) {
		try {
			Product product=productService.getById(id);
			List<Brand>brands=brandService.listAllBrand();
			Integer numberOfExistingExtraImages=product.getImages().size();
			model.addAttribute("product",product);
			model.addAttribute("listBrands",brands);
			model.addAttribute("pageTitle","Edit Product (ID: " +id+")");
			model.addAttribute("numberOfExistingExtraImages",numberOfExistingExtraImages);
			return "product_form";
		}catch(ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/products";
		}
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable("id")Integer id,Model model,RedirectAttributes redirectAttributes) {
		try {
			Product product=productService.getById(id);			
			model.addAttribute("product",product);				
			return "product_detail_modal";
		}catch(ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message",e.getMessage());
			return "redirect:/products";
		}
	}
}
