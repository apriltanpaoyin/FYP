package com.example.i330158.fyp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;

import static android.content.ContentValues.TAG;

/**
 * Created by Pao Yin Tan on 27/01/2018.
 *
 * This displays the live video stream in the app. NOT IMPLEMENTED DUE TO LIMITED RESOURCES.
 */

public class VidStream extends Activity {
    private WebView web_view;
    private Thread t;
    // Saved preferences for user entered details
    SharedPreferences sharedPreferences;
    public static String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_vidstream);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("paired", false) == true) {
            // Thread below stops ffmpeg process on the Pi
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSch jsch = new JSch();
                        Session session = jsch.getSession("pi", sharedPreferences.getString("IP", "Unknown IP"), 22);
                        session.setPassword(sharedPreferences.getString("password", "Wrong password"));

                        // Avoid asking for key confirmation
                        Properties prop = new Properties();
                        prop.put("StrictHostKeyChecking", "no");
                        session.setConfig(prop);

                        Log.d(TAG, "SSH Connecting");
                        session.connect();
                        Log.d(TAG, "SSH connected");

                        ChannelExec channelssh = (ChannelExec) session.openChannel("exec");
                        channelssh.setCommand("kill -CONT $(ps -ef | grep ffmpeg | awk '{print $2}' | head -1)");

                        channelssh.connect();
                        channelssh.disconnect();
                        session.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            // Displays the stream
            web_view = (WebView)findViewById(R.id.web_view);
            web_view.loadUrl("http://" + sharedPreferences.getString("IP", "Unknown IP") + ":8080/test.mjpg");
        }
    }

    @Override
    // Stops the stream when user leaves activity
    protected void onStop(){
        super.onStop();
        if (sharedPreferences.getBoolean("paired", false) == true) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSch jsch = new JSch();
                        Session session = jsch.getSession("pi", sharedPreferences.getString("IP", "Unknown IP"), 22);
                        session.setPassword(sharedPreferences.getString("passowrd", "Wrong password"));

                        // Avoid asking for key confirmation
                        Properties prop = new Properties();
                        prop.put("StrictHostKeyChecking", "no");
                        session.setConfig(prop);

                        Log.d(TAG, "SSH Connecting");
                        session.connect();
                        Log.d(TAG, "SSH connected");

                        ChannelExec channelssh = (ChannelExec) session.openChannel("exec");
                        channelssh.setCommand("kill -STOP $(ps -ef | grep ffmpeg | awk '{print $2}' | head -1)");

                        channelssh.connect();
                        channelssh.disconnect();
                        session.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }
}
