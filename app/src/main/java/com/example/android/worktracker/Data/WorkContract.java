package com.example.android.worktracker.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

public final class WorkContract {
    //Empty contract
    private WorkContract(){}
    //Content Authority
    public static final String CONTENT_AUTHORITY = "com.example.android.worktracker";
    // Base Content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
    //Possible
    public static final String PATH_PROJECTS ="project";
    public static final  String PATH_CATEGORIES = "category";
    public static final String PATH_WORKED_TIME = "worked_time";
    /**
     * Inner class that defines constant values for the project table.
     * Each entry in the table represents a single project.
     */
    public static final class ProjectEntry implements BaseColumns{
        //Content URI to access project data in the provider
        public static final Uri CONTENT_PROJECT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PROJECTS);

        //The MIME type of the CONTENT_PROJECT_URI for a list of projects.
        //"vnd.android.cursor.dir/com.example.android.worktracker/project"
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROJECTS;

        //The MIME type of the CONTENT_PROJECT_URI for a single projects.
        //"vnd.android.cursor.item/com.example.android.worktracker/project"
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROJECTS;

        //Define CONSTANTS for Project table
        public static final String TABLE_NAME = "project";
        public static final String _ID = BaseColumns._ID;
        public  static final String COLUMN_NAME = "name";
        public  static final String COLUMN_PRIORITY = "priority";
        public  static final String COLUMN_ESTIMATED_TIME = "estimated_time";
        public  static final String COLUMN_VICTORY_LINE = "victory_line";
        public  static final String COLUMN_ARCHIVED = "archived";
        public  static final String COLUMN_CATEGORY_ID = "category_id";
        public  static final String COLUMN_TIME_ID = "time_id";
    }

    /**
     * Inner class that defines constant values for the category table.
     * Each entry in the table represents a single category.
     */
    public static final class CategoryEntry implements BaseColumns {
        //Content URI to access category data in the provider
        public static final Uri CONTENT_CATEGORY_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CATEGORIES);

        //The MIME type of the CONTENT_CATEGORY_URI for a list of category.
        //"vnd.android.cursor.dir/com.example.android.worktracker/category"
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORIES;

        //The MIME type of the CONTENT_CATEGORY_URI for a single category.
        //"vnd.android.cursor.item/com.example.android.worktracker/category"
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORIES;

        //Define CONSTANTS for category table
        public static final String TABLE_NAME = "category";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
    }

    public static final class WorkedTime implements BaseColumns{
        //Content URI to access category data in the provider
        public static final Uri CONTENT_WORKED_TIME_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WORKED_TIME);

        //The MIME type of the CONTENT_CATEGORY_URI for a list of worked_time.
        //"vnd.android.cursor.dir/com.example.android.worktracker/worked_time"
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKED_TIME;

        //The MIME type of the CONTENT_CATEGORY_URI for a single worked_time.
        //"vnd.android.cursor.item/com.example.android.worktracker/worked_time"
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKED_TIME;

        //Defining CONSTANTS for worked_time table
        public static final String TABLE_NAME = "worked_time";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_DAILY_TIME = "daily_time";
        public static final String COLUMN_WEEKLY_TIME = "weekly_time";
        public static final String COLUMN_MONTHLY_TIME = "monthly_time";
        public static final String COLUMN_YEARLY_TIME = "yearly_time";
    }
}
