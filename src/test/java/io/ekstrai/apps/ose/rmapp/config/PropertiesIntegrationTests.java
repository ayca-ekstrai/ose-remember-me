package io.ekstrai.apps.ose.rmapp.config;

import io.ekstrai.apps.ose.rmapp.RmappApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class) //junit4's RunWith() for 5
@SpringBootTest(classes = RmappApplication.class)
@TestPropertySource("classpath:cloud-test.properties")
public class PropertiesIntegrationTests {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesIntegrationTests.class);

    @Autowired
    @Qualifier("dataSource")
    private String mockDatabase;

    @Autowired
    private ImmutableConfigProperties immutableProperties;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Autowired
    private Environment env;

    @Autowired
    private String reminderTable;

    @Autowired
    private String noteTable;


    @Test
    public void whenEnvUsedForDatabaseProperties_thenItWorksProperly() {

        assertEquals(mockDatabase, "user0password123");
    }

    @Test
    public void immutableConfigPropertiesWorksWithConstructorBinding() {

        assertThat(immutableProperties.getAuthMethod()).isEqualTo("SHA1");
        assertThat(immutableProperties.getUsername()).isEqualTo("mary");
        assertThat(immutableProperties.getPassword()).isEqualTo("password");
    }

    @Test
    public void dynamoDbClientBeanInjectionWorks() {

        assertThat(dynamoDbClient.listTables().tableNames().size()).isGreaterThan(0);
    }

    @Test
    public void dynamoDbTableNamesFromPropertiesViaEnv_pulledCorrectly() {

        assertThat(env.containsProperty("aws.dynamo.tableName.reminder")).isTrue();
        assertThat(env.getProperty("aws.dynamo.tableName.reminder")).isEqualTo("ose.rm.reminder");

        assertThat(env.containsProperty("aws.dynamo.tableName.note")).isTrue();
        assertThat(env.getProperty("aws.dynamo.tableName.note")).isEqualTo("ose.rm.note");
    }

    @Test
    public void dynamoDbTableNameBeans_assignedProperly() {

        assertThat(reminderTable).isEqualTo("ose.rm.reminder");
        assertThat(noteTable).isEqualTo("ose.rm.note");
    }
}
