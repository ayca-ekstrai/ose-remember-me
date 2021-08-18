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
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private DynamoDbRepo repo;

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



    @Test
    public void getRetrieveItemFromDynamo_successfully() {
        final Reminder reminder = new Reminder(
                "user_test", LocalDateTime.now(), LocalDateTime.now().plusDays(2), "work", "meeting");

        assertTrue(repo.addItem(reminder));

        List<Note> notes = repo.getAllNotes_withUserId("user_test");
        assertNotNull(notes);
        //LOG.info(MAPPER.writeValueAsString(notes));
        LOG.info(String.valueOf(notes.size()));
        LOG.info(notes.toString());
    }

}
