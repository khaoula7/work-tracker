package com.example.android.worktracker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.worktracker.Data.WorkContract;
import com.example.android.worktracker.Data.WorkContract.CategoryEntry;

/**
 * WorkCursorAdapter adapts data coming from database as a cursor
 * to be properly displayed in UI according to list_view_item
 */
public class WorkCursorAdapter extends CursorAdapter {
    Context mContext;
    //Default Constructor
    public WorkCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
        mContext = context;
    }
    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     * @param parent  The parent to which the new view is attached to
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_view_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Find views to populate them
        TextView nameTV = (TextView) view.findViewById(R.id.name_tv);
        TextView categoryTV = (TextView) view.findViewById(R.id.category_tv);
        TextView todayTV = (TextView) view.findViewById(R.id.today_time);
        TextView weeklyTV = (TextView) view.findViewById(R.id.weekly_time);
        TextView monthlyTV = (TextView) view.findViewById(R.id.monthly_time);
        ImageView playIV = (ImageView) view.findViewById(R.id.play_img);

        //Find id columns of information we're interested in
        int idColumnIndex = cursor.getColumnIndex(WorkContract.ProjectEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(WorkContract.ProjectEntry.COLUMN_NAME);
        int categoryIdColumnIndex = cursor.getColumnIndex(WorkContract.ProjectEntry.COLUMN_CATEGORY_ID);
        int todayColumnIndex = cursor.getColumnIndex(WorkContract.ProjectEntry.COLUMN_DAILY_TIME);
        int weeklyColumnIndex = cursor.getColumnIndex(WorkContract.ProjectEntry.COLUMN_WEEKLY_TIME);
        int monthlyColumnIndex = cursor.getColumnIndex(WorkContract.ProjectEntry.COLUMN_MONTHLY_TIME);

        //Extract values from cursor
        int id = cursor.getInt(idColumnIndex);
        int categoryID = cursor.getInt(categoryIdColumnIndex);
        //String category = Integer.toString(categoryID);
        String name = cursor.getString(nameColumnIndex);
        int todayTime = cursor.getInt(todayColumnIndex);
        String today = Integer.toString(todayTime);
        int weekTime = cursor.getInt(weeklyColumnIndex);
        String week = Integer.toString(weekTime);
        int monthTime = cursor.getInt(monthlyColumnIndex);
        String month = Integer.toString(monthTime);
        //Query Category table to get category name
        String [] projection = {CategoryEntry.COLUMN_NAME};
        String selection = CategoryEntry._ID + "=?";
        String [] selectionArgs = {Integer.toString(categoryID)};
        Cursor cursor1 = context.getContentResolver().query(CategoryEntry.CONTENT_CATEGORY_URI,
                projection, selection, selectionArgs, null);
        try {
            if(cursor1.moveToFirst()){
                int catColumnIndex = cursor1.getColumnIndex(CategoryEntry.COLUMN_NAME);
                String category = cursor1.getString(catColumnIndex);
                categoryTV.setText(category);
            }
        }finally {
            cursor1.close();
        }

        //Set values from cursor to Text Views
        nameTV.setText(name);
        todayTV.setText("00:00");
        weeklyTV.setText("00:00");
        monthlyTV.setText("00:00");
    }
}
