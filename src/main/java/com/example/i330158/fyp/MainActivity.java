package com.example.i330158.fyp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by I330158 on 24/01/2018.
 */

public class MainActivity extends Activity implements AdapterView.OnItemClickListener{
    private ListView mainMenu;
    private String[] mainOptions = {"Call 999", "Text Neighbour", "View Live Stream", "View Images",
            "Settings"};
    public String phoneNum = "";
    public String message = "";
    public SharedPreferences sharedPreferences;
    public static String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mainMenu = (ListView)findViewById(R.id.main_menu);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, mainOptions);
        mainMenu.setAdapter(adapter);
        mainMenu.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long l) {
        switch (mainOptions[pos]) {
            case "Call 999":
                callEmergency();
                break;
            case "Text Neighbour":
                sendText();
                break;
            case "View Live Stream":
                Intent streamIntent = new Intent(MainActivity.this, VidStream.class);
                startActivity(streamIntent);
                break;
            case "View Images":
                Intent recordIntent = new Intent(MainActivity.this, Images.class);
                startActivity(recordIntent);
                break;
            case "Settings":
                Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
                startActivity(settingsIntent);
                break;
        }
    }

    //https://www.tutorialspoint.com/android/android_phone_calls.htm
    private void callEmergency(){
        String number = "0879609224";
        //ACTION_DIAL used cause emergency numbers cannot be auto-called
        final Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + number));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please enable permissions in phone settings.", Toast.LENGTH_LONG).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage("Are you sure you would like to call emergency services?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(callIntent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void sendText() {
        phoneNum = sharedPreferences.getString("Phone", "DEFAULT");
        message = sharedPreferences.getString("Text", "DEFAULT");

        if (phoneNum.equals("DEFAULT") || message.equals("DEFAULT")) {
            Toast.makeText(this, "Please enter a phone number and message in settings.", Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please enable permissions in phone settings.", Toast.LENGTH_LONG).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage("Are you sure you would like to send a text?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SmsManager manager = SmsManager.getDefault();
                        manager.sendTextMessage(phoneNum, null, message, null, null);
                        Toast.makeText(MainActivity.this, "Text sent", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}