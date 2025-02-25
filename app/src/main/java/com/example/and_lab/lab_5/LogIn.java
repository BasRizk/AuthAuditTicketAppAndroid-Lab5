package com.example.and_lab.lab_5;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LogIn extends AppCompatActivity {

    ActionBar actionBar;
    Toolbar m_toolbar;
    Menu menu;

    private String currentUsername;
    private String currentTimestamp;
    private String currentLongitude;
    private String currentLatitude;
    private boolean isLoggedIn;
    private boolean isLocationEnabled;

    private LocationListener locationListener;
    private FeedReaderDbHelper mDbHelper;

    private ListView listView;
    private LogInRecordAdapter mAdapter;
    private Button clearButton;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        listView = findViewById(R.id.record_view);
        clearButton = findViewById(R.id.clear_b);

        m_toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(m_toolbar);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                currentLongitude = Double.toString(location.getLongitude());
                currentLatitude = Double.toString(location.getLatitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        setCurrentLocation();

        /*
        To use the ActionBar utility methods, call the activity's getSupportActionBar() method.
        This method returns a reference to an appcompat ActionBar object.
        Once you have that reference, you can call any of the ActionBar methods to adjust the
        app bar. For example, to hide the app bar, call ActionBar.hide().
         */
        actionBar = getSupportActionBar();

        /* Initializing the DB instance*/
        mDbHelper = new FeedReaderDbHelper(this);

    }

    @Override
    protected void onDestroy() {

        if(currentUsername != null)
            saveUserInstance();
        mDbHelper.close();
        super.onDestroy();
    }

    private void saveUserInstance() {
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("REGISTERED_USERNAME", currentUsername);
        if(isLocationEnabled)
            editor.putString("REGISTERED_LOCATION_STATUS", "true");
        else
            editor.putString("REGISTERED_LOCATION_STATUS", "false");

        editor.commit();

        if(currentUsername != null)
            Log.d("LOGIN_LOGOUT","Username saved into the DB:" + currentUsername);
        else
            Log.d("LOGIN_LOGOUT", "Erasing username saved into the DB");
    }

    private void saveLoggedInUserToDB() {
        currentTimestamp = getCurrentTimestamp();
        Log.i("locationInfo", "\n status: " + isLocationEnabled + "\n long:" + currentLongitude + "\n lat: " + currentLatitude);
        saveToDB();
    }

    private void setCurrentLocation() {
        LocationManager lm = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy\nhh:mm:ss", Locale.US);
        return s.format(new Date());
    }


    /* Action Bar functions methods */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_options, menu);
        this.menu = menu;

        isLoggedIn = false;
        isLocationEnabled = false;

        /* Shared Preferences */
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        currentUsername = sharedPref.getString("REGISTERED_USERNAME", null);
        String locationStatus = sharedPref.getString("REGISTERED_LOCATION_STATUS", null);
        if(locationStatus.equals("true"))
            isLocationEnabled = true;
        else
            isLocationEnabled = false;

        if(currentUsername != null) {
            loginSetup();
        }

        if(!isLoggedIn) {
            listView.setVisibility(View.INVISIBLE);
            clearButton.setVisibility(View.INVISIBLE);
            MenuItem action_CSV = menu.findItem(R.id.action_exportCSV);
            action_CSV.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_login:
                setCurrentLocation();
                loginClicked();
               // if(isLoggedIn)      // TODO why do we use this?
                 //   listView.setVisibility(View.VISIBLE);
                return true;

            case R.id.action_settings:
                setCurrentLocation();
                settingsClicked();
                return true;

            case R.id.action_user:
                return true;

            case R.id.action_logout:
                setCurrentLocation();
                logoutClicked();
                if(!isLoggedIn) {
                    listView.setVisibility(View.INVISIBLE);
                    clearButton.setVisibility(View.INVISIBLE);
                    MenuItem action_CSV = menu.findItem(R.id.action_exportCSV);
                    action_CSV.setVisible(false);
                }
                return true;

            case R.id.action_exportCSV:
                if (createCsvFile())
                    Toast.makeText(getApplicationContext(), "CSV file created", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Error creating CSV file", Toast.LENGTH_SHORT).show();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void loginSetup() {
        MenuItem action_login = menu.findItem(R.id.action_login);
        MenuItem action_CSV = menu.findItem(R.id.action_exportCSV);
        MenuItem action_user = menu.findItem(R.id.action_user);
        clearButton.setVisibility(View.VISIBLE);
        action_user.setTitle(currentUsername);
        action_login.setVisible(false);
        action_CSV.setVisible(true);
        action_user.setVisible(true);
        isLoggedIn = true;
    }

    public void loginClicked() {
        LayoutInflater li = LayoutInflater.from(this);
        View prompt = li.inflate(R.layout.login_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(prompt);
        final EditText user = prompt.findViewById(R.id.login_name);
        //user.setText(Login_USER); //login_USER is loaded from previous session (optional)
        alertDialogBuilder.setTitle("Login");
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        currentUsername = user.getText().toString();
                        saveUserInstance();
                        saveLoggedInUserToDB();
                        loginSetup();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        alertDialogBuilder.show();

    }

    public void settingsClicked() {
        LayoutInflater li = LayoutInflater.from(this);
        final View prompt = li.inflate(R.layout.settings_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(prompt);
        alertDialogBuilder.setTitle("Settings");

        RadioGroup radioGroup = prompt.findViewById(R.id.radio_location_group);
        if(isLocationEnabled) {
            radioGroup.check(R.id.enable_location);
        } else {
            radioGroup.check(R.id.disable_location);
        }

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RadioButton enable_location = prompt.findViewById(R.id.enable_location);
                        if(enable_location.isChecked()) {
                            isLocationEnabled = true;
                        } else {
                            isLocationEnabled = false;
                        }

                        saveUserInstance();

                        Log.i("locationInfo", "" + isLocationEnabled);

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Log.i("locationInfo", "" + isLocationEnabled);
            }
        });

        alertDialogBuilder.show();
    }

    public void logoutClicked() {
        currentUsername = null;
        saveUserInstance();

        MenuItem action_login = menu.findItem(R.id.action_login);
        MenuItem action_user = menu.findItem(R.id.action_user);
        action_user.setTitle(currentUsername);
        action_login.setVisible(true);
        action_user.setVisible(false);
        isLoggedIn = false;
    }


    /* DB Operations and file creation methods */
    private boolean createCsvFile() {
        String filename = "timestamps.csv";

        ArrayList<LogInRecord> recordList = new ArrayList<>();
        mAdapter = new LogInRecordAdapter(this,recordList);
        listView.setAdapter(mAdapter);
        recordList.add(new LogInRecord("username" , "timestamp", "longitude", "latitude"));
        listView.setVisibility(View.VISIBLE);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + filename);
        Boolean write_successful = false;
        File root;
        try {
            // <span id="IL_AD8" class="IL_AD">check for</span> SD card
            root = Environment.getExternalStorageDirectory();
            Log.i("DB", "path.." + root.getAbsolutePath());

            //check sdcard permission
            if (root.canWrite()) {
                File fileDir = new File(root.getAbsolutePath());
                fileDir.mkdirs();

                File file = new File(fileDir, filename);
                FileWriter filewriter = new FileWriter(file);
                BufferedWriter out = new BufferedWriter(filewriter);

                out.write("Username,Timestamp,Longitude,Latitude");
                ArrayList<String> tableEntries = readFromDB();
                for (String row : tableEntries) {
                    out.write(row);
                    String[] row_splitted = row.split(",");
                    recordList.add(new LogInRecord(row_splitted[0], row_splitted[1], row_splitted[2], row_splitted[3]));
                    Log.v("DBRead", "" + row);
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
                FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP,
                FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE
        };

        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = {"Username"};

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

        ArrayList<String> rows = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String username = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME));
            String timestamp = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP));
            String longitude = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE));
            String latitude = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE));
            String row = username + "," + timestamp + "," + longitude + "," + latitude;
            Log.d("DB", "Read entry from db = " + row);
            rows.add(row);
        }
        cursor.close();

        Log.d("DB", "DB finished reading data");

        return rows;
    }

    private void saveToDB() {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_USERNAME, currentUsername);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP, currentTimestamp);
        if(isLocationEnabled) {
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE, currentLongitude);
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE, currentLatitude);
        } else {
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LONGITUDE, "NAN");
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LATITUDE, "NAN");
        }


        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
        Toast.makeText(getApplicationContext(), "New Entry in the DB with ID = " + newRowId, Toast.LENGTH_SHORT).show();

    }

    /* OnClick Methods */
    public void clearDB(View view) {
        // Define 'where' part of query.
        //String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        // Specify arguments in placeholder order.
        //String[] selectionArgs = { "MyTitle" };
        // Issue SQL statement.

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int deletedRows = db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, null, null);
        if(deletedRows > 0) {
            Toast.makeText(getApplicationContext(), deletedRows + " rows were deleted from the DB.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No rows to be cleared.", Toast.LENGTH_SHORT).show();
        }
    }

}
