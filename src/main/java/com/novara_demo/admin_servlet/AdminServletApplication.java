package com.novara_demo.admin_servlet;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class AdminServletApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminServletApplication.class, args);
	}

}
