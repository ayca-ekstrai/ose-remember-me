package io.ekstrai.apps.ose.rmapp.persistance;

import io.ekstrai.apps.ose.rmapp.api.Note;
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

    public boolean addNote(Note note) {
        boolean isSuccessful = true;
        //TODO to be filled
        PutItemRequest request = PutItemRequest.builder()
                .tableName(noteTable)
                .item(toItemValues(note))
                .build();

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
        itemValues.put("isValid", AttributeValue.builder().bool(note.isValid()).build());
        itemValues.put("isPinned", AttributeValue.builder().bool(note.isPinned()).build());
        itemValues.put("content", AttributeValue.builder().s(note.getContent()).build());

        return itemValues;
    }
}
