package io.ekstrai.apps.ose.rmapp;

import io.ekstrai.apps.ose.rmapp.api.Note;
import io.ekstrai.apps.ose.rmapp.api.Reminder;
import io.ekstrai.apps.ose.rmapp.service.RmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class RmController {


    /*
        TODO jackson LocalDateTime mapping needs to be fixed
     */

    @Autowired
    private RmService service;


    //--------- Note Endpoints ---------

    @GetMapping(value = "/note/{id}",produces = "application/json")
    public Note getNoteById(@PathVariable String id) {
        return service.getNote(id);
    }

    @GetMapping(value = "/note/all/{userId}")
    public List<Note> getAllNotesByUser(@PathVariable String userId) {
        return service.getAllNotes(userId);
    }

    @PostMapping(value = "/note/add")
    public Note addNewNote(@RequestParam(name = "userId") String userId,
                           @RequestParam(name = "groupId") String groupId,
                           @RequestParam(name = "label") String label,
                           @RequestParam(name = "content") String content) {

        final Note note = new Note(userId, groupId, label, content);
        return service.saveNote(note) ? note : null;
    }


    //--------- Reminder Endpoints ---------

    @GetMapping(value = "/reminder/{id}",produces = "application/json")
    public Reminder getReminderById(@PathVariable String id) {
        return service.getReminder(id);
    }

    @GetMapping(value = "/reminder/all/{userId}")
    public List<Reminder> getAllRemindersByUser(@PathVariable String userId) {
        return service.getAllReminder(userId);
    }

    @PostMapping(value = "/reminder/add")
    public Reminder addNewReminder (@RequestParam(name = "userId") String userId,
                           @RequestParam(name = "groupId") String remindDate,
                           @RequestParam(name = "label") String label,
                           @RequestParam(name = "content") String content) {

        final Reminder reminder = new Reminder(
                userId, LocalDateTime.parse(remindDate), label, content);

        return service.saveReminder(reminder) ? reminder : null;
    }

}
