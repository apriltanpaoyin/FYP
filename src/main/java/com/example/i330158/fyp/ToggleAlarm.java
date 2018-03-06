package com.example.i330158.fyp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import java.util.Properties;

import static android.content.ContentValues.TAG;

/**
 * Created by i330158 on 03/02/2018.
 */

public class ToggleAlarm extends Activity implements View.OnClickListener{
    public TextView status;
    public Button toggle;
    public SharedPreferences sharedPreferences;
    public static String MyPREFERENCES = "MyPrefs";
    public String prev = "true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_togglealarm);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        status = (TextView) findViewById(R.id.status);
        toggle = (Button)findViewById(R.id.button);

        toggle.setOnClickListener(this);
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
        String curStat = status.getText().toString();

        if (curStat.equals("OFF")) {
            changeConf("true");
            status.setText("ON");
            toggle.setText("Disable Alarm");
        }
        else if (curStat.equals("ON")) {
            changeConf("false");
            status.setText("OFF");
            toggle.setText("Enable Alarm");
        }
    }

    public void changeConf(final String setStatus) {
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
                    ((ChannelExec) channelssh).setCommand("perl -pi -w -e 's/" + prev + "/" +
                            setStatus + "/g' /home/pi/FYP/conf.json");
                    channelssh.setInputStream(null);
                    ((ChannelExec) channelssh).setErrStream(System.err);

                    channelssh.connect();
                    channelssh.disconnect();
                    session.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                prev = setStatus;
            }
        });
        t.start();
    }
}
