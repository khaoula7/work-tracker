package com.example.android.worktracker.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.worktracker.Data.WorkContract.*;

public class WorkDbHelper extends SQLiteOpenHelper {
    //Name of the database file
    public static final int DATABASE_VERSION = 1;
    //Database version. If you change the database schema you must increment the database version
    public static final String DATABASE_NAME = "worktracker.db";

    //Constructor
    public WorkDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL statement to create project table
        String SQL_CREATE_PROJECT = "CREATE TABLE " + ProjectEntry.TABLE_NAME + " ("
                + ProjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProjectEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + ProjectEntry.COLUMN_PRIORITY + " INTEGER, "
                + ProjectEntry.COLUMN_ESTIMATED_TIME + " INTEGER, "
                + ProjectEntry.COLUMN_VICTORY_LINE + " DATE, "
                + ProjectEntry.COLUMN_ARCHIVED + " INTEGER, "
                + ProjectEntry.COLUMN_CATEGORY_ID + " INTEGER, "
                + ProjectEntry.COLUMN_TIME_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY ( " + ProjectEntry.COLUMN_CATEGORY_ID + " ) REFERENCES " + CategoryEntry._ID
                + ", FOREIGN KEY ( " + ProjectEntry.COLUMN_TIME_ID + " ) REFERENCES " + WorkedTimeEntry._ID
                + " )";

        //SQL statement to create category table
        String SQL_CREATE_CATEGORY = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " ("
                + CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CategoryEntry.COLUMN_NAME + " TEXT NOT NULL )";


        //SQL statement to create project table
        String SQL_CREATE_WORKED_TIME = "CREATE TABLE " + WorkedTimeEntry.TABLE_NAME + " ("
                + WorkedTimeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WorkedTimeEntry.COLUMN_DAILY_TIME + " INTEGER DEFAULT 0, "
                + WorkedTimeEntry.COLUMN_WEEKLY_TIME + " INTEGER DEFAULT 0, "
                + WorkedTimeEntry.COLUMN_MONTHLY_TIME + " INTEGER DEFAULT 0, "
                + WorkedTimeEntry.COLUMN_YEARLY_TIME + " INTEGER DEFAULT 0 ) ";

        //Execute SQL Create statements
        db.execSQL(SQL_CREATE_PROJECT);
        db.execSQL(SQL_CREATE_CATEGORY);
        db.execSQL(SQL_CREATE_WORKED_TIME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
