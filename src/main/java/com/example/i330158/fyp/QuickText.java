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

/**
 * Created by Pao Yin Tan on 03/02/2018.
 *
 * This is where the user can save a number and text message. The message is sent to the number when
 * the user clicks on the Text Neighbour option in the main menu.
 */

public class QuickText extends Activity {
    private EditText phone;
    private EditText message;
    private Button save;
    // Saved preferences for user entered details
    public SharedPreferences sharedPreferences;
    public static String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_quicktext);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        phone = (EditText)findViewById(R.id.phone);
        message = (EditText)findViewById(R.id.message);

        save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNum = phone.getText().toString();
                String textMessage = message.getText().toString();

                // Saves entered details to shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Phone", phoneNum);
                editor.putString("Text", textMessage);
                editor.commit();
                Toast.makeText(QuickText.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        // Number and message is displayed so that the user can look at the saved details.
        phone.setText(sharedPreferences.getString("Phone", ""));
        message.setText(sharedPreferences.getString("Text", ""));
    }
}
