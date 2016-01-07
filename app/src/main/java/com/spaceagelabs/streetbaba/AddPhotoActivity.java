package com.spaceagelabs.streetbaba;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.facebook.login.widget.LoginButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.spaceagelabs.streetbaba.util.ApplicationConstants;
import com.spaceagelabs.streetbaba.util.GPSTracker;

import java.util.ArrayList;
import java.util.List;


public class AddPhotoActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 101;
    private static final int SELECT_FILE = 102;
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 999;
    LoginButton loginButton;
    private static final String TAG = "AddCartActivity";
    private SlidingUpPanelLayout mLayout;
    String lat, lng;
    GPSTracker gpsTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        selectImage();
        locationManger();
//        gpsTracker = new GPSTracker(AddPhotoActivity.this);

        // Set up the 'clear text' button that clears the text in the autocomplete view
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    Uri imageUri;

    public void selectImage() {

        CardView cameraCard = (CardView) findViewById(R.id.camera_button);
        cameraCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture" + ".jpg");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        CardView galaryCard = (CardView) findViewById(R.id.gallery_button);
        galaryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(intent, "Select Image"),
                        SELECT_FILE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(AddPhotoActivity.this, AddCartActivity.class);
            gpsTracker = new GPSTracker(AddPhotoActivity.this);
            if (gpsTracker.getIsGPSTrackingEnabled()) {
                Log.d(TAG, "started tracking...");
                String lat = String.valueOf(gpsTracker.getLatitude());
                String lng = String.valueOf(gpsTracker.getLongitude());
                Log.d(TAG, "started tracking..." + lat + lng);
                gpsTracker.stopUsingGPS();
                intent.putExtra(ApplicationConstants.CAM_LAT, lat);
                intent.putExtra(ApplicationConstants.CAM_LONG, lng);
            }
            if (requestCode == REQUEST_CAMERA) {
                try {
                    intent.putExtra(ApplicationConstants.FROM_CAM, "true");
                    intent.putExtra(ApplicationConstants.IMG_BUNDLE, imageUri.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                intent.putExtra(ApplicationConstants.IMG_BUNDLE, selectedImageUri.toString());

            }
            finish();
            startActivity(intent);

        }
    }

    public void locationManger() {


    }

}
