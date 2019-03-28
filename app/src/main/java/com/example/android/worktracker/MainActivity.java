package com.example.android.worktracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.worktracker.Data.WorkContract;
import com.example.android.worktracker.Data.WorkContract.*;
import com.example.android.worktracker.Data.WorkDbHelper;

import java.util.Calendar;

import static java.lang.Math.toIntExact;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private FloatingActionButton fab;
    private View inflator;
    private SimpleCursorAdapter mCategoryAdapter;
    private WorkCursorAdapter mWorkAdapter;
    //Flag to indicate the user typed a new category
    private boolean newCategory;
    private int categoryId;
    private static final int WORK_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //The click on Floating Action button will open editor_dialog
        fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCategory = true;
                categoryId = -1;
                showEditorDialog();
            }
        });
        //Find List View and Empty View
        ListView listView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        //Show Empty View when List View has 0 items
        listView.setEmptyView(emptyView);
        //Instanciate the Cursor Adapter. There is no data yet (until the loader finishes) so pass in null for the cursor.
        mWorkAdapter = new WorkCursorAdapter(this, null);
        listView.setAdapter(mWorkAdapter);
        //Initialize and activate loader
        getLoaderManager().initLoader(WORK_LOADER, null, this);
    }

    /**
     * Builds the editor_dialog to allow user to create a new project
     */
    private void showEditorDialog() {
        //Create the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Inflate the layout
        inflator = getLayoutInflater().inflate(R.layout.dialog_editor, null);
        //set the layout for the AlertDialog
        builder.setView(inflator);
        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);
        final AlertDialog alertDialog = builder.create();
        //Access Alert Dialog Edit Texts
        final EditText editName = (EditText) inflator.findViewById(R.id.project_name);
        final AutoCompleteTextView editCategory = (AutoCompleteTextView) inflator.findViewById(R.id.project_category);
        final EditText editEstimatedTime = (EditText) inflator.findViewById(R.id.estimated_time);
        final EditText editPriority = (EditText) inflator.findViewById(R.id.project_priority);
        final EditText editVictoryLine = (EditText) inflator.findViewById(R.id.victory_line);
        //Category AutoCompletion
        autoCompleteCategory(editCategory);
        //Calendar object for VictoryLine Edit Text whose calendar fields have been initialized with the current date and time
        showCalendar(editVictoryLine);
        //Define Positive and Negative buttons
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //Save Button: Save Project into project table, eventually save new category in category table
                Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Put information into a ContentValues object
                        ContentValues values = new ContentValues();
                        //Get project name
                        String projectName = editName.getText().toString().trim();
                        if (TextUtils.isEmpty(projectName)) {
                            editName.setError("Project Name cannot be blank");
                        }
                        //Get project category
                        String projectCategory = editCategory.getText().toString().trim();
                        //Get project estimated time
                        String estimatedTimeString = editEstimatedTime.getText().toString().trim();
                        if(!TextUtils.isEmpty(estimatedTimeString)){
                            int projectEstimatedTime = Integer.parseInt(estimatedTimeString);
                            values.put(ProjectEntry.COLUMN_ESTIMATED_TIME, projectEstimatedTime);
                        }
                        //Get project priority
                        String priorityString = editPriority.getText().toString().trim();
                        if(!TextUtils.isEmpty(priorityString)){
                            int projectPriority = Integer.parseInt(priorityString);
                            values.put(ProjectEntry.COLUMN_PRIORITY, projectPriority);
                        }
                        //Get project VictoryLine
                        String projectVictoryLine = editVictoryLine.getText().toString().trim();
                        //If user enters a new category then insert it in category table
                        if (newCategory) {
                            categoryId = insertCategory(projectCategory);
                        }
                        values.put(ProjectEntry.COLUMN_NAME, projectName);
                        values.put(ProjectEntry.COLUMN_CATEGORY_ID, categoryId);
                        values.put(ProjectEntry.COLUMN_VICTORY_LINE, projectVictoryLine);
                        Uri uri = getContentResolver().insert(ProjectEntry.CONTENT_PROJECT_URI, values);
                        if(uri == null){
                            Toast.makeText(MainActivity.this, "Error with inserting project", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Project inserted successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //Cancel Button
                Button NegativeButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                NegativeButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Close the Alert Dialog
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    /**
     *Filters category names that starts with characters typed by user in AutoCompleteTextView
     * @param str: filter characters
     * @return cursor that contains the result of filter
     */
    private Cursor getCursor(CharSequence str) {
        String selection = CategoryEntry.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {str + "%"};
        return getContentResolver().query(CategoryEntry.CONTENT_CATEGORY_URI, null, selection, selectionArgs, null);
    }

    /**
     * Insert a new Category in category Table
     * @param projectCategory: name of the category
     */
    private int insertCategory(String projectCategory) {
            //Add new Category to table category
            ContentValues values = new ContentValues();
            values.put(CategoryEntry.COLUMN_NAME, projectCategory);
            Uri uri = getContentResolver().insert(CategoryEntry.CONTENT_CATEGORY_URI, values);
            long id = ContentUris.parseId(uri);
            //Safely cast long to int
           return (int) id;
    }

    /**
     * AutoComplete category from a cursor
     * @param editCategory: AutoComplete TextView
     */
    void autoCompleteCategory(final AutoCompleteTextView editCategory){
        //Sending a query to get all categories from category table
        final Cursor cursor = getContentResolver().query(CategoryEntry.CONTENT_CATEGORY_URI, null,
                null, null, null);
        //Power category Drop Down menu with names from category table
        mCategoryAdapter = new SimpleCursorAdapter(MainActivity.this, android.R.layout.simple_list_item_1,
                null, new String []{CategoryEntry.COLUMN_NAME}, new int[]{android.R.id.text1}, 0);
        editCategory.setAdapter(mCategoryAdapter);

        // FilterQueryProvider is an interface. Its runQuery callback is executed every time we change something in AutoCompleteTextView.
        mCategoryAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return getCursor(str);
            } });
        //when an item appears in Drop Down, convertToString() is called.
        mCategoryAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                int nameIndex = cur.getColumnIndex(CategoryEntry.COLUMN_NAME);
                //Toast.makeText(MainActivity.this, cur.getString(nameIndex), Toast.LENGTH_SHORT).show();
                return cur.getString(nameIndex);

            }});
        /*The listener used to indicate the user has selected an option from the drop down menu*/
        editCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //User selected an existing category
                newCategory = false;
                categoryId = (int) id;
            }
        });
    }

    /**
     * Shows a date dialog for the user to select the VictoryLine date
     * @param editVictoryLine: Edit Text
     */
    void showCalendar(final EditText editVictoryLine){
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Setting the calendar field values
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String victoryDate =  ++month + "/" + dayOfMonth + "/" + year ;
                editVictoryLine.setText(victoryDate);
            }
        };
        //The click in VictoryLine Edit Text shows a Date Picker Dialog
            editVictoryLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting year, month and day from the Calendar
                int year = myCalendar.get(Calendar.YEAR);
                int month = myCalendar.get(Calendar.MONTH);
                int day = myCalendar.get(Calendar.DAY_OF_MONTH);
                //Creating Date Picker Dialog
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year, month, day);
                //Setting the background of  Date Picker Dialog  to Transparent
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    /**
     * Create loader and define data to be queried from Content Provider
     * @param id
     * @param args
     * @return
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection that specifies the columns from the projection we care about.
        String [] projection = {
                ProjectEntry._ID,
                ProjectEntry.COLUMN_NAME,
                ProjectEntry.COLUMN_CATEGORY_ID,
                ProjectEntry.COLUMN_DAILY_TIME,
                ProjectEntry.COLUMN_WEEKLY_TIME,
                ProjectEntry.COLUMN_MONTHLY_TIME
        };
        //Send Query to the Content Provider
        return new CursorLoader(this, ProjectEntry.CONTENT_PROJECT_URI, projection, null, null, null);
    }

    /**
     * Called when loader has finished loading data and has a cursor
     * @param loader
     * @param data: updated cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Update Work Cursor Adapter with this new cursor containing updated project data
        mWorkAdapter.swapCursor(data);
    }

    /**
     * Called when loader is being destroyed
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Data needs to be deleted
        mWorkAdapter.swapCursor(null);
    }
}
