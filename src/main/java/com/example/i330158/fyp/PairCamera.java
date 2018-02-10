package com.example.i330158.fyp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
    private String piIP = "";
    private String piPasswd = "";
    private boolean paired = false;
    private TextView ipAdd;
    private TextView piPwd;
    private Button pair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_paircam);

        pair = (Button)findViewById(R.id.pair);
        pair.setOnClickListener(this);

        ipAdd = (EditText)findViewById(R.id.ipAddress);
        piPwd = (EditText)findViewById(R.id.password);
    }

    @Override
    public void onClick(View view) {
        String text = ipAdd.getText().toString();
        setPiIP(text);

        text = piPwd.getText().toString();
        setPiPasswd(text);

        setPaired(true);
        Toast.makeText(this, "Paired Successfully", Toast.LENGTH_SHORT).show();
    }

    public String getPiIP() {
        return piIP;
    }

    public void setPiIP(String piIP) {
        this.piIP = piIP;
    }

    public String getPiPasswd() {
        return piPasswd;
    }

    public void setPiPasswd(String piPasswd) {
        this.piPasswd = piPasswd;
    }

    public boolean isPaired() {
        return paired;
    }

    public void setPaired(boolean paired) {
        this.paired = paired;
    }
}
