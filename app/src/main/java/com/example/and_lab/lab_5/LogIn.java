package com.example.and_lab.lab_5;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LogIn extends AppCompatActivity {

    ActionBar actionBar;
    Toolbar m_toolbar;
    Menu menu;
    String username = "user";

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
    }

    private void logIn(String username) {

        actionBar.setTitle(username);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_options, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                loginClicked();
                return true;

            case R.id.action_settings:
                settingsClicked();
                return true;

            case R.id.action_user:
//                userClicked();
                return true;

            case R.id.action_logout:
                logoutClicked();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void loginClicked(){
        LayoutInflater li = LayoutInflater.from(this);
        View prompt = li.inflate(R.layout.login_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(prompt);
        final EditText user = (EditText) prompt.findViewById(R.id.login_name);
        //user.setText(Login_USER); //login_USER is loaded from previous session (optional)
        alertDialogBuilder.setTitle("Login");
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        username = user.getText().toString();
                        MenuItem action_login = menu.findItem(R.id.action_login);
                        MenuItem action_user = menu.findItem(R.id.action_user);
                        action_user.setTitle(username);
                        action_login.setVisible(false);
                        action_user.setVisible(true);
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });

        alertDialogBuilder.show();
    }

    public void settingsClicked() {
        LayoutInflater li = LayoutInflater.from(this);
        View prompt = li.inflate(R.layout.settings_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(prompt);
        alertDialogBuilder.setTitle("Settings");
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
//                        String username = user.getText().toString();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });

        alertDialogBuilder.show();
    }

    public void logoutClicked() {
        username = "user";
        MenuItem action_login = menu.findItem(R.id.action_login);
        MenuItem action_user = menu.findItem(R.id.action_user);
        action_user.setTitle(username);
        action_login.setVisible(true);
        action_user.setVisible(false);
    }

//    public void userClicked() {
//        LayoutInflater li = LayoutInflater.from(this);
//        View prompt = li.inflate(R.layout.settings_dialog, null);
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setView(prompt);
//        alertDialogBuilder.setTitle("Settings");
//        alertDialogBuilder.setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id)
//                    {
////                        String username = user.getText().toString();
//                    }
//                });
//
//        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id)
//            {
//                dialog.cancel();
//            }
//        });
//
//        alertDialogBuilder.show();
//    }
}
