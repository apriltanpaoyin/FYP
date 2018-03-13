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
 * Created by i330158 on 03/02/2018.
 */

public class ToggleAlarm extends Activity implements View.OnClickListener{
    public TextView status;
    public Button toggle;
    public SharedPreferences sharedPreferences;
    public static String MyPREFERENCES = "MyPrefs";
    public String prev;
    public String setStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_togglealarm);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        status = (TextView) findViewById(R.id.status);
        toggle = (Button)findViewById(R.id.button);
        toggle.setOnClickListener(this);

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
//        String curStat = status.getText().toString();
//        if (curStat.equals("OFF")) {
        if (prev.equals("false")){
            setStatus = "true";
//            changeConf("true");
            changeConf();
            status.setText("ON");
            toggle.setText("Disable Alarm");
        }
//        else if (curStat.equals("ON")) {
        else if (prev.equals("true")){
            setStatus = "false";
//            changeConf("false");
            changeConf();
            status.setText("OFF");
            toggle.setText("Enable Alarm");
        }
    }

    public void changeConf() {
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
                prev = setStatus;
            }
        });
        t.start();
    }

    private class ParseJson extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params){
            try {
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
                Log.d(TAG, "Error:" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if (prev.equals("true")){
                status.setText("ON");
                toggle.setText("Disable Alarm");
            }
            else if (prev.equals("false"))
            {
                status.setText("OFF");
                toggle.setText("Enable Alarm");
            }
            super.onPostExecute(result);
        }
    }
}
