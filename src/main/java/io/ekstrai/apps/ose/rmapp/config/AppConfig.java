package io.ekstrai.apps.ose.rmapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ekstrai.apps.ose.rmapp.RmController;
import io.ekstrai.apps.ose.rmapp.persistance.DynamoDbRepo;
import io.ekstrai.apps.ose.rmapp.service.RmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class AppConfig {

    @Autowired
    private Environment env;

    //TODO initial config wiring and purposes only.
    @Bean(name = "dataSource")
    public String dataSource() {

        return env.getProperty("database.username") + env.getProperty("database.password");
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.create();
    }

    @Bean
    public String reminderTable() {
        return env.getProperty("aws.dynamo.tableName.reminder");
    }

    @Bean
    public String noteTable() {
        return env.getProperty("aws.dynamo.tableName.note");
    }

    @Bean(name = "repo")
    public DynamoDbRepo dynamoDbRepo() {

        return new DynamoDbRepo();
    }

    @Bean(name = "mapper")
    public ObjectMapper mapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean(name = "service")
    public RmService rmService() {
        return new RmService();
    }


}
