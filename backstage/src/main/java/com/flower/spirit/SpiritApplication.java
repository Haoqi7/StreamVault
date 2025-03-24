package com.flower.spirit;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.flower.spirit.utils.FileUtil;


@SpringBootApplication
@EnableScheduling
public class SpiritApplication {

	public static void main(String[] args) {
		SpiritApplication.initData();
		SpringApplication.run(SpiritApplication.class, args);
	}

	
	public static void initData() {
		try {
			  File destDir = new File("/app/db/spirit.db");
			  if(!destDir.exists()) {
				  FileUtil.copyDir("/home/app/db", "/app/db");
			  }
		} catch (Exception e) {
		}
	}
}
