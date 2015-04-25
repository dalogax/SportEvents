package com.dalogax.sportevents;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DATE = "date";

    private static final String DATABASE_NAME = "sportevents";
    private static final int DATABASE_VERSION = 9;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_EVENTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null, " + COLUMN_DESCRIPTION + " text not null, "
            + COLUMN_CATEGORY + " integer not null, " + COLUMN_DATE + " text);";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

}
