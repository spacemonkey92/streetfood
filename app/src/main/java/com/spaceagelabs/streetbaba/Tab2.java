package com.spaceagelabs.streetbaba;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.spaceagelabs.streetbaba.UI.DividerItemDecoration;
import com.spaceagelabs.streetbaba.UI.adapters.CartsAdapter;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsViewModel;
import com.spaceagelabs.streetbaba.clientSDK.APIManager;
import com.spaceagelabs.streetbaba.clientSDK.OnComplete;
import com.spaceagelabs.streetbaba.clientSDK.OnTabSwipeListner;
import com.spaceagelabs.streetbaba.clientSDK.model.Cart;
import com.spaceagelabs.streetbaba.util.ApplicationConstants;
import com.spaceagelabs.streetbaba.util.GPSTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nitin on 25/7/15.
 */
public class Tab2 extends Fragment implements CartsAdapter.RVClickListener {

    private RecyclerView mRecycler;
    private CartsAdapter mRVAdapter;
    private View mView;
    private final static String TAG = "ViewCartsTab";
    GPSTracker gpsTracker;
    List<CartsViewModel> allCarts;
    OnCartsDataListener mOncarOnCartsDataListener;
    OnTabSwipeListner mTabSwipeListner;
    LinearLayoutManager linearLayoutManager; // this is to fix the android null pointer exception.

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOncarOnCartsDataListener = (OnCartsDataListener)activity;
            mTabSwipeListner = (OnTabSwipeListner)activity;
        }catch (ClassCastException e){

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2, container, false);
        mView = v;
        mRecycler = (RecyclerView) mView.findViewById(R.id.carts_recycle_view);
        allCarts = new ArrayList<>();

        final FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.attachToRecyclerView(mRecycler, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                fab.show();
            }

            @Override
            public void onScrollUp() {
                fab.hide();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clicket button");
                Intent i = new Intent(getActivity(), AddPhotoActivity.class);
                startActivity(i);
            }
        });

        CircularProgressView progressView = (CircularProgressView) v.findViewById(R.id.progress_view);
        progressView.startAnimation();
        showEmptyList(false);
        homeCardsData();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"tab 2 activity result");
        homeCardsData();
    }

    public void homeCardsData() {
        double lat=0,lng=0;
        gpsTracker = new GPSTracker(getContext());
        if (gpsTracker.getIsGPSTrackingEnabled()) {
            gpsTracker.getLocation();
            lat = gpsTracker.getLatitude();
            lng= gpsTracker.getLongitude();
            gpsTracker.stopUsingGPS();
            gpsTracker = null;

        } else {

            // dialog to turn on GPS owner.
            gpsTracker.showSettingsAlert();
        }
        APIManager.getInstance().getCarts(lat, lng, new OnComplete<ArrayList<CartsViewModel>>() {
            @Override
            public void done(ArrayList<CartsViewModel> carts, Exception e) {
                CircularProgressView progressView = (CircularProgressView) mView.findViewById(R.id.progress_view);
                progressView.setVisibility(View.INVISIBLE);
                if (e == null) {

                    if (carts.size() > 0) {

                        allCarts = carts;
                        Log.d(TAG, "got from API manager");
                        mRVAdapter = new CartsAdapter(getActivity(), carts);
                        mRVAdapter.setClickListener(Tab2.this);
                        mRecycler.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.line_divider));
                        mRecycler.setAdapter(mRVAdapter);
                        linearLayoutManager = new LinearLayoutManager(getContext());
                        mRecycler.setLayoutManager(linearLayoutManager);

                        //now send the data to the activity, to mark the map;
                        if (mOncarOnCartsDataListener != null) {
                            Log.d(TAG, "data received to Tab 1, sending to activity..");
                            mOncarOnCartsDataListener.onCartsDataReceived(carts);
                        }
                    } else {
                        showEmptyList(true);
                    }
                }
            }
        });
    }

    @Override
    public void itemClick(View view, int position) {
        Log.d(TAG, " clicked " + position);
        CartsViewModel cart = allCarts.get(position);
        ImageView imageView = (ImageView) view.findViewById(R.id.cart_image);
        Bitmap bm = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Intent i = new Intent(getActivity(),CartDetailsActivity.class);
        if(bm!=null){
            Log.d(TAG,"bit map set");
            i.putExtra(ApplicationConstants.CART_IMG_BUNDLE, bm);
            i.putExtra(ApplicationConstants.CART_ID_BUNDLE,cart.getId());
        }
        startActivity(i);

    }



    public interface OnCartsDataListener {

        public void onCartsDataReceived(ArrayList<CartsViewModel> cartsViewModels);

    }

    public  void showEmptyList(boolean state){
        if(mView!=null){
            View emptyView = mView.findViewById(R.id.empty_view);
            if(state){
                if(mRecycler!=null){
                    mRecycler.setVisibility(View.INVISIBLE);
                }
                emptyView.setVisibility(View.VISIBLE);

            }else{
                if(mRecycler!=null){
                    mRecycler.setVisibility(View.VISIBLE);
                }
                emptyView.setVisibility(View.INVISIBLE);
            }
        }
    }





}