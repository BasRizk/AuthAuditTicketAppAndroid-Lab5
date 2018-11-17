package com.example.and_lab.lab_5;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogIn extends AppCompatActivity {

    ActionBar actionBar;

    Toolbar m_toolbar;

    String current_username;
    String current_timestamp;
    FeedReaderDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        m_toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(m_toolbar);
        /*
        To use the ActionBar utility methods, call the activity's getSupportActionBar() method.
        This method returns a reference to an appcompat ActionBar object.
        Once you have that reference, you can call any of the ActionBar methods to adjust the
        app bar. For example, to hide the app bar, call ActionBar.hide().
         */
        actionBar = getSupportActionBar();

        /* Initializing the DB (Probably) */
        mDbHelper = new FeedReaderDbHelper(this);


        current_username = "Login-Timeline";
        current_timestamp = "Login_Occurances";
        saveToDB(current_username, current_timestamp);

    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    private void logIn(String username) {

        actionBar.setTitle(username);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                // User chose the "Login" item, show the app settings UI...
                return true;

            case R.id.action_settings:
                // User chose the "Settings" action, mark the current item
                return true;
            case R.id.action_exportCSV:
                if(createCsvFile())
                    Toast.makeText(getApplicationContext(),"CSV file created",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(),"Error creating CSV file",Toast.LENGTH_SHORT).show();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private boolean createCsvFile() {
        String filename = "timestamps.csv";

        //Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + filename);
        Boolean write_successful = false;
        File root;
        try {
            // <span id="IL_AD8" class="IL_AD">check for</span> SD card
            root = Environment.getExternalStorageDirectory();
            Log.i("DB","path.." +root.getAbsolutePath());

            //check sdcard permission
            if (root.canWrite()){
                File fileDir = new File(root.getAbsolutePath());
                fileDir.mkdirs();

                File file= new File(fileDir, filename);
                FileWriter filewriter = new FileWriter(file);
                BufferedWriter out = new BufferedWriter(filewriter);

                out.write("Username,Timestamp");
                ArrayList<String> tableEntries = readFromDB();
                for(String row : tableEntries) {
                    out.write(row);
                }
                out.close();

                write_successful = true;
            }
        } catch (IOException e) {
            Log.e("ERROR:---", "Could not write file to SDCard" + e.getMessage());
            write_successful = false;
        }
        return write_successful;
    }

    private ArrayList<String> readFromDB() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP
        };

        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { "Username" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP + " DESC";

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        ArrayList<String> rows = new ArrayList();
        while(cursor.moveToNext()) {
            String username = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME));
            String timestamp = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP));
            String row = username + "," + timestamp;
            Log.d("DB", "Read entry from db = " + row);
            rows.add(row);
        }
        cursor.close();

        Log.d("DB", "DB finished reading data");

        return rows;
    }

    private void saveToDB(String username, String timestamp) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME, current_username);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP, current_timestamp);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
        Toast.makeText(getApplicationContext(),"New Entry in the DB with ID = " + newRowId,Toast.LENGTH_SHORT).show();

    }

}
