package io.ekstrai.apps.ose.rmapp;

import io.ekstrai.apps.ose.rmapp.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ConfigurationPropertiesScan
@PropertySource("classpath:cloud.properties")
public class RmappApplication {

	public static void main(String[] args) {
		SpringApplication.run(RmappApplication.class, args);
	}

}
