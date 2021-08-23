package io.ekstrai.apps.ose.rmapp.service;

import io.ekstrai.apps.ose.rmapp.api.Note;
import io.ekstrai.apps.ose.rmapp.api.Reminder;
import io.ekstrai.apps.ose.rmapp.persistance.DynamoDbRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class RmService implements IService {


    @Autowired
    private DynamoDbRepo repo;



    //--------- Note Related Services ---------

    public boolean saveNote(Note note) {

        return repo.addItem(note);
    }

    public Note getNote(String noteId) {

        return repo.getNote(noteId);
    }

    public List<Note> getAllNotes(String userId) {

        return repo.getAllNotes_withUserId(userId);
    }

    public Note updateNoteContent(String userId, String timestamp, String content) {

        return repo.updateItemContent(Note.class, userId, timestamp, content)
                ? repo.getNote(userId, timestamp) : null;
    }

    public Note updateNoteLabel(String userId, String timestamp, String label) {

        return repo.updateItemLabel(Note.class, userId, timestamp, label)
                ? repo.getNote(userId, timestamp) : null;
    }


    public Note updateNoteValidity(String userId, String timestamp, boolean isValid) {

        return repo.updateItemValidity(Note.class, userId, timestamp, isValid)
                ? repo.getNote(userId, timestamp) : null;
    }

    public Note updateNotePin(String userId, String timestamp, boolean isPinned) {

        return repo.updateItemPin(Note.class, userId, timestamp, isPinned)
                ? repo.getNote(userId, timestamp) : null;
    }

    public Note updateNoteGroup(String userId, String timestamp, String newGroup) {

        return repo.updateItem(Note.class, userId, timestamp,"groupId", newGroup)
                ? repo.getNote(userId, timestamp) : null;
    }

    //--------- Reminder Related Services ---------

    public boolean saveReminder(Reminder reminder) {

        return repo.addItem(reminder);
    }

    public Reminder getReminder(String reminderId) {

        return repo.getReminder(reminderId);
    }

    public List<Reminder> getAllReminder(String user) {

        return repo.getAllReminder_withUserId(user);
    }

    public Reminder updateReminderContent(String userId, String timestamp, String content) {

        return repo.updateItemContent(Reminder.class, userId, timestamp, content)
                ? repo.getReminder(userId, timestamp) : null;
    }

    public Reminder updateReminderLabel(String userId, String timestamp, String label) {

        return repo.updateItemLabel(Reminder.class, userId, timestamp, label)
                ? repo.getReminder(userId, timestamp) : null;
    }

    public Reminder updateReminderValidity(String userId, String timestamp, boolean isValid) {

        return repo.updateItemValidity(Reminder.class, userId, timestamp, isValid)
                ? repo.getReminder(userId, timestamp) : null;
    }


}
