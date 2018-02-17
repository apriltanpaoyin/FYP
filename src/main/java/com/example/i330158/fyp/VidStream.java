package com.example.i330158.fyp;

import android.app.Activity;
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
 * Created by I330158 on 27/01/2018.
 */

public class VidStream extends Activity implements View.OnClickListener {
    private WebView web_view;
    private Button alert;
    private Thread t;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_vidstream);

        if (sharedPreferences.getBoolean("paired", false) == true) {
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
            web_view = (WebView)findViewById(R.id.web_view);
            web_view.loadUrl("http://" + sharedPreferences.getString("IP", "Unknown IP") + ":8080/test.mjpg");
        }
        alert = (Button)findViewById(R.id.alert);
        alert.setOnClickListener(this);
    }

    @Override
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

    @Override
    public void onClick(View view) {

    }
}
