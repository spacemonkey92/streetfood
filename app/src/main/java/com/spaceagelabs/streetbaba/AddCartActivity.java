package com.spaceagelabs.streetbaba;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;


import com.facebook.login.widget.LoginButton;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.spaceagelabs.streetbaba.UI.adapters.PlaceAutocompleteAdapter;
import com.spaceagelabs.streetbaba.clientSDK.APIManager;
import com.spaceagelabs.streetbaba.clientSDK.model.Cart;
import com.spaceagelabs.streetbaba.clientSDK.OnComplete;
import com.spaceagelabs.streetbaba.util.ApplicationConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class AddCartActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener {

    CardView loginButton;
    private static final String TAG = "AddCartActivity";
    private SlidingUpPanelLayout mLayout;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    Float Latitude =null, Longitude=null;

    //Parse
    ParseFile parseImage=null;
    ParseFile largeParseImage=null;
    Cart cart = null;
    ParseGeoPoint geoPoint=null;
    String fullAddress = null;
    String address = null;


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
            bounds = new LatLngBounds(new LatLng(Latitude, Longitude), new LatLng(Latitude, Longitude));
        }else{
            // Use default
            bounds = new LatLngBounds(new LatLng(1.3, 103.833333), new LatLng(1.3, 103.833333));
        }
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, bounds, null);
        mAutocompleteView.setAdapter(mAdapter);
        showLoading(false);
        setupImage();
        setupButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==App.PARSE_FB) {
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setupBottomSheet(){
        loginButton = (CardView) findViewById(R.id.fb_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLogin();
            }
        });
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
//        super.onBackPressed();
        if (mLayout != null && (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)) {
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
        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this, "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
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

        /**
         * load thumbnail image.
         */
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);

        final int REQUIRED_SIZE = 200;

        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE) scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        if(bm!=null){
            image.setImageBitmap(bm);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte[] byteArray = stream.toByteArray();
            parseImage = new ParseFile("omly.jpg", byteArray);
        }else{
            Toast.makeText(this, getResources().getString(R.string.get_picture_error), Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * load large image
         */

        Bitmap largeBit;
        BitmapFactory.Options largeOptions = new BitmapFactory.Options();
        largeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, largeOptions);

        final int LARGE_SIZE = 600;

        int largeScale = 1;
        while (largeOptions.outWidth / largeScale/ 2 >= LARGE_SIZE && largeOptions.outHeight / largeScale/ 2 >= LARGE_SIZE) largeScale*= 2;
        largeOptions.inSampleSize = largeScale;
        largeOptions.inJustDecodeBounds = false;
        largeBit = BitmapFactory.decodeFile(selectedImagePath, largeOptions);

        if(largeBit!=null){
            ByteArrayOutputStream largeStream = new ByteArrayOutputStream();
            largeBit.compress(Bitmap.CompressFormat.JPEG, 80, largeStream);
            byte[] largeByteArray = largeStream.toByteArray();
            largeParseImage = new ParseFile("omly.jpg", largeByteArray);
        }else{
            Toast.makeText(this, getResources().getString(R.string.get_picture_error), Toast.LENGTH_SHORT).show();
            return;
        }




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
            if(Latitude!=null && Longitude!=null){

                geoPoint = new ParseGeoPoint(Latitude,Longitude);
            }
        }catch (IOException e){

        }

    }

    public void setupButton(){
        CardView postButton = (CardView) findViewById(R.id.normal_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(APIManager.getInstance().isUserLoggedIn()){
                    submitCart();
                }else{
                    mLayout.setAnchorPoint(0.5f);
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
            }
        });
    }

    public void submitCart(){
        AppCompatEditText nameET = (AppCompatEditText) findViewById(R.id.cart_name_ET);
        String name = nameET.getText().toString();
        AppCompatEditText aboutET = (AppCompatEditText) findViewById(R.id.cart_des_ET);
        String about = aboutET.getText().toString();
        if(parseImage!=null && largeParseImage!=null && fullAddress != null && name != null && name.length()>0 && geoPoint!=null && address != null && about!=null && about.length()>0){

            cart = new Cart();
            cart.setAddress(address);
            cart.setFullAddress(fullAddress);
            cart.setName(name);
            cart.setLocation(geoPoint);
            cart.setCreatedBy(ParseUser.getCurrentUser());
            cart.setAbout(about);
            showLoading(true);
            APIManager.getInstance().submitCart(cart, parseImage,largeParseImage, new OnComplete<String>() {
                @Override
                public void done(String cartId, Exception e) {
                    showLoading(false);
                    if(e==null){
                        Log.d(TAG,"success");
                        navigateToNextScreen(cartId);
                    }else{
                        Log.d(TAG,"fail");
                        Toast.makeText(AddCartActivity.this,"Failed to post! Try again.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            //// TODO: 8/1/16 validate the error.
            Toast.makeText(this,"Please fill in all the details.",Toast.LENGTH_SHORT).show();
        }
    }

    public void navigateToNextScreen(String cartID){
        ImageView imageView = (ImageView) findViewById(R.id.selected_image);
        Bitmap bm = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Intent i = new Intent(this,CartDetailsActivity.class);
        if(bm!=null){
            i.putExtra(ApplicationConstants.CART_IMG_BUNDLE, bm);
            i.putExtra(ApplicationConstants.CART_ID_BUNDLE,cartID);
        }
        finish();
        startActivity(i);
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
            fullAddress = item.getSecondaryText(null).toString();
            Log.i(TAG, "Autocomplete item selected: " + primaryText);
            address = primaryText.toString();

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            mAutocompleteView.setText(primaryText);
            mAutocompleteView.dismissDropDown();
            mAutocompleteView.setEnabled(false);
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

                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            Log.i(TAG, "lat long" + place.getLatLng().toString());
            Log.i(TAG, "Place details received: " + place.getName());
            if (geoPoint == null) {
                geoPoint = new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
            }
            places.release();
        }
    };

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void facebookLogin(){
        Log.d(TAG,"facebook login");
        List<String> permissions = Arrays.asList("public_profile", "email");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                if (user != null && e == null) {
                    APIManager.getInstance().saveFBUserInParse(new OnComplete<String>() {
                        @Override
                        public void done(String var1, Exception e) {
                            if (e == null){
                                Log.d(TAG, "saved profile finally !");
                                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                                submitCart();
                            }
                        }
                    });
                }
            }
        });
    }

    public  void showLoading(boolean state){
        View postButton = findViewById(R.id.post_button);
        CircularProgressView progressView = (CircularProgressView) findViewById(R.id.progress_view);
        if(state){
            postButton.setVisibility(View.INVISIBLE);
            progressView.setVisibility(View.VISIBLE);
        }else{
            postButton.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.INVISIBLE);
        }
    }
}
