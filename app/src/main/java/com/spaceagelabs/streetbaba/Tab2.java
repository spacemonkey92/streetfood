package com.spaceagelabs.streetbaba;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.spaceagelabs.streetbaba.UI.DividerItemDecoration;
import com.spaceagelabs.streetbaba.UI.adapters.CartsAdapter;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsViewModel;
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
                Intent i = new Intent(getActivity(), AddPhotoActivity.class);
                startActivity(i);
            }
        });

        CircularProgressView progressView = (CircularProgressView) v.findViewById(R.id.progress_view);
        progressView.startAnimation();
        homeCardsData();
        return v;
    }

    public List<CartsViewModel> homeCardsData() {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("version", "1");
        params.put("range", 1);
        gpsTracker = new GPSTracker(getContext());
        if (gpsTracker.getIsGPSTrackingEnabled()) {
            Log.d(TAG, "started tracking...");
            gpsTracker.getLocation();
            params.put("lat", gpsTracker.getLatitude());
            params.put("long", gpsTracker.getLongitude());
            gpsTracker.stopUsingGPS();
            gpsTracker = null;

        } else {
            //TODO.
            // dialog to turn on GPS owner.
        }


        ParseCloud.callFunctionInBackground("getCarts", params, new FunctionCallback<List<Cart>>() {

            @Override
            public void done(List<Cart> carts, com.parse.ParseException e) {
                CircularProgressView progressView = (CircularProgressView) mView.findViewById(R.id.progress_view);
                progressView.setVisibility(View.INVISIBLE);
                Log.d(TAG, "done");
                if (e == null) {
                    if (carts != null) {
                        for (Cart cart : carts) {
                            String rating = "0" + " Likes";
                            final CartsViewModel mCart = new CartsViewModel(cart.getObjectId(),cart.getName(), cart.getAddress(), String.valueOf(cart.getReviewCount()), rating, cart.getImage());
//                            final CartsViewModel cardsMain = new CartsViewModel("Maharajaa","kavuri Hills, Madhapur, Hyderabad","12","3.7/5");
                            allCarts.add(mCart);
                        }
                    }

                } else {
                    Log.d(TAG, "fail");
                }
                //mRecycler =(RecyclerView) mView.findViewById(R.id.carts_recycle_view);
                mRVAdapter = new CartsAdapter(getActivity(), allCarts);
                mRVAdapter.setClickListener(Tab2.this);
                mRecycler.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.line_divider));
                mRecycler.setAdapter(mRVAdapter);
                mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

            }
        });
        return allCarts;
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
            i.putExtra(ApplicationConstants.CART_IMG_BUNDLE,bm);
        }
        startActivity(i);

    }
}