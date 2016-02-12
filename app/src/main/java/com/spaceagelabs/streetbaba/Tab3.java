package com.spaceagelabs.streetbaba;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsViewModel;
import com.spaceagelabs.streetbaba.util.ApplicationConstants;
import com.spaceagelabs.streetbaba.util.GPSTracker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nitin on 25/7/15.
 */
public class Tab3 extends Fragment implements Tab2.OnCartsDataListener, GoogleMap.OnMarkerClickListener {

    MapView mMapView;
    GPSTracker gpsTracker;
    private GoogleMap googleMap;
    ArrayList<CartsViewModel> cartsViewModels;
    String TAG = "Tab3";
    Boolean mapsPloted = false;
    HashMap<String, CartsViewModel> cartsMap;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_3, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        processMaps();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTipsDisplay(true);
        if(!mapsPloted){
            processMaps();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "on attach tab 3");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "on pause tab 3");
    }

    @Override
    public void onCartsDataReceived(ArrayList<CartsViewModel> cartsModels) {
        Log.d(TAG, "Finally ! received data to tab 3");
        cartsViewModels = new ArrayList<>();
        if (cartsModels != null) {
            for (int i = 0; i < cartsModels.size(); i++) {
                cartsViewModels.add(cartsModels.get(i));
            }
        }
        processMaps();
    }


    public void processMaps() {
        Log.d(TAG, "processing maps");

        if (mMapView != null) {
            mMapView.onResume();// needed to get the map to display immediately
            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            googleMap = mMapView.getMap();
            googleMap.setOnMarkerClickListener(this);
//            googleMap.setMyLocationEnabled(true); this is draining my batter. thus removed.

            double firstLatitude = 0;
            double firstLongitude = 0;
            gpsTracker = new GPSTracker(getContext());
            if (gpsTracker.getIsGPSTrackingEnabled()) {
                gpsTracker.getLocation();
                firstLatitude = gpsTracker.getLatitude();
                firstLongitude = gpsTracker.getLongitude();
                gpsTracker.stopUsingGPS();
                gpsTracker = null;

            } else {

                // dialog to turn on GPS owner.
                gpsTracker.showSettingsAlert();
            }

            Log.d(TAG,"adding markers");
            if (cartsViewModels != null) {
                cartsMap = new HashMap<>();
                for (int i = 0; i < cartsViewModels.size(); i++) {

                    CartsViewModel cartsViewModel = cartsViewModels.get(i);
                    ParseGeoPoint point = cartsViewModels.get(i).getGeoPoint();
                    double latitude = point.getLatitude();
                    double longitude = point.getLongitude();
                    if (i == 0) {
                        firstLatitude = latitude;
                        firstLongitude = longitude;
                    }
                    MarkerOptions marker = new MarkerOptions().position(
                            new LatLng(latitude, longitude)).title(cartsViewModel.getCartName());

                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    String makerId = googleMap.addMarker(marker).getId();
                    Log.d(TAG, "marker added" + makerId);
                    cartsMap.put(makerId, cartsViewModel);
                    mapsPloted = true;
                }
            }
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(firstLatitude, firstLongitude)).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {


        View v = getView();
        TextView cartName;
        TextView cartAddress;
        TextView rating;
        TextView reviewCount;
        if(cartsMap==null){
            Log.d(TAG, "models null");
        }
        Log.d(TAG,"marker id clicked is "+marker.getId());

        final CartsViewModel currentData = cartsMap.get(marker.getId());
        if(currentData==null){
            Log.d(TAG,"current data null");
        }

        if (currentData!= null && v != null) {
            setTipsDisplay(false);
            final ImageView cartImage = (ImageView) v.findViewById(R.id.cart_image);
            cartName = (TextView) v.findViewById(R.id.cart_name_TV);
            cartAddress = (TextView) v.findViewById(R.id.cart_address_TV);
            rating = (TextView) v.findViewById(R.id.rating);
            reviewCount = (TextView) v.findViewById(R.id.review_count);
            ParseFile image = currentData.getParseImage();
            cartImage.getDrawable();


            if (image != null) {
                Log.d(TAG, "image not null. Downloading....");
                image.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null) {
                            Log.d(TAG, "download finished");
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            cartImage.setImageBitmap(bmp);
                        } else {
                            cartImage.setImageResource(R.mipmap.ic_noun_53360_cc);
                        }
                    }
                });
            } else {
                cartImage.setImageResource(R.mipmap.ic_noun_53360_cc);// life saving line of code :D
            }
            cartName.setText(currentData.getCartName());
            cartAddress.setText(currentData.getCartAddress());
            rating.setText(currentData.getRating());
            reviewCount.setText(currentData.getReviewCount());

            View cell = (View) v.findViewById(R.id.cart_cell);
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bitmap bm = ((BitmapDrawable) cartImage.getDrawable()).getBitmap();
                    Intent i = new Intent(getActivity(), CartDetailsActivity.class);
                    if (bm != null) {

                        i.putExtra(ApplicationConstants.CART_IMG_BUNDLE, bm);
                        i.putExtra(ApplicationConstants.CART_ID_BUNDLE, currentData.getId());
                    }
                    startActivity(i);
                }
            });
        }
        return false;
    }

    public void setTipsDisplay(boolean status){

        Log.d(TAG,"marker clicked");
        View view = getView().findViewById(R.id.cart_details_map);
        TextView textView = (TextView) getView().findViewById(R.id.map_text);
        if(status){
            view.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.VISIBLE);
            textView.setVisibility(View.INVISIBLE);
        }
    }

}