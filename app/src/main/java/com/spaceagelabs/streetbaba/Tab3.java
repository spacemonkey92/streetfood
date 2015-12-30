package com.spaceagelabs.streetbaba;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsViewModel;

import java.util.ArrayList;

/**
 * Created by Nitin on 25/7/15.
 */
public class Tab3 extends Fragment implements Tab2.OnCartsDataListener {

    MapView mMapView;
    private GoogleMap googleMap;
    ArrayList<CartsViewModel> cartsViewModels;
    String TAG = "Tab3";


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.tab_3, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // latitude and longitude

        double firstLatitude = 0;
        double firstLongitude = 0;

        if(cartsViewModels!=null){
            for(int i= 0 ; i < cartsViewModels.size() ; i++){
                ParseGeoPoint point = cartsViewModels.get(i).getGeoPoint();
                double latitude = point.getLatitude();
                double longitude = point.getLongitude();

                if(i==0){
                    firstLatitude=latitude;
                    firstLongitude=longitude;
                }
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(latitude, longitude)).title(cartsViewModels.get(i).getCartName());

                // Changing marker icon
                marker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                // adding marker
                googleMap.addMarker(marker);

            }
        }

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(firstLatitude, firstLongitude)).zoom(12).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        return v;

    }

    @Override
    public void onCartsDataReceived(ArrayList<CartsViewModel> cartsModels) {
        Log.d(TAG, "Finally ! received data to tab 3");
        cartsViewModels = new ArrayList<>();
        if(cartsModels!=null){
            for(int i =0 ; i < cartsModels.size() ; i++){
                cartsViewModels.add(cartsModels.get(i));
            }
        }
    }
}