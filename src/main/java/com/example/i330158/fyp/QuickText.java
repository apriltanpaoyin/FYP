package com.example.i330158.fyp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by i330158 on 03/02/2018.
 */

public class QuickText extends Activity implements View.OnClickListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private EditText phone;
    private EditText message;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_quicktext);

        phone = (EditText)findViewById(R.id.phone);
        message = (EditText)findViewById(R.id.message);
        save = (Button)findViewById(R.id.save);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        editor = sharedPreferences.edit();
        String text = phone.getText().toString();
        editor.putString("phone", text);
        editor.apply();
        text = message.getText().toString();
        editor.putString("message", text);
        editor.apply();
        Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
    }
}
