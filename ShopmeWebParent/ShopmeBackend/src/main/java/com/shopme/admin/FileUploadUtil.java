package com.shopme.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.admin.user.UserController;

public class FileUploadUtil {
	private static final Logger LOGGER=LoggerFactory.getLogger(UserController.class);
	
	public static void main(String uploadDir,String fileName,MultipartFile multipartFile) throws IOException {
		LOGGER.info("FileUploadUtil main method called.");
		Path uploadPath=Paths.get(uploadDir);
		if(!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		try(InputStream inputStream=multipartFile.getInputStream()){
			Path filePath=uploadPath.resolve(fileName);
			Files.copy(inputStream,filePath,StandardCopyOption.REPLACE_EXISTING);
		}catch(IOException ex) {
			LOGGER.info("FileUploadUtil main method:could not save file."+ex);
		}
	}
	
	//remove the directory
	public static void cleanDir(String dir) {
		LOGGER.info("FileUploadUtil | cleanDir is started");		
		LOGGER.info("FileUploadUtil | cleanDir | dir : " + dir);		
		Path dirPath = Paths.get(dir);		
		LOGGER.info("FileUploadUtil | cleanDir | dirPath : " + dirPath);

		try {
			Files.list(dirPath).forEach(file -> {				
				LOGGER.info("FileUploadUtil | cleanDir | file : " + file.toString());				
				LOGGER.info("FileUploadUtil | cleanDir | Files.isDirectory(file) : " + Files.isDirectory(file));
				
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
}
