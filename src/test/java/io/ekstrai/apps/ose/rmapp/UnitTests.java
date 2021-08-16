package io.ekstrai.apps.ose.rmapp;

import io.ekstrai.apps.ose.rmapp.api.Note;
import io.ekstrai.apps.ose.rmapp.api.Reminder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {

    @Test
    public void testingFrameworkWorks() {

        final int i = 2;
        assertEquals(i, 2);
    }

    @Test
    public void notePojoDefaultConstructorsWorks() {

        Note note = new Note(
                "user", LocalDateTime.now(), "group", "A note");

        assertNotNull(note.getNoteId());
        assertTrue(note.isValid());
        assertFalse(note.isPinned());
    }

    @Test
    public void reminderPojoDefaultConstructorsWorks() {

        Reminder reminder = new Reminder(
                "user", LocalDateTime.now(), LocalDateTime.now().plusDays(2), "work", "meeting");

        assertNotNull(reminder.getReminderId());
        assertTrue(reminder.isValid());
    }


}
