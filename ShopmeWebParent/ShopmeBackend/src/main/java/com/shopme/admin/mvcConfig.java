package com.shopme.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class mvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String dirName="user-photos";
		Path userPhotosDir=Paths.get(dirName);		
		String userPhotosPath=userPhotosDir.toFile().getAbsolutePath();		
		registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + userPhotosPath + "/");
		
		String categoryImageDirName="../category-image";
		Path categoryImageDir=Paths.get(categoryImageDirName);		
		String categoryImagePath=categoryImageDir.toFile().getAbsolutePath();		
		registry.addResourceHandler("/category-image/**").addResourceLocations("file:/" + categoryImagePath + "/");
		
		String brandImageDirName="../brand-logos";
		Path brandImageDir=Paths.get(brandImageDirName);		
		String brandImagePath=brandImageDir.toFile().getAbsolutePath();		
		registry.addResourceHandler("/brand-logos/**").addResourceLocations("file:/" + brandImagePath + "/");
		
		String productDirName="../product-images";
		Path productPhotosDir=Paths.get(productDirName);		
		String productPhotosPath=productPhotosDir.toFile().getAbsolutePath();		
		registry.addResourceHandler("/product-images/**").addResourceLocations("file:/" + productPhotosPath + "/");
		
		String siteDirName="../site-logo";
		Path sitePhotosDir=Paths.get(siteDirName);		
		String sitePhotosPath=sitePhotosDir.toFile().getAbsolutePath();		
		registry.addResourceHandler("/site-logo/**").addResourceLocations("file:/" + sitePhotosPath + "/");
	}

}
