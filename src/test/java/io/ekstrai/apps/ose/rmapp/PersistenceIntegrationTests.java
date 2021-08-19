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
    public void getAllNotesFromDynamo_successfully() {

        final Note note =  new Note(
                "user_test", LocalDateTime.now(), "group", "label", "A note");

        assertTrue(repo.addItem(note));

        List<Note> notes = repo.getAllNotes_withUserId("user_test");
        assertTrue(notes.size() > 0);

        LOG.info(mapper.writeValueAsString(notes));
        LOG.info(String.valueOf(notes.size()));
    }

    @SneakyThrows
    @Test
    public void getAllRemindersFromDynamo_successfully() {

        final Reminder reminder = new Reminder(
                "user_test", LocalDateTime.now(), LocalDateTime.now().plusDays(2), "work", "meeting");

        assertTrue(repo.addItem(reminder));

        List<Reminder> reminders = repo.getAllReminder_withUserId("user_test");
        assertTrue(reminders.size() > 0);

        LOG.info(mapper.writeValueAsString(reminders));
        LOG.info(String.valueOf(reminders.size()));
    }

    @SneakyThrows
    @Test
    public void getSingleNoteFromDynamo_withPrimaryKey_successfully() {

        final Note note =  new Note(
                "user_test", LocalDateTime.now(), "group", "label", "A note");

        assertTrue(repo.addItem(note));

        Note returnedNote = repo.getNote(note.getUserId(), note.getTimestamp().toString());

        assertNotNull(returnedNote);

        LOG.info(mapper.writeValueAsString(returnedNote));
    }

    @SneakyThrows
    @Test
    public void getSingleNoteFromDynamo_withGSI_noteId_successfully() {

        final Note note =  new Note(
                "user_test", LocalDateTime.now(), "group", "label", "A note");

        assertTrue(repo.addItem(note));

        final String noteId = note.getNoteId();
        Note returnedNote = repo.getNote(noteId);

        assertNotNull(returnedNote);

        LOG.info(mapper.writeValueAsString(returnedNote));
    }

    @SneakyThrows
    @Test
    public void getSingleReminderFromDynamo_withPrimaryKey_successfully() {

        final Reminder reminder = new Reminder(
                "user_test", LocalDateTime.now(), LocalDateTime.now().plusDays(2), "work", "meeting");

        assertTrue(repo.addItem(reminder));

        Reminder returnedReminder = repo.getReminder(
                reminder.getUserId(), reminder.getTimestamp().toString());

        assertNotNull(returnedReminder);

        LOG.info(mapper.writeValueAsString(returnedReminder));
    }

    @SneakyThrows
    @Test
    public void getSingleReminderFromDynamo_withGSI_noteId_successfully() {

        final Reminder reminder = new Reminder(
                "user_test", LocalDateTime.now(), LocalDateTime.now().plusDays(2), "work", "meeting");

        assertTrue(repo.addItem(reminder));

        final String reminderId = reminder.getReminderId();
        Reminder returnedReminder = repo.getReminder(reminderId);

        assertNotNull(returnedReminder);

        LOG.info(mapper.writeValueAsString(returnedReminder));
    }

}
