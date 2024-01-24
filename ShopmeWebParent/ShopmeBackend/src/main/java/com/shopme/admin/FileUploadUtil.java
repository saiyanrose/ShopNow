package com.shopme.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.user.UserController;
import com.shopme.common.entity.Product;

@Component
public class FileUploadUtil {
	private static final Logger LOGGER=LoggerFactory.getLogger(UserController.class);
	
	public static void main(String uploadDir,String fileName,MultipartFile multipartFile){		
		LOGGER.info("FileUploadUtil main method called.");
		Path uploadPath=Paths.get(uploadDir);
		
		if(!Files.exists(uploadPath)) {
			try {
				Files.createDirectories(uploadPath);
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}		
		
		try(InputStream inputStream=multipartFile.getInputStream()){
			Path filePath=uploadPath.resolve(fileName);			
			Files.copy(inputStream,filePath,StandardCopyOption.REPLACE_EXISTING);
		}catch(IOException ex) {
			ex.printStackTrace();
			LOGGER.info("FileUploadUtil main method:could not save file."+ex);
		}
	}	
	
	public static void cleanDir(String dir) {
		LOGGER.info("FileUploadUtil | cleanDir is started");		
		LOGGER.info("FileUploadUtil | cleanDir | dir : " + dir);		
		Path dirPath = Paths.get(dir);		
		LOGGER.info("FileUploadUtil | cleanDir | dirPath : " + dirPath);

		try {
			Files.list(dirPath).forEach(file -> {				
				if (!Files.isDirectory(file)) {
					try {
						Files.delete(file);						
						LOGGER.info("FileUploadUtil | cleanDir | delete is completed");						
					} catch (IOException ex) {						
						LOGGER.error("FileUploadUtil | cleanDir | ex.getMessage() : " + ex.getMessage());
						LOGGER.error("Could not delete file: " + file);						
					}
				}
			});
		} catch (IOException ex) {			
			LOGGER.error("FileUploadUtil | cleanDir | ex.getMessage() : " + ex.getMessage());
			LOGGER.error("Could not list directory: " + dirPath);			
		}
	}

	public static void removeDir(String brandDir) throws IOException {
		LOGGER.info("FileUploadUtil | removeDir is started");		
		LOGGER.info("FileUploadUtil | removeDir | dir : " + brandDir);		
		
		cleanDir(brandDir);		
		LOGGER.info("FileUploadUtil | cleanDir(dir) is over");

		try {
			Files.delete(Paths.get(brandDir));
		} catch (IOException e) {
			LOGGER.error("Could not remove directory: " + brandDir);
		}
	}
	
	public void deleteExtraImagesRemoveFromForm(Product product) {
		LOGGER.info("Products || deleteExtraImagesRemoveFromForm called");
		String extraImageDir = "../product-images/" + product.getId() + "/extras";

		Path dirPath = Paths.get(extraImageDir);
		try {
			Files.list(dirPath).forEach(file -> {
				String filename = file.toFile().getName();
				if (!product.containsImageName(filename)) {
					try {
						Files.delete(file);
					} catch (IOException e) {
						LOGGER.error("Colud not delete extra images " + filename);
					}
				}
			});
		} catch (IOException ex) {
			LOGGER.error("Colud not list directory " + dirPath);
		}
	}
	
	public void saveUplodedImage(MultipartFile file, MultipartFile[] img, Product saveProduct) throws IOException {
		LOGGER.info("Products || saveUplodedImage called");
		if (!file.isEmpty()) {
			String filename = StringUtils.cleanPath(file.getOriginalFilename()).replace(" ", "");

			String uploadDir = "../product-images/" + saveProduct.getId();

			FileUploadUtil.cleanDir(uploadDir);

			FileUploadUtil.main(uploadDir, filename, file);

		}
		if (img.length > 0) {
			String uploadDir = "../product-images/" + saveProduct.getId() + "/extras";
			LOGGER.info("Products || saveUplodedImage extra uploadDir " + uploadDir);
			for (MultipartFile extraFile : img) {
				if (extraFile.isEmpty())
					continue;

				String filename = StringUtils.cleanPath(extraFile.getOriginalFilename()).replace(" ", "");

				FileUploadUtil.main(uploadDir, filename, extraFile);

			}
		}

	}
}
