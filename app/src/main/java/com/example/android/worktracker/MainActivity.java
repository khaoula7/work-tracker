package com.example.android.worktracker;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.worktracker.Data.WorkContract.*;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private View inflator;
    private static final String[] countries = {"Afghanistan", "Albania", "Algeria", "Andorra", "Angola"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Click on Floating Action button will open editor_dialog
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
        //Inflate and set the layout for the AlertDialog
        inflator = getLayoutInflater().inflate(R.layout.dialog_editor, null);
        builder.setView(inflator);
        final AutoCompleteTextView category = (AutoCompleteTextView) inflator.findViewById(R.id.project_category);
        ArrayAdapter<String> array = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, countries);
        category.setAdapter(array);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, category.getText().toString(), Toast.LENGTH_SHORT).show();
                //saveProject();
            }
        });
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
        values.put(ProjectEntry.COLUMN_ESTIMATED_TIME, projectEstimatedTime);
        values.put(ProjectEntry.COLUMN_PRIORITY, projectPriority);
    }
}
