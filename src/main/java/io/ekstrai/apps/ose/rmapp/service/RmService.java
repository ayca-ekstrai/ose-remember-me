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



}
