package io.ekstrai.apps.ose.rmapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ekstrai.apps.ose.rmapp.api.Note;
import io.ekstrai.apps.ose.rmapp.api.Reminder;
import io.ekstrai.apps.ose.rmapp.persistance.DynamoDbRepo;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RmappApplication.class)
@TestPropertySource("classpath:cloud-test.properties")
public class PersistenceIntegrationTests {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceIntegrationTests.class);


    @Autowired
    private DynamoDbRepo repo;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void contextLoads() {

    }

    @Test
    public void addNewNoteToDynamoDb_successfully() {

        final Note note =  new Note(
                "user_test", LocalDateTime.now(), "group", "label", "A note");

        assertTrue(repo.addItem(note));
    }

    @Test
    public void addNewReminderToDynamoDb_successfully() {

        final Reminder reminder = new Reminder(
                "user_test", LocalDateTime.now(), LocalDateTime.now().plusDays(2), "work", "meeting");

        assertTrue(repo.addItem(reminder));
    }



    @SneakyThrows
    @Test
    public void getRetrieveItemFromDynamo_successfully() {
        final Note note =  new Note(
                "user_test", LocalDateTime.now(), "group", "label", "A note");

        assertTrue(repo.addItem(note));

        List<Note> notes = repo.getAllNotes_withUserId("user_test");
        assertTrue(notes.size() > 0);

        LOG.info(mapper.writeValueAsString(notes));
        LOG.info(String.valueOf(notes.size()));
    }

}
