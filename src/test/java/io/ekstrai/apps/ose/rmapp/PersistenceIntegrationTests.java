package io.ekstrai.apps.ose.rmapp;

import io.ekstrai.apps.ose.rmapp.api.Note;
import io.ekstrai.apps.ose.rmapp.persistance.DynamoDbRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RmappApplication.class)
@TestPropertySource("classpath:cloud-test.properties")
public class PersistenceIntegrationTests {

    @Autowired
    private DynamoDbRepo repo;

    @Test
    public void contextLoads() {

    }

    @Test
    public void addNewNoteToDynamoDb_successfully() {

        final Note note =  new Note(
                "user_test", LocalDateTime.now(), "group", "label", "A note");

        assertTrue(repo.addNote(note));
    }

}
