package io.ekstrai.apps.ose.rmapp.api;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Note {

    private final String userId;
    private final LocalDateTime timestamp;
    private final String noteId;
    private final String groupId;
    private final boolean isValid;
    private final boolean isPinned;
    private final String content;

    public Note(String userId, LocalDateTime timestamp, String groupId, String content) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.noteId = String.valueOf(UUID.randomUUID());
        this.groupId = groupId;
        this.isValid = true;
        this.isPinned = false;
        this.content = content;
    }

    public Note(String userId, LocalDateTime timestamp, String noteId, String groupId, boolean isValid, boolean isPinned, String content) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.noteId = noteId;
        this.groupId = groupId;
        this.isValid = isValid;
        this.isPinned = isPinned;
        this.content = content;
    }
}