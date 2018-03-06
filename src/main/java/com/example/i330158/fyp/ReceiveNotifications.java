package com.example.i330158.fyp;

import com.google.firebase.iid.FirebaseInstanceIdService;

import android.provider.*;
import android.provider.Settings;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by i330158 on 17/02/2018.
 */

public class ReceiveNotifications extends FirebaseInstanceIdService {
    private static final String TAG = "ReceiveNotifications";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "New token: " + token);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .build();

        Request req = new Request.Builder()
                .url("http://83.212.82.100/register.php")
                .post(body)
                .build();

        try {
            client.newCall(req).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
