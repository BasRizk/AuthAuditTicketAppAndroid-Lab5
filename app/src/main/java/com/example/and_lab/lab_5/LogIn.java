package com.example.and_lab.lab_5;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class LogIn extends AppCompatActivity {

    ActionBar actionBar;

    Toolbar m_toolbar;

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
}
