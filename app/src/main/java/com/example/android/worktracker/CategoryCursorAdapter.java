package com.example.android.worktracker;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.android.worktracker.Data.WorkContract;
import com.example.android.worktracker.Data.WorkContract.*;


public class CategoryCursorAdapter extends CursorAdapter implements Filterable {
    private ContentResolver mContent;

    private static final int COLUMN_DISPLAY_NAME = 1;
    public static final String[] CATEGORY_PROJECTION = new String[] {
            CategoryEntry._ID,
            CategoryEntry.COLUMN_NAME
    };

    public CategoryCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
        mContent = context.getContentResolver();
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final TextView view = (TextView) inflater.inflate(
                android.R.layout.simple_dropdown_item_1line, parent, false);
        view.setText(cursor.getString(COLUMN_DISPLAY_NAME));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view).setText(cursor.getString(COLUMN_DISPLAY_NAME));
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(COLUMN_DISPLAY_NAME);
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        FilterQueryProvider filter = getFilterQueryProvider();
        if (filter != null) {
            return filter.runQuery(constraint);
        }

        Uri uri = Uri.withAppendedPath(
                CategoryEntry.CONTENT_CATEGORY_URI,
                Uri.encode(constraint.toString()));
        return mContent.query(uri, CATEGORY_PROJECTION, null, null, null);
    }
}
