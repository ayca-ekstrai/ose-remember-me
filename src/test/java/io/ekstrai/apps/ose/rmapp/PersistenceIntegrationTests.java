package io.ekstrai.apps.ose.rmapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ekstrai.apps.ose.rmapp.api.Note;
import io.ekstrai.apps.ose.rmapp.api.Reminder;
import io.ekstrai.apps.ose.rmapp.persistance.DynamoDbRepo;
import io.ekstrai.apps.ose.rmapp.service.RmService;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RmappApplication.class)
@TestPropertySource("classpath:cloud-test.properties")
public class PersistenceIntegrationTests {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceIntegrationTests.class);


    @Autowired
    private DynamoDbRepo repo;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RmService service;

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

    @SneakyThrows
    @Test
    public void updateReminderFlagAttribute_withPrimaryKey_successfully() {

        final Reminder reminder = new Reminder(
                "user_test", LocalDateTime.now(), LocalDateTime.now().plusDays(2), "work", "meeting");

        assertTrue(repo.addItem(reminder));

        assertTrue(repo.updateItem(Reminder.class,
                reminder.getUserId(), reminder.getTimestamp().toString(), "isValid", false));

        Reminder returnedReminder = repo.getReminder(
                reminder.getUserId(), reminder.getTimestamp().toString());

        LOG.info(mapper.writeValueAsString(reminder));
        LOG.info(mapper.writeValueAsString(returnedReminder));

        assertTrue(reminder.isValid());
        assertFalse(returnedReminder.isValid());
    }


    @SneakyThrows
    @Test
    public void updateNoteFlagAttribute_withPrimaryKey_successfully() {

        final Note note =  new Note(
                "user_test", LocalDateTime.now(), "group", "label", "A note");

        assertTrue(repo.addItem(note));

        // test updating isValid flag
        assertTrue(repo.updateItem(Note.class,
                note.getUserId(), note.getTimestamp().toString(), "isValid", false));

        final Note returnedNote = repo.getNote(
                note.getUserId(), note.getTimestamp().toString());

        LOG.info(mapper.writeValueAsString(note));
        LOG.info(mapper.writeValueAsString(returnedNote) + "\n");

        assertTrue(note.isValid());
        assertFalse(returnedNote.isValid());

        // test updating isPinned flag
        assertTrue(repo.updateItem(Note.class,
                note.getUserId(), note.getTimestamp().toString(), "isPinned", true));

        final Note returnedPinnedNote = repo.getNote(
                note.getUserId(), note.getTimestamp().toString());

        LOG.info(mapper.writeValueAsString(note));
        LOG.info(mapper.writeValueAsString(returnedPinnedNote));

        assertFalse(note.isPinned());
        assertFalse(returnedNote.isPinned());
        assertTrue(returnedPinnedNote.isPinned());
    }

    @SneakyThrows
    @Test
    public void updateReminderContentAttribute_withPrimaryKey_successfully() {

        final Reminder reminder = new Reminder(
                "user_test", LocalDateTime.now(), LocalDateTime.now().plusDays(2), "work", "meeting");

        assertTrue(repo.addItem(reminder));

        // test updating content text field
        assertTrue(repo.updateItem(Reminder.class,
                reminder.getUserId(), reminder.getTimestamp().toString(), "content", "shopping"));

        Reminder returnedReminder = repo.getReminder(
                reminder.getUserId(), reminder.getTimestamp().toString());

        LOG.info(mapper.writeValueAsString(reminder));
        LOG.info(mapper.writeValueAsString(returnedReminder) + "\n");

        assertNotEquals(reminder.getContent(), returnedReminder.getContent());
        assertEquals(returnedReminder.getContent(), "shopping");

        // test updating remindDate field
        assertTrue(repo.updateItem(Reminder.class,
                reminder.getUserId(), reminder.getTimestamp().toString(),
                "remindDate", LocalDateTime.now().plusDays(3).toString()));

        Reminder returnedChangedRemindDateReminder = repo.getReminder(
                reminder.getUserId(), reminder.getTimestamp().toString());

        LOG.info(mapper.writeValueAsString(reminder));
        LOG.info(mapper.writeValueAsString(returnedChangedRemindDateReminder));

        assertEquals(reminder.getRemindDate(), returnedReminder.getRemindDate());
        assertNotEquals(reminder.getRemindDate(), returnedChangedRemindDateReminder.getRemindDate());

    }

    @SneakyThrows
    @Test
    public void serviceLayerGetNote(){
        Note note = service.getNote("3785f66a-9f17-4f0e-9949-9838916b5532");
        assertNotNull(note);
        LOG.info(mapper.writeValueAsString(note));
    }

    @SneakyThrows
    @Test
    public void serviceLayerGetAllNotes(){
        final Note note =  new Note(
                "user_test", LocalDateTime.now(), "group", "label", "A note");

        assertTrue(repo.addItem(note));
        List<Note> notes = service.getAllNotes("user_test");
        assertTrue(notes.size() > 0);
        LOG.info(mapper.writeValueAsString(notes));
    }





}
