package com.example.i330158.fyp;

import android.app.Activity;
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

public class PairCamera extends Activity implements View.OnClickListener{
    private EditText ipAdd;
    private EditText piPwd;
    private Button save;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_paircam);

        save = (Button)findViewById(R.id.save);
        save.setOnClickListener(this);

        ipAdd = (EditText)findViewById(R.id.ipAddress);
        piPwd = (EditText)findViewById(R.id.password);
    }

    @Override
    public void onClick(View view) {
        String text = ipAdd.getText().toString();
        editor  = sharedPreferences.edit();

        editor.putString("IP", text);
        editor.apply();

        text = piPwd.getText().toString();
        editor.putString("password", text);
        editor.apply();

        editor.putBoolean("paired", true);
        Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
    }
}
