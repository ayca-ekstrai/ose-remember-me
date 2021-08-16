package io.ekstrai.apps.ose.rmapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppConfig {

    @Autowired
    private Environment env;

    //TODO initial config wiring and purposes only.
    @Bean(name = "dataSource")
    public String dataSource() {

        return env.getProperty("database.username") + env.getProperty("database.password");
    }
}
