package com.example.i330158.fyp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by i330158 on 03/02/2018.
 */

public class PairCamera extends Activity {
    private EditText ipAdd;
    private EditText piPwd;
    private Button save;
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

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("IP", ipAddr);
                editor.putString("Password", password);
                editor.putBoolean("Paired", true);
                editor.commit();
                Toast.makeText(PairCamera.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        ipAdd.setText(sharedPreferences.getString("IP", ""));
        piPwd.setText(sharedPreferences.getString("Password", ""));
    }
}
