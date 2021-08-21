package io.ekstrai.apps.ose.rmapp.persistance;

import io.ekstrai.apps.ose.rmapp.api.Note;
import io.ekstrai.apps.ose.rmapp.api.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.*;

/*
    TODOS:
    TODO Key Names need to be enumized
    TODO duplicate code can be refactored further thru passing in class type as param
    TODO more useful logs can be added
    TODO no query limit safety in several methods
 */


public class DynamoDbRepo implements IRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDbRepo.class);

    private static final String PARTITION_KEY_NAME = "userId";
    private static final String SORT_KEY_NAME = "timestamp";

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Autowired
    private String noteTable;

    @Autowired
    private String reminderTable;



    /**
     * Updates a single item and single string type attribute in dynamo table.
     * @param cls the Class type of the POJO Item. Only Reminder or Note classes allowed.
     * @param partKey the partition key
     * @param sortKey the sort key
     * @param keyName attribute key name
     * @param keyValue attribute key value
     * @return true of updating item is complete successfully
     */
    public boolean updateItem(Class <?> cls, String partKey, String sortKey,
                              String keyName, String keyValue) {

        return updateAnyField(cls, toPrimaryKey(partKey, sortKey), toUpdatedValues(keyName, keyValue));
    }

    /**
     * Updates a single item and single boolean type attribute in dynamo table.
     * @param cls the Class type of the POJO Item. Only Reminder or Note classes allowed.
     * @param partKey the partition key
     * @param sortKey the sort key
     * @param flagKeyName attribute key name
     * @param flagKeyValue attribute key value of boolean type
     * @return true of updating item is complete successfully
     */
    public boolean updateItem( Class <?> cls, String partKey, String sortKey,
                              String flagKeyName, boolean flagKeyValue) {

        return updateAnyField(
                cls, toPrimaryKey(partKey, sortKey), toUpdatedValues(flagKeyName, flagKeyValue));
    }

    /**
     * Retrieve a single Note object via GSI NoteId
     * @param noteId the note ID
     * @return the Note with the given ID
     */
    public Note getNote(String noteId) {

        QueryRequest request = QueryRequest.builder()
                .indexName("gsi-noteId")
                .tableName(noteTable)
                .keyConditions(toKeyCondition("noteId", noteId))
                .build();

        return tryGetNote(request);
    }

    /**
     * Retrieve a single Note object via its primary key
     * @param userId the partition key
     * @param timestamp the sort key
     * @return the Note with the given primary key
     */
    public Note getNote(String userId, String timestamp) {

        GetItemRequest request = GetItemRequest.builder()
                .key(toPrimaryKey(userId, timestamp))
                .tableName(noteTable)
                .build();

        return tryGetNote(request);
    }

    /**
     * Retrieve a single Reminder object via GSI Reminder Id
     * @param reminderId the reminder ID
     * @return the Reminder with the given ID
     */
    public Reminder getReminder(String reminderId) {

        QueryRequest request = QueryRequest.builder()
                .indexName("gsi-reminderId")
                .tableName(reminderTable)
                .keyConditions(toKeyCondition("reminderId", reminderId))
                .build();

        return tryGetReminder(request);
    }

    /**
     * Retrieve a single Reminder object via its primary key
     * @param userId the partition key
     * @param timestamp the sort key
     * @return the Reminder with the given primary key
     */
    public Reminder getReminder(String userId, String timestamp) {

        GetItemRequest request = GetItemRequest.builder()
                .key(toPrimaryKey(userId, timestamp))
                .tableName(reminderTable)
                .build();

        return tryGetReminder(request);
    }

    /**
     * Makes a query with the given `userId` returns all notes with identical matching partition key.
     * @param userId partition key value for Note table
     * @return List of Note objects with specified partition key.
     */
    //TODO Not limit safe
    public List<Note> getAllNotes_withUserId(String userId) {

        List<Note> notes = new ArrayList<>();
        QueryRequest request = QueryRequest.builder()
                .tableName(noteTable)
                .keyConditions(toKeyCondition(PARTITION_KEY_NAME, userId))
                .build();

        return tryGetAllNotes(notes, request);
    }

    /**
     * Makes a query with the given `userId` returns all notes with identical matching partition key.
     * @param userId partition key value for Note table
     * @return List of Note objects with specified partition key.
     */
    //TODO Not limit safe
    public List<Reminder> getAllReminder_withUserId(String userId) {

        List<Reminder> reminders = new ArrayList<>();
        QueryRequest request = QueryRequest.builder()
                .tableName(reminderTable)
                .keyConditions(toKeyCondition(PARTITION_KEY_NAME, userId))
                .build();

        return tryGetAllReminders(reminders, request);
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


    //---------------------------------------
    //----------- PRIVATE METHODS -----------
    //---------------------------------------

    private boolean updateAnyField(Class<?> cls, Map<String, AttributeValue> itemKey, Map<String, AttributeValueUpdate> updatedValues) {
        UpdateItemRequest request = null;

        if (cls == Reminder.class) {
            request = UpdateItemRequest.builder()
                    .tableName(reminderTable)
                    .key(itemKey)
                    .attributeUpdates(updatedValues)
                    .build();
        } else if (cls == Note.class) {
            request = UpdateItemRequest.builder()
                    .tableName(noteTable)
                    .key(itemKey)
                    .attributeUpdates(updatedValues)
                    .build();
        } else {
            System.exit(1);
            throw new IllegalArgumentException();
        }

        return tryUpdateItem(request);
    }

    private boolean tryUpdateItem(UpdateItemRequest request) {

        boolean updateSuccess = true;
        try {
            dynamoDbClient.updateItem(request);

        } catch (ResourceNotFoundException e) {
            updateSuccess = false;
            LOG.error("Table can't be found");
        } catch (DynamoDbException e) {
            updateSuccess = false;
            LOG.error("Database error occurred: " + e.getMessage());
            System.exit(1);
        }
        return updateSuccess;
    }

    private Reminder tryGetReminder(QueryRequest request) {

        Reminder reminder = null;
        try{
            QueryResponse response = dynamoDbClient.query(request);
            reminder = response.hasItems() ?  toReminder(response.items().get(0)) : null;
        } catch (ResourceNotFoundException e) {
            LOG.error("Table can't be found");

        } catch (DynamoDbException e) {
            LOG.error("Database error occurred: " + e.getMessage());
            System.exit(1);
        }
        return reminder;
    }

    private Reminder tryGetReminder(GetItemRequest request) {
        Reminder reminder = null;
        try{
            GetItemResponse response = dynamoDbClient.getItem(request);
            reminder = response.hasItem() ?  toReminder(response.item()) : null;
        } catch (ResourceNotFoundException e) {
            LOG.error("Table can't be found");
        } catch (DynamoDbException e) {
            LOG.error("Database error occurred: " + e.getMessage());
            System.exit(1);
        }
        return reminder;
    }

    private Note tryGetNote(QueryRequest request) {

        Note note = null;
        try{
            QueryResponse response = dynamoDbClient.query(request);
            note = response.hasItems() ?  toNote(response.items().get(0)) : null;
        } catch (ResourceNotFoundException e) {
            LOG.error("Table can't be found");

        } catch (DynamoDbException e) {
            LOG.error("Database error occurred: " + e.getMessage());
            System.exit(1);
        }
        return note;
    }

    private Note tryGetNote(GetItemRequest request) {
        Note note = null;
        try{
            GetItemResponse response = dynamoDbClient.getItem(request);
            note = response.hasItem() ?  toNote(response.item()) : null;
        } catch (ResourceNotFoundException e) {
            LOG.error("Table can't be found");


        } catch (DynamoDbException e) {
            LOG.error("Database error occurred: " + e.getMessage());
            System.exit(1);
        }
        return note;
    }

    private boolean tryPutItem(PutItemRequest request) {

        boolean isSuccessful = true;
        try {
            dynamoDbClient.putItem(request);
            LOG.info("New item added to table successfully.");
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

    private List<Reminder> tryGetAllReminders(List<Reminder> reminders, QueryRequest request) {

        try{
            QueryResponse response = dynamoDbClient.query(request);
            response.items().forEach(i -> reminders.add(toReminder(i)));
        } catch (ResourceNotFoundException e) {
            LOG.error("Table can't be found");

        } catch (DynamoDbException e) {
            LOG.error("Database error occurred: " + e.getMessage());
            System.exit(1);
        }
        return reminders;
    }

    private Map<String, AttributeValueUpdate> toUpdatedValues(String flagKeyName, boolean flagKeyValue) {
        Map <String, AttributeValueUpdate> updatedValues = new HashMap<>();
        updatedValues.put(flagKeyName, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().bool(flagKeyValue).build())
                .action(AttributeAction.PUT)
                .build());
        return updatedValues;
    }

    private Map<String, AttributeValueUpdate> toUpdatedValues(String keyName, String keyValue) {
        Map <String, AttributeValueUpdate> updatedValues = new HashMap<>();
        updatedValues.put(keyName, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(keyValue).build())
                .action(AttributeAction.PUT)
                .build());
        return updatedValues;
    }

    private Map<String, AttributeValue> toPrimaryKey(String userId, String timestamp) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(PARTITION_KEY_NAME, AttributeValue.builder().s(userId).build());
        key.put(SORT_KEY_NAME, AttributeValue.builder().s(timestamp).build());
        return key;
    }

    private Map<String, Condition> toKeyCondition(String keyName, String keyValue) {
        List<AttributeValue> values = new ArrayList<>();
        values.add(AttributeValue.builder().s(keyValue).build());
        Map<String, Condition> keyCondition = new HashMap<>();

        keyCondition.put(keyName, Condition.builder()
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
        itemValues.put("reminderId", AttributeValue.builder().s(reminder.getReminderId()).build());
        itemValues.put("remindDate", AttributeValue.builder().s(String.valueOf(reminder.getRemindDate())).build());
        itemValues.put("label", AttributeValue.builder().s(reminder.getLabel()).build());
        itemValues.put("isValid", AttributeValue.builder().bool(reminder.isValid()).build());
        itemValues.put("content", AttributeValue.builder().s(reminder.getContent()).build());

        return itemValues;
    }

    private Note toNote(Map<String, AttributeValue> itemValues) {

        return new Note(
                itemValues.get(PARTITION_KEY_NAME).s(),
                LocalDateTime.parse(itemValues.get("timestamp").s()),
                itemValues.get("noteId").s(),
                itemValues.getOrDefault("groupId", AttributeValue.builder().s("invalid_group").build()).s(),
                itemValues.getOrDefault("label", AttributeValue.builder().s("invalid_label").build()).s(),
                itemValues.getOrDefault("isValid", AttributeValue.builder().bool(false).build()).bool(),
                itemValues.getOrDefault("isPinned", AttributeValue.builder().bool(false).build()).bool(),
                itemValues.getOrDefault("content", AttributeValue.builder().s("[empty note]").build()).s());
    }

    private Reminder toReminder(Map<String, AttributeValue> itemValues) {

        return new Reminder(
                itemValues.get(PARTITION_KEY_NAME).s(),
                LocalDateTime.parse(itemValues.get("timestamp").s()),
                itemValues.get("reminderId").s(),
                LocalDateTime.parse(itemValues.get("remindDate").s()),
                itemValues.getOrDefault("label", AttributeValue.builder().s("invalid_label").build()).s(),
                itemValues.getOrDefault("isValid", AttributeValue.builder().bool(false).build()).bool(),
                itemValues.getOrDefault("content", AttributeValue.builder().s("[empty note]").build()).s());
    }




}
