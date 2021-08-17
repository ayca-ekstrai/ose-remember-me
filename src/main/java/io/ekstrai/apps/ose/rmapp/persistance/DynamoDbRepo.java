package io.ekstrai.apps.ose.rmapp.persistance;

import io.ekstrai.apps.ose.rmapp.api.Note;
import io.ekstrai.apps.ose.rmapp.api.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class DynamoDbRepo implements IRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDbRepo.class);

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Autowired
    private String noteTable;

    @Autowired
    private String reminderTable;

    public boolean addItem(Note note) {

        PutItemRequest request = PutItemRequest.builder()
                .tableName(noteTable)
                .item(toItemValues(note))
                .build();

        return tryPutItem(request);
    }

    public boolean addItem(Reminder reminder) {

        PutItemRequest request = PutItemRequest.builder()
                .tableName(reminderTable)
                .item(toItemValues(reminder))
                .build();

        return tryPutItem(request);
    }

    //----- private methods -----

    private boolean tryPutItem(PutItemRequest request) {

        boolean isSuccessful = true;
        try {
            dynamoDbClient.putItem(request);
            LOG.info("New note added to table successfully.");
        } catch (ResourceNotFoundException e) {
            LOG.error("Table can't be found");
            isSuccessful = false;
        } catch (DynamoDbException e) {
            LOG.error("Database error occurred: " + e.getMessage());
            isSuccessful = false;
        }
        return isSuccessful;
    }

    private Map<String, AttributeValue> toItemValues(Note note) {

        Map<String, AttributeValue> itemValues = new HashMap<>();

        itemValues.put("userId", AttributeValue.builder().s(note.getUserId()).build());
        itemValues.put("timestamp", AttributeValue.builder().s(String.valueOf(note.getTimestamp())).build());
        itemValues.put("noteId", AttributeValue.builder().s(note.getNoteId()).build());
        itemValues.put("groupId", AttributeValue.builder().s(note.getGroupId()).build());
        itemValues.put("label", AttributeValue.builder().s(note.getLabel()).build());
        itemValues.put("isValid", AttributeValue.builder().bool(note.isValid()).build());
        itemValues.put("isPinned", AttributeValue.builder().bool(note.isPinned()).build());
        itemValues.put("content", AttributeValue.builder().s(note.getContent()).build());

        return itemValues;
    }

    private Map<String, AttributeValue> toItemValues(Reminder reminder) {

        Map<String, AttributeValue> itemValues = new HashMap<>();

        itemValues.put("userId", AttributeValue.builder().s(reminder.getUserId()).build());
        itemValues.put("timestamp", AttributeValue.builder().s(String.valueOf(reminder.getTimestamp())).build());
        itemValues.put("noteId", AttributeValue.builder().s(reminder.getReminderId()).build());
        itemValues.put("remindDate", AttributeValue.builder().s(String.valueOf(reminder.getRemindDate())).build());
        itemValues.put("label", AttributeValue.builder().s(reminder.getLabel()).build());
        itemValues.put("isValid", AttributeValue.builder().bool(reminder.isValid()).build());
        itemValues.put("content", AttributeValue.builder().s(reminder.getContent()).build());

        return itemValues;
    }
}
