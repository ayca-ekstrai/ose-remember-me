package io.ekstrai.apps.ose.rmapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RmappApplication {

	public static void main(String[] args) {
		SpringApplication.run(RmappApplication.class, args);
	}

}
