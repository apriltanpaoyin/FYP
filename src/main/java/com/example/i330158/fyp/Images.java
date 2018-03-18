package com.example.i330158.fyp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.io.InputStream;

import static android.content.ContentValues.TAG;
import static com.google.android.gms.drive.DriveFile.MODE_READ_ONLY;

/**
 * Created by Pao Yin Tan on 10/02/2018.
 *
 * This displays an image that the user chooses from their Drive
 */

public class Images extends Activity implements View.OnClickListener{
    private Button select;
    private ImageView show;
    private DriveId selectedDriveId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_images);

        select = (Button)findViewById(R.id.select_image);
        show = (ImageView)findViewById(R.id.show_image);

        select.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.select_image:
                // Create a Drive pop-up screen
                IntentSender intentSender = Drive.DriveApi
                        .newOpenFileActivityBuilder()
                        .setMimeType(new String[]{"image/jpeg"})
                        .build(LoginActivity.getGoogleApiClient());
                try {
                    startIntentSenderForResult(intentSender, 3, null, 0, 0, 0);
                }
                catch (IntentSender.SendIntentException e) {
                    Log.w(TAG, "Unable to send intent", e);
                }
                break;
        }
    }

    @Override
    // After selecting an image in the Drive pop-up, this method is executed
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        selectedDriveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
        open();
    }

    private void open(){
        DriveFile file = selectedDriveId.asDriveFile();
        file.open(LoginActivity.getGoogleApiClient(), MODE_READ_ONLY, null)
                .setResultCallback(driveContentsCallback);
        selectedDriveId = null;
    }

    //Callback to return a bitmap of the image selected and display it
    private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Toast.makeText(Images.this, "Error: Could not open file contents", Toast.LENGTH_LONG).show();
                        return;
                    }
                    DriveContents contents = result.getDriveContents();
                    InputStream is = contents.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    show.setImageBitmap(bitmap);
                }
            };
}
