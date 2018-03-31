package com.example.i330158.fyp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.drive.Drive;
import com.google.firebase.iid.FirebaseInstanceId;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;

import static android.content.ContentValues.TAG;

/**
 * Created by Pao Yin Tan on 24/01/2018.
 *
 * This is the login page of the app. User is shown this page when they are not logged in.
 */

public class LoginActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private SignInButton signInButton;
    static GoogleSignInClient signInClient;
    private static int RC_SIGN_IN = 0;
    static  GoogleSignInAccount acc;

    // TOKEN STUFF
    private static GoogleApiClient apiClient;
    public String prevKey = "notset";
    public String token;
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        signInButton = (com.google.android.gms.common.SignInButton)findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

        //Request client info
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Create client with provided info
        signInClient = GoogleSignIn.getClient(this, signInOptions);
        token = FirebaseInstanceId.getInstance().getToken();
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA: " + token);
    }

    //Check for previously signed in user & update UI
    protected void onStart(){
        super.onStart();
        acc = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(acc);
    }

    private void signIn(){
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If returned result is expected
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            apiClient.connect();
            // Builds a connection for notifications
            if (apiClient == null) {
                apiClient = new GoogleApiClient.Builder(this)
                        .addApi(Drive.API)
                        .addScope(Drive.SCOPE_FILE)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
            }
            apiClient.connect();
            handleSignIn(task);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // SSH
        // Update notify.py on the Pi with token; this sends notification from Pi to app
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSch jsch = new JSch();
                    Session session = jsch.getSession("pi", "192.168.1.10", 22);
                    session.setPassword("raspberry");

                    // Avoid asking for key confirmation
                    Properties prop = new Properties();
                    prop.put("StrictHostKeyChecking", "no");
                    session.setConfig(prop);

                    Log.d(TAG, "SSH Connecting");
                    session.connect();
                    Log.d(TAG, "SSH connected");

                    Channel channelssh = session.openChannel("exec");
                    ((ChannelExec) channelssh).setCommand("perl -pi -w -e 's/" + prevKey + "/" +
                            token + "/g' /home/pi/FYP/notify.py");
                    channelssh.setInputStream(null);
                    ((ChannelExec) channelssh).setErrStream(System.err);

                    channelssh.connect();
                    channelssh.disconnect();
                    session.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                prevKey = token;
            }
        });
        t.start();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Google Api Client connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Google Api Client connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // Show the error message
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0)
                    .show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    private void handleSignIn(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount acc = task.getResult(ApiException.class);

            //Change UI if successfully signed in
            updateUI(acc);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult: failed code = " + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount acc) {
        if (acc != null) {
            Intent mainMenuIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainMenuIntent);
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.signInButton:
                signIn();
                break;
        }
    }

    public static GoogleApiClient getGoogleApiClient() {
        return apiClient;
    }
}
