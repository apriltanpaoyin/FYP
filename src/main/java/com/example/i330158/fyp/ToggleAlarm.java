package com.example.i330158.fyp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import static android.content.ContentValues.TAG;

/**
 * Created by Pao Yin Tan on 03/02/2018.
 *
 * This allows the user to toggle the alarm on the Pi. It displays the current status of the alarm
 * as well.
 */

public class ToggleAlarm extends Activity implements View.OnClickListener{
    public TextView status;
    public Button toggle;
    public String prev;
    public String setStatus;
    // Saved preferences for user entered details
    public SharedPreferences sharedPreferences;
    public static String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_togglealarm);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        status = (TextView) findViewById(R.id.status);
        toggle = (Button)findViewById(R.id.button);
        toggle.setOnClickListener(this);

        // Gets the current status of alarm. See class below.
        new ParseJson().execute();
    }

    @Override
    public void onClick(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        toggleAlarm();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void toggleAlarm() {
        // Checks prev, which is set in ParseJson below. This is the current status. setStatus is the
        // status that user wants to change to. Current status and button is updated accordingly.
        if (prev.equals("false")){
            setStatus = "true";
            changeConf();
            status.setText("ON");
            toggle.setText("Disable Alarm");
        }
        else if (prev.equals("true")){
            setStatus = "false";
            changeConf();
            status.setText("OFF");
            toggle.setText("Enable Alarm");
        }
    }

    public void changeConf() {
        // SSH
        // Updates the alarm configuration in the Pi with the desired status
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSch jsch = new JSch();
                    String ip = sharedPreferences.getString("IP", "DEFAULT");
                    String pass = sharedPreferences.getString("Password", "DEFAULT ");

                    if (ip.equals("DEFAULT") || pass.equals("DEFAULT")) {
                        Toast.makeText(ToggleAlarm.this, "Please connect to the Pi in settings.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Session session = jsch.getSession("pi", ip, 22);
                    session.setPassword(pass);

                    // Avoid asking for key confirmation
                    Properties prop = new Properties();
                    prop.put("StrictHostKeyChecking", "no");
                    session.setConfig(prop);

                    Log.d(TAG, "SSH Connecting");
                    session.connect();
                    Log.d(TAG, "SSH connected");

                    Channel channelssh = session.openChannel("exec");
                    ((ChannelExec) channelssh).setCommand("sudo perl -pi -w -e 's/" + prev + "/" +
                            setStatus + "/g' /var/www/html/config.json");
                    channelssh.setInputStream(null);
                    ((ChannelExec) channelssh).setErrStream(System.err);

                    channelssh.connect();
                    channelssh.disconnect();
                    session.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Set current status
                prev = setStatus;
            }
        });
        t.start();
    }

    private class ParseJson extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params){
            try {
                // Connects to the URL to access the configuration file and reads it line by line.
                // The current status of the alarm is set by getting the value of the "alarm_set" key
                // in the json
                URL url = new URL("http://192.168.1.10/config.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder builder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null){
                    builder.append(line);
                }
                String stringJson = builder.toString();

                JSONObject jsonObject = new JSONObject(stringJson);
                prev = jsonObject.getString("alarm_set");
            }
            catch (Exception e) {
                Log.d(TAG, "Error: " + e);
            }
            return null;
        }

        @Override
        // Sets the current status and button to reflect the info taken from the json. Basically, it
        // sets the text field and button when the activity is launched.
        protected void onPostExecute(Void result){
            try {
                if (prev.equals("true")){
                    status.setText("ON");
                    toggle.setText("Disable Alarm");
                }
                else if (prev.equals("false"))
                {
                    status.setText("OFF");
                    toggle.setText("Enable Alarm");
                }
            }
            catch (Exception e) {
                Log.d(TAG, "Error: " + e );
                new AlertDialog.Builder(ToggleAlarm.this)
                        .setMessage("Please ensure that your Pi is turned on.")
                        .setPositiveButton("OK", null)
                        .show();
            }
            super.onPostExecute(result);
        }
    }
}
