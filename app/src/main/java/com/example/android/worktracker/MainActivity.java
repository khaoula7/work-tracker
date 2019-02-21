package com.example.android.worktracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
        final AutoCompleteTextView category = (AutoCompleteTextView) inflator.findViewById(R.id.project_category);
        final EditText editEstimatedTime = (EditText) inflator.findViewById(R.id.estimated_time);
        final EditText editPriority = (EditText) findViewById(R.id.project_priority);
        final EditText editVictoryLine = (EditText) inflator.findViewById(R.id.victory_line);

        /**Dealing with Category AutoCompleteTextView*/
        //Power category Drop Down menu with names from category table
        final ArrayAdapter<String> array = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, countries);
        category.setAdapter(array);
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, item + " " +String.valueOf(position) + " "+ String.valueOf(id), Toast.LENGTH_LONG).show();
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
                String victoryDate =  month + "/" + dayOfMonth + "/" + year ;
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
        //Save Button
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Save Project
                Toast.makeText(MainActivity.this, category.getText().toString() , Toast.LENGTH_SHORT).show();
                //saveProject();
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
     * Extract information from editor_dialog input texts and save the new project in database
     */
    private void saveProject() {
        //Extract information from input text
        EditText editName = (EditText) findViewById(R.id.project_name);
        AutoCompleteTextView editCategory = (AutoCompleteTextView) findViewById(R.id.project_category);
        EditText editEstimatedTime = (EditText) findViewById(R.id.estimated_time);
        EditText editPriority = (EditText) findViewById(R.id.project_priority);
        String projectName = editName.getText().toString().trim();
        String projectCategory = editCategory.getText().toString().trim();
        int  projectEstimatedTime = Integer.parseInt(editEstimatedTime.getText().toString().trim());
        int projectPriority = Integer.parseInt(editPriority.getText().toString().trim());

        //Put information into ContentValues object
        ContentValues values = new ContentValues();
        values.put(ProjectEntry.COLUMN_NAME, projectName);
        //ProjectCategory
        values.put(ProjectEntry.COLUMN_ESTIMATED_TIME, projectEstimatedTime);
        values.put(ProjectEntry.COLUMN_PRIORITY, projectPriority);
        //VictoryLine

    }
}
