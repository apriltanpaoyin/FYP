package com.example.i330158.fyp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by Pao Yin Tan on 03/02/2018.
 *
 * This is where the user can save the IP address and password of the Pi. This is used for toggling
 * the alarm and other connections to the Pi.
 */

public class PairCamera extends Activity {
    private EditText ipAdd;
    private EditText piPwd;
    private Button save;
    // Saved preferences for user entered details
    public SharedPreferences sharedPreferences;
    public static String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_paircam);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        ipAdd = (EditText)findViewById(R.id.ipAddress);
        piPwd = (EditText)findViewById(R.id.password);

        save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipAddr = ipAdd.getText().toString();
                String password = piPwd.getText().toString();

                // Saves entered details to shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("IP", ipAddr);
                editor.putString("Password", password);
                editor.putBoolean("Paired", true);
                editor.commit();
                Toast.makeText(PairCamera.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        // IP address is displayed so that the user can look at the saved details. Password not
        // shown for security reasons.
        ipAdd.setText(sharedPreferences.getString("IP", ""));
    }
}
