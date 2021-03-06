package com.example.i330158.fyp;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.FirebaseInstanceId;
import android.util.Log;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Pao Yin Tan on 17/02/2018.
 *
 * This sends a token to the server for when multiple users are involved. Not useful for the scope
 * of this project.
 */

public class ReceiveNotifications extends FirebaseInstanceIdService {
    private static final String TAG = "ReceiveNotifications";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        String email = LoginActivity.acc.getEmail();
        Log.d(TAG, "New token: " + token);

        OkHttpClient client = new OkHttpClient();

        // Create a request, populate it, then send it to the URL
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .add("email", email)
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
