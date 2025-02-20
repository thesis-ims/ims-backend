package com.backend.ims.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
	DataSourceAutoConfiguration.class,
	MailSenderAutoConfiguration.class
})
@ComponentScan(basePackages = {
	"com.backend.ims",
	"com.backend.ims.data",
	"com.backend.ims.general"
})
public class ImsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImsApplication.class, args);
	}

}
