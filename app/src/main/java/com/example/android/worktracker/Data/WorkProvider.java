package com.example.android.worktracker.Data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
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


