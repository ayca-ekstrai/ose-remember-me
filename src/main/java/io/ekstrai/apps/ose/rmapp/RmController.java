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
        TODO Server response codes are not accurate
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

    @PostMapping(value = "/note/upd/c")
    public Note updateNoteContent(@RequestParam(name = "userId") String userId,
                           @RequestParam(name = "timestamp") String timestamp,
                           @RequestParam(name = "content") String content) {

        return service.updateNoteContent(userId, timestamp, content);
    }

    @PostMapping(value = "/note/upd/l")
    public Note updateNoteLabel(@RequestParam(name = "userId") String userId,
                                  @RequestParam(name = "timestamp") String timestamp,
                                  @RequestParam(name = "label") String label) {

        return service.updateNoteLabel(userId, timestamp, label);
    }

    @PostMapping(value = "/note/upd/g")
    public Note updateNoteGroup(@RequestParam(name = "userId") String userId,
                                @RequestParam(name = "timestamp") String timestamp,
                                @RequestParam(name = "groupId") String groupId) {

        return service.updateNoteGroup(userId, timestamp, groupId);
    }

    @PostMapping(value = "/note/upd/p")
    public Note updateNotePin(@RequestParam(name = "userId") String userId,
                                  @RequestParam(name = "timestamp") String timestamp,
                                  @RequestParam(name = "isPinned") boolean isPinned) {

        return service.updateNotePin(userId, timestamp, isPinned);
    }

    @PostMapping(value = "/note/del")
    public Note deleteNoteContent(@RequestParam(name = "userId") String userId,
                                  @RequestParam(name = "timestamp") String timestamp,
                                  @RequestParam(name = "isValid") boolean isValid) {

        return service.updateNoteValidity(userId, timestamp, isValid);
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
                           @RequestParam(name = "remindDate") String remindDate,
                           @RequestParam(name = "label") String label,
                           @RequestParam(name = "content") String content) {

        final Reminder reminder = new Reminder(
                userId, LocalDateTime.parse(remindDate), label, content);

        return service.saveReminder(reminder) ? reminder : null;
    }

    @PostMapping(value = "/reminder/upd/c")
    public Reminder updateReminderContent(@RequestParam(name = "userId") String userId,
                                  @RequestParam(name = "timestamp") String timestamp,
                                  @RequestParam(name = "content") String content) {

        return service.updateReminderContent(userId, timestamp, content);
    }

    @PostMapping(value = "/reminder/upd/l")
    public Reminder updateReminderLabel(@RequestParam(name = "userId") String userId,
                                @RequestParam(name = "timestamp") String timestamp,
                                @RequestParam(name = "label") String label) {

        return service.updateReminderLabel(userId, timestamp, label);
    }

    @PostMapping(value = "/reminder/del")
    public Reminder deleteReminderContent(@RequestParam(name = "userId") String userId,
                                  @RequestParam(name = "timestamp") String timestamp,
                                  @RequestParam(name = "isValid") boolean isValid) {

        return service.updateReminderValidity(userId, timestamp, isValid);
    }
}
