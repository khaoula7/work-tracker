package com.example.android.worktracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.worktracker.Data.WorkContract.*;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private View inflator;
    //Flag to indicate the user typed a new category
    private boolean newCategory;
    private static final String[] countries = {"Afghanistan", "Albania", "Algeria", "Andorra", "Angola"};


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
                showEditorDialog();
            }
        });
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

        //Access Alert Dialog Edit Texts
        final EditText editName = (EditText) inflator.findViewById(R.id.project_name);
        final AutoCompleteTextView editCategory = (AutoCompleteTextView) inflator.findViewById(R.id.project_category);
        final EditText editEstimatedTime = (EditText) inflator.findViewById(R.id.estimated_time);
        final EditText editPriority = (EditText) inflator.findViewById(R.id.project_priority);
        final EditText editVictoryLine = (EditText) inflator.findViewById(R.id.victory_line);


        /**Dealing with Category AutoCompleteTextView*/
        //Sending a query to get all categories from category table
        Cursor cursor = getContentResolver().query(CategoryEntry.CONTENT_CATEGORY_URI, null, null, null, null);
        //Power category Drop Down menu with names from category table
        CategoryCursorAdapter categoryAdapter = new CategoryCursorAdapter(MainActivity.this, cursor);
        editCategory.setAdapter(categoryAdapter);
        //The listener used to indicate the user has selected an option from the drop down menu
        editCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //User selected an existing category
                newCategory = false;
                //String item = parent.getItemAtPosition(position).toString();
            }
        });
        /**Dealing with victoryLine Edit Text to show date*/
        //returns a Calendar object whose calendar fields have been initialized with the current date and time
        final Calendar myCalendar = Calendar.getInstance();
        //The listener used to indicate the user has finished selecting a date
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
        //Save Button: Save Project into project table, eventually save new category in category table
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Extract information from UI Edit texts
                String projectName = editName.getText().toString().trim();
                String projectCategory = editCategory.getText().toString().trim();
                int  projectEstimatedTime = Integer.parseInt(editEstimatedTime.getText().toString().trim());
                int projectPriority = Integer.parseInt(editPriority.getText().toString().trim());
                String projectVictoryLine = editVictoryLine.getText().toString().trim();
                long category_ID = getCategoryId(newCategory, projectCategory);

                //Put information into ContentValues object
                ContentValues values = new ContentValues();
                values.put(ProjectEntry.COLUMN_NAME, projectName);
                //values.put(ProjectEntry.COLUMN_CATEGORY_ID, id);
                values.put(ProjectEntry.COLUMN_ESTIMATED_TIME, projectEstimatedTime);
                values.put(ProjectEntry.COLUMN_PRIORITY, projectPriority);
                values.put(ProjectEntry.COLUMN_VICTORY_LINE, projectVictoryLine);

                //Toast.makeText(MainActivity.this, projectCategory + " " + projectVictoryLine + " " + newCategory, Toast.LENGTH_SHORT).show();



            }
        });
        //Discard Button
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Close the Alert Dialog
                dialog.dismiss(); }
        });
        builder.show();
    }

    /**
     * Get the _ID of a category from category Table
     * @param newCategory: whether it is a new category to be inserted or an existing one
     * @param projectCategory: name of the category
     */
    private long getCategoryId(boolean newCategory, String projectCategory) {
        long id = -1;
        if(newCategory){
            //Add new Category to table category
            ContentValues values = new ContentValues();
            values.put(CategoryEntry.COLUMN_NAME, projectCategory);
            Uri uri = getContentResolver().insert(CategoryEntry.CONTENT_CATEGORY_URI, values);
            id = ContentUris.parseId(uri);
        }
        else{
            //Get id of existing category from Cursor
        }
        return id;
    }






}
