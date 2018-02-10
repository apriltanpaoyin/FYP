package com.example.i330158.fyp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Created by i330158 on 03/02/2018.
 */

public class QuickText extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_vidstream);
    }

    @Override
    public void onClick(View view) {

    }
}
