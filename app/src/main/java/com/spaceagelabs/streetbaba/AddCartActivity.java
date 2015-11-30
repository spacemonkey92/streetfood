package com.spaceagelabs.streetbaba;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.spaceagelabs.streetbaba.UI.adapters.PlaceAutocompleteAdapter;
import com.spaceagelabs.streetbaba.util.ApplicationConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;


public class AddCartActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CAMERA = 101;
    private static final int SELECT_FILE = 102;
    LoginButton loginButton;
    private static final String TAG = "AddCartActivity";
    private SlidingUpPanelLayout mLayout;

    private static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(1.3, 103.833333), new LatLng(1.3, 103.833333));

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    Float Latitude =null, Longitude=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cart);

        setupBottomSheet();
        mAutocompleteView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, 0 /* clientId */, this).addApi(Places.GEO_DATA_API).build();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Register a listener that receives callbacks when a suggest ion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        LatLngBounds bounds;
        Float Latitude =  Float.parseFloat(getIntent().getExtras().getString(ApplicationConstants.CAM_LAT));
        Float Longitude= Float.parseFloat(getIntent().getExtras().getString(ApplicationConstants.CAM_LONG));
        if (Latitude!=null && Longitude !=null ){
            Log.d(TAG,"got lat long" +String.valueOf(Latitude)+String.valueOf(Longitude));
            bounds = new LatLngBounds(
                    new LatLng(Latitude, Longitude), new LatLng(Latitude, Longitude));
        }else{
            Log.d(TAG,"using default lat long");
            bounds = new LatLngBounds(
                    new LatLng(1.3, 103.833333), new LatLng(1.3, 103.833333));
        }
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, bounds,
                null);
        mAutocompleteView.setAdapter(mAdapter);
        setupImage();

        // Set up the 'clear text' button that clears the text in the autocomplete view
    }


    public void setupBottomSheet(){
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");

            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        } else {
            finish();
        }

        //TODO. check if dropdown open, close if open.
    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }


    public void  setupImage(){
        ImageView image = (ImageView) findViewById(R.id.selected_image);
        Intent intent = getIntent();
        Uri myUri = Uri.parse(intent.getExtras().getString(ApplicationConstants.IMG_BUNDLE));
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(myUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        image.setImageBitmap(bm);

        /** try to get lat long **/
        try{
            String filePath = getRealPathFromURI(myUri);
            ExifInterface exif = new ExifInterface(filePath);
            String LATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String LATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String LONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String LONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);


            if((LATITUDE !=null) && (LATITUDE_REF !=null) && (LONGITUDE != null) && (LONGITUDE_REF !=null)) {

                if(LATITUDE_REF.equals("N")){
                    Latitude = ApplicationConstants.convertToDegree(LATITUDE);
                }
                else{
                    Latitude = 0 - ApplicationConstants.convertToDegree(LATITUDE);
                }

                if(LONGITUDE_REF.equals("E")){
                    Longitude = ApplicationConstants.convertToDegree(LONGITUDE);
                }
                else{
                    Longitude = 0 - ApplicationConstants.convertToDegree(LONGITUDE);
                }
                Log.d(TAG,"lat long of " +filePath +" : "+LATITUDE+ "  "+ LONGITUDE + LATITUDE_REF + LONGITUDE_REF);
            }else{
                //Check if from camera.
                Log.d(TAG,"else");
                String fromCam= (intent.getExtras().getString(ApplicationConstants.FROM_CAM));
                if (fromCam!=null){
                    Log.d(TAG,"from cam");
                    Latitude=  Float.parseFloat(intent.getExtras().getString(ApplicationConstants.CAM_LAT));
                    Longitude= Float.parseFloat(intent.getExtras().getString(ApplicationConstants.CAM_LONG));
                }
            }

            Log.d(TAG,"lat long of " +filePath +" : ("+String.valueOf(Latitude)+ " , "+ String.valueOf(Longitude)+")");
        }catch (IOException e){

        }

    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);

            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);


            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            Log.i(TAG,"lat long"+place.getLatLng().toString());
            Log.i(TAG, "Place details received: " + place.getName());
            mAutocompleteView.setText(place.getName());
            mAutocompleteView.dismissDropDown();
            mAutocompleteView.setEnabled(false);
            places.release();
        }
    };

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void setupPlace (){
        PlaceDetectionApi aplce = new PlaceDetectionApi() {
            @Override
            public PendingResult<PlaceLikelihoodBuffer> getCurrentPlace(GoogleApiClient googleApiClient, PlaceFilter placeFilter) {
                return null;
            }

            @Override
            public PendingResult<Status> reportDeviceAtPlace(GoogleApiClient googleApiClient, PlaceReport placeReport) {
                return null;
            }
        };
    }



}
