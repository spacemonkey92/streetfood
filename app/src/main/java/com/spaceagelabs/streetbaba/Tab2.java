package com.spaceagelabs.streetbaba;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.spaceagelabs.streetbaba.UI.DividerItemDecoration;
import com.spaceagelabs.streetbaba.UI.adapters.CartsAdapter;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsViewModel;
import com.spaceagelabs.streetbaba.clientSDK.model.Cart;
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
    private final static String TAG ="ViewCartsTab";
    GPSTracker gpsTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_2,container,false);
        mView =v;
//        mRecycler =(RecyclerView) v.findViewById(R.id.carts_recycle_view);
//        mRVAdapter = new CartsAdapter(getActivity(),homeCardsData());
//        mRVAdapter.setClickListener(this);
//        mRecycler.setAdapter(mRVAdapter);
//        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler =(RecyclerView) mView.findViewById(R.id.carts_recycle_view);

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
        CircularProgressView progressView = (CircularProgressView)  v.findViewById(R.id.progress_view);
        progressView.startAnimation();
        homeCardsData();


        return v;
    }

    public List<CartsViewModel> homeCardsData(){

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("version", "1");
        params.put("range",1);
        gpsTracker = new GPSTracker(getContext());
        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            Log.d(TAG,"started tracking...");
            gpsTracker.getLocation();
//            String lat =  String.valueOf(gpsTracker.getLatitude());
//            String lng=  String.valueOf(gpsTracker.getLongitude());
//            Log.d(TAG,"lat long is :"+lat+lng);
            params.put("lat",gpsTracker.getLatitude());
            params.put("long",gpsTracker.getLongitude());
            gpsTracker.stopUsingGPS();
            gpsTracker=null;

        }else{
         //TODO.
            // dialog to turn on GPS owner.
            Log.d(TAG,"gps fail");
        }


        final List<CartsViewModel> allCarts = new ArrayList<>();

        ParseCloud.callFunctionInBackground("getCarts", params, new FunctionCallback<List<Cart>>() {

            @Override
            public void done(List<Cart> carts, com.parse.ParseException e) {
                CircularProgressView progressView = (CircularProgressView)  mView.findViewById(R.id.progress_view);
                progressView.setVisibility(View.INVISIBLE);
                Log.d(TAG, "done");
                if (e == null) {
                    Log.d(TAG, "pass" + carts.get(0).getName());
                    if(carts!=null){
                        for(Cart cart : carts){
                            String rating =String.valueOf(cart.getRating())+"/5";
                            final CartsViewModel mCart = new CartsViewModel(cart.getName(),cart.getAddress(),String.valueOf(cart.getReviewCount()),rating);
//                            final CartsViewModel cardsMain = new CartsViewModel("Maharajaa","kavuri Hills, Madhapur, Hyderabad","12","3.7/5");
                            allCarts.add(mCart);
                        }
                    }

                } else {
                    Log.d(TAG, "fail");
                }
                //mRecycler =(RecyclerView) mView.findViewById(R.id.carts_recycle_view);
                mRVAdapter = new CartsAdapter(getActivity(),allCarts);
                mRVAdapter.setClickListener(Tab2.this);
                mRecycler.addItemDecoration(
                        new DividerItemDecoration(getActivity(), R.drawable.line_divider));
                mRecycler.setAdapter(mRVAdapter);
                mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

            }
        });


//        CartsViewModel exploreCard = new CartsViewModel("Maharajaa","kavuri Hills, Madhapur, Hyderabad","12","3.7/5");
//
//
//        List<CartsViewModel> carts = new ArrayList<>();
//        carts.add(exploreCard);

        return allCarts;
    }


    @Override
    public void itemClick(View view, int position) {



    }
}