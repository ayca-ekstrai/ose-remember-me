package io.ekstrai.apps.ose.rmapp.api;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Reminder {

    private final String userId;
    private final LocalDateTime timestamp;
    private final String reminderId;
    private final LocalDateTime remindDate;
    private final String label;
    private final boolean isValid;
    private final String content;

    public Reminder(String userId, LocalDateTime timestamp, LocalDateTime remindDate, String label, String content) {

        this.userId = userId;
        this.timestamp = timestamp;
        this.reminderId = String.valueOf(UUID.randomUUID());
        this.remindDate = remindDate;
        this.label = label;
        this.isValid = true;
        this.content = content;
    }

    public Reminder(String userId, LocalDateTime timestamp, String reminderId, LocalDateTime remindDate, String label, boolean isValid, String content) {

        this.userId = userId;
        this.timestamp = timestamp;
        this.reminderId = reminderId;
        this.remindDate = remindDate;
        this.label = label;
        this.isValid = isValid;
        this.content = content;
    }
}