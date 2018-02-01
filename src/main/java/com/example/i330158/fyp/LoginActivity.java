package com.example.i330158.fyp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity implements View.OnClickListener {
    private SignInButton signInButton;
    private GoogleSignInClient signInClient;
    private static int RC_SIGN_IN = 0;

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
    }

    //Check for previously signed in user & update UI accordingly
    protected void onStart(){
        super.onStart();
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
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
            handleSignIn(task);
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

    private void updateUI(GoogleSignInAccount acc){
        if (acc == null) {
            //remain in this page?
            //Display error message
//            AlertDialog err = new AlertDialog.Builder(this).create();
//            err.setTitle("Error");
//            err.setMessage("An error occurred while signing in.");
//            err.show();
        } else {
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

}
