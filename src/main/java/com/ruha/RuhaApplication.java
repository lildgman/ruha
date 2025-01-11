package com.ruha;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.ruha.mapper")
public class RuhaApplication {

	public static void main(String[] args) {
		SpringApplication.run(RuhaApplication.class, args);
	}

}
