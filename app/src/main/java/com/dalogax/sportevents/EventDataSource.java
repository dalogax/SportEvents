package com.dalogax.sportevents;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class EventDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TITLE, MySQLiteHelper.COLUMN_DESCRIPTION,
            MySQLiteHelper.COLUMN_CATEGORY, MySQLiteHelper.COLUMN_DATE};

    public EventDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public EventInfo createEvent(String title, String description, int category, String date) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, description);
        values.put(MySQLiteHelper.COLUMN_CATEGORY, category);
        values.put(MySQLiteHelper.COLUMN_DATE, date);
        long insertId = database.insert(MySQLiteHelper.TABLE_EVENTS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        EventInfo newEvent = cursorToEvent(cursor);
        cursor.close();
        return newEvent;
    }

    public EventInfo createEvent(EventInfo event) {
        return createEvent(event.getTitle(), event.getDescription(), event.getCategory(), event.getDate());
    }

    public void deleteEvent(int id) {
        System.out.println("Event deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_EVENTS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public void deleteAllEvents() {
        System.out.println("All events deleted");
        database.delete(MySQLiteHelper.TABLE_EVENTS, null, null);
    }

    public EventInfo getEvent(long id) {
        EventInfo event = null;
        Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENTS,
                allColumns, MySQLiteHelper.COLUMN_ID + "=" + id, null, null, null, null);
        cursor.moveToFirst();
        event = cursorToEvent(cursor);
        cursor.close();
        return event;
    }

    public List<EventInfo> getAllEvents() {
        List<EventInfo> events = new ArrayList<EventInfo>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            EventInfo event = cursorToEvent(cursor);
            events.add(event);
            cursor.moveToNext();
        }
        cursor.close();
        return events;
    }

    public List<EventInfo> getEventsByCategory(int category) {
        List<EventInfo> events = new ArrayList<EventInfo>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_EVENTS,
                allColumns, MySQLiteHelper.COLUMN_CATEGORY + "=" + category, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            EventInfo event = cursorToEvent(cursor);
            events.add(event);
            cursor.moveToNext();
        }
        cursor.close();
        return events;
    }

    private EventInfo cursorToEvent(Cursor cursor) {
        EventInfo event = new EventInfo();
        event.setId(cursor.getLong(0));
        event.setTitle(cursor.getString(1));
        event.setDescription(cursor.getString(2));
        event.setCategory(cursor.getInt(3));
        event.setDate(cursor.getString(4));
        return event;
    }

}

