package com.example.i330158.fyp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;

/**
 * Created by I330158 on 24/01/2018.
 */

public class MainActivity extends Activity implements AdapterView.OnItemClickListener{
    private ListView mainMenu;
    private String[] mainOptions = {"Call 999", "Text Neighbour", "View Live Stream", "View Recordings",
            "Settings"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mainMenu = (ListView)findViewById(R.id.main_menu);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, mainOptions);
        mainMenu.setAdapter(adapter);
        mainMenu.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long l) {
        switch (mainOptions[pos]) {
            case "Call 999":
                //Add functionality
                break;
            case "Text Neighbour":
                //Add functionality
                break;
            case "View Live Stream":
                Intent streamIntent = new Intent(MainActivity.this, VidStream.class);
                startActivity(streamIntent);
                break;
            case "View Recordings":
                Intent recordIntent = new Intent(MainActivity.this, Recordings.class);
                startActivity(recordIntent);
                break;
            case "Settings":
                Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
                startActivity(settingsIntent);
                break;
        }
    }
}
