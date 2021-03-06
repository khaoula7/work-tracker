package com.example.android.worktracker.Data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.net.URI;

import static com.example.android.worktracker.Data.WorkContract.*;

public class WorkProvider extends ContentProvider {
    //Tag for the log messages
    private static final String LOG_TAG = WorkProvider.class.getSimpleName();
    // Database Helper Object
    private WorkDbHelper mWorkDbHelper;

    // URI matcher code of the content URI for the project table
    private static final int PROJECT = 100;
    // URI matcher code of the content URI for a single project
    private  static final int PROJECT_ID = 101;
    // URI matcher code of the content URI for the category table
    private static final int CATEGORY = 200;
    // URI matcher code of the content URI for a single category
    private static final int CATEGORY_ID = 201;

    // UriMatcher object
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // Add all of the content URI patterns that the provider should recognize.
        // All paths added to the UriMatcher have a corresponding code to return when a match is found.
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PROJECTS, PROJECT);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PROJECTS + "/#", PROJECT_ID);

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_CATEGORIES, CATEGORY);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_CATEGORIES + "/#", CATEGORY_ID);
    }

    /**
     * Initialize the provider and the database helper object to gain access to the database.
     */
    @Override
    public boolean onCreate() {
        mWorkDbHelper = new WorkDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,  String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase db = mWorkDbHelper.getReadableDatabase();
        //The result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        final int match = sUriMatcher.match(uri);
        switch (match){
            case CATEGORY :
                //Perform database query on category table
                cursor = db.query(CategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CATEGORY_ID:
                // Define selection statement as "_ID=?"
                selection = CategoryEntry._ID + "=?";
                //Extract the ID from the URI and add it in selectionArgs.
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor =db.query(CategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PROJECT:
                //Perform database query on category table
                cursor = db.query(ProjectEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PROJECT_ID:
                // Define selection statement as "_ID=?"
                selection = ProjectEntry._ID + "=?";
                //Extract the ID from the URI and add it in selectionArgs.
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor =db.query(ProjectEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //Specify the uri to watch If the data at this URI changes, then we know we need to update the cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case CATEGORY:
                return insertCategory(uri, values);
            case PROJECT:
                return  insertProject(uri, values);
            default:
                throw new IllegalArgumentException("insertion is not supported for this uri: " + uri);
        }

    }

    private Uri insertProject(Uri uri, ContentValues values) {
        //Get a writable database
        SQLiteDatabase db = mWorkDbHelper.getWritableDatabase();
        //Call database insert method
        long id = db.insert(ProjectEntry.TABLE_NAME, null, values);
        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertCategory(Uri uri, ContentValues values) {
        SQLiteDatabase db = mWorkDbHelper.getWritableDatabase();
        long id = db.insert(CategoryEntry.TABLE_NAME, null, values);
        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        //Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri); //match is the code of the content URI
        switch(match){
            case PROJECT:
                return ProjectEntry.CONTENT_LIST_TYPE;
            case PROJECT_ID:
                return ProjectEntry.CONTENT_ITEM_TYPE;
            case CATEGORY:
                return CategoryEntry.CONTENT_LIST_TYPE;
            case CATEGORY_ID:
                return CategoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);

        }
    }
}


