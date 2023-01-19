package com.shopme.admin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

	public static void main(String uploadDir,String fileName,MultipartFile multipartFile) throws IOException {
		
		Path uploadPath=Paths.get(uploadDir);
		if(!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		try(InputStream inputStream=multipartFile.getInputStream()){
			Path filePath=uploadPath.resolve(fileName);
			Files.copy(inputStream,filePath,StandardCopyOption.REPLACE_EXISTING);
		}catch(IOException ex) {
			throw new IOException("Could not save file :" +fileName,ex);
		}
	}
	
	//clean the directory
	public static void cleanDir(String dir) {
		Path dirPath=Paths.get(dir);
		//System.out.println("Directory "+dirPath);		
		try {
			Files.list(dirPath).forEach(file->{
				if(!Files.isDirectory(file)) {
					try {
						Files.delete(file);
					}catch (IOException e) {
						System.out.println("could not delete file " +file);
					}
				}
			});			
		}catch (IOException e) {
			System.out.println("could not list directory " +dirPath);
		}
	}

	public static void removeDir(String brandDir) throws IOException {
		Path dirPath=Paths.get(brandDir);
		for(File file: dirPath.toFile().listFiles()) {
			Files.delete(file.toPath());
		}
		Files.deleteIfExists(dirPath);	
		System.out.println("folder removed successfully!");
	}
}
