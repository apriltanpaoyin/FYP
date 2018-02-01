package com.example.i330158.fyp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

/**
 * Created by I330158 on 27/01/2018.
 */

public class VidStream extends Activity implements View.OnClickListener {
    private WebView web_view;
    private Button alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_vidstream);

        web_view = (WebView)findViewById(R.id.web_view);
        alert = (Button)findViewById(R.id.alert);

        web_view.loadUrl("http://192.168.1.11:8080/test.mjpg");

        alert.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}
