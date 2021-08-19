package io.ekstrai.apps.ose.rmapp.persistance;

import io.ekstrai.apps.ose.rmapp.api.Note;
import io.ekstrai.apps.ose.rmapp.api.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.*;

public class DynamoDbRepo implements IRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDbRepo.class);

    private static final String PARTITION_KEY_NAME = "userId";

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Autowired
    private String noteTable;

    @Autowired
    private String reminderTable;

    /**
     * Makes a query with the given `userId` returns all notes with identical matching partition key.
     * @param userId partition key value for Note table
     * @return List of Note objects with specified partition key.
     */
    public List<Note> getAllNotes_withUserId(String userId) {

        List<Note> notes = new ArrayList<>();
        QueryRequest request = QueryRequest.builder()
                .tableName(noteTable)
                .keyConditions(toKeyCondition(userId))
                .build();

        return tryGetAllNotes(notes, request);
    }

    /**
     * Saves new Note to table.
     * @param note the Note pojo to be saved
     * @return true if persistence is successful
     */
    public boolean addItem(Note note) {

        PutItemRequest request = PutItemRequest.builder()
                .tableName(noteTable)
                .item(toItemValues(note))
                .build();

        return tryPutItem(request);
    }

    /**
     * Saves new Reminder to table.
     * @param reminder the Reminder pojo to be saved
     * @return true if persistence is successful
     */
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
            System.exit(1);
        }
        return isSuccessful;
    }

    private List<Note> tryGetAllNotes(List<Note> notes, QueryRequest request) {

        try{
            QueryResponse response = dynamoDbClient.query(request);
            response.items().forEach(i -> notes.add(toNote(i)));
        } catch (ResourceNotFoundException e) {
            LOG.error("Table can't be found");

        } catch (DynamoDbException e) {
            LOG.error("Database error occurred: " + e.getMessage());
            System.exit(1);
        }
        return notes;
    }

    private Map<String, Condition> toKeyCondition(String userId) {
        List<AttributeValue> values = new ArrayList<>();
        values.add(AttributeValue.builder().s(userId).build());
        Map<String, Condition> keyCondition = new HashMap<>();

        keyCondition.put(PARTITION_KEY_NAME, Condition.builder()
                .comparisonOperator(ComparisonOperator.EQ)
                .attributeValueList(values)
                .build());
        return keyCondition;
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

    private Note toNote(Map<String, AttributeValue> itemValues) {

        return new Note(
                itemValues.get("userId").s(),
                LocalDateTime.parse(itemValues.get("timestamp").s()),
                itemValues.get("noteId").s(),
                itemValues.getOrDefault("groupId", AttributeValue.builder().s("invalid_group").build()).s(),
                itemValues.getOrDefault("label", AttributeValue.builder().s("invalid_label").build()).s(),
                itemValues.getOrDefault("isValid", AttributeValue.builder().bool(false).build()).bool(),
                itemValues.getOrDefault("isFalse", AttributeValue.builder().bool(false).build()).bool(),
                itemValues.getOrDefault("content", AttributeValue.builder().s("[empty note]").build()).s());
    }


}
