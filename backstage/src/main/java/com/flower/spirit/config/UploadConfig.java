package com.flower.spirit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 
 * 上传文件配置
 * @author flower
 *
 */
@Configuration
public class UploadConfig implements WebMvcConfigurer {
	
	

    @Value("${file.save.staticAccessPath}")
    private String staticAccessPath;
    @Value("${file.save.path}")
    private String uploadRealPath;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    // 添加静态资源处理器，指定访问路径为staticAccessPath，资源位置为file:///uploadRealPath
	    registry.addResourceHandler(staticAccessPath).addResourceLocations("file:///"+uploadRealPath);
	}


}
