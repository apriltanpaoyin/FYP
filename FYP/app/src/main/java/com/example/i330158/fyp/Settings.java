package com.example.i330158.fyp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by Pao Yin Tan on 28/01/2018.
 *
 * This is the settings page. There are 4 options to choose from: Set Up Quick Text,
 * Pair With Camera, Enable/Disable Alarm, Logout.
 */

public class Settings extends Activity implements AdapterView.OnItemClickListener{
    private ListView settings;
    private String[] settingOptions = {"Set Up Quick Text", "Pair With Camera", "Toggle Alarm", "Logout"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);

        settings = (ListView)findViewById(R.id.settings);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, settingOptions);
        settings.setAdapter(adapter);
        settings.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long l) {
        switch (settingOptions[pos]) {
            case "Set Up Quick Text":
                Intent textIntent = new Intent(Settings.this, QuickText.class);
                startActivity(textIntent);
                break;
            case "Pair With Camera":
                Intent camIntent = new Intent(Settings.this, PairCamera.class);
                startActivity(camIntent);
                break;
            case "Toggle Alarm":
                Intent alarmIntent = new Intent(Settings.this, ToggleAlarm.class);
                startActivity(alarmIntent);
                break;
            case "Logout":
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
        }
    }

    private void logout(){
        // Brings user back to the login page
        LoginActivity.signInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent loginIntent = new Intent(Settings.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
