package com.spaceagelabs.streetbaba;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsDetailsModel;
import com.spaceagelabs.streetbaba.clientSDK.APIManager;
import com.spaceagelabs.streetbaba.clientSDK.OnComplete;
import com.spaceagelabs.streetbaba.util.ApplicationConstants;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class CartDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "cheese_name";
    public static final String TAG= "cartDetailsActivity";
    boolean liked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_detail);
        liked=false;

        Intent intent = getIntent();
        final String cheeseName = intent.getStringExtra(EXTRA_NAME);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle(cheeseName);

        loadBackdrop();
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra(ApplicationConstants.CART_IMG_BUNDLE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Glide.with(this).load(byteArray).centerCrop().into(imageView);
        String cartID=  intent.getStringExtra(ApplicationConstants.CART_ID_BUNDLE);
        APIManager.getInstance().getCartDetails(cartID, null, new OnComplete<CartsDetailsModel>() {
            @Override
            public void done(CartsDetailsModel var1, Exception e) {
                Log.d(TAG, "done !");
            }
        });


        final FloatingActionButton likeBtn = (FloatingActionButton) findViewById(R.id.like_button);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!liked){
                    likeBtn.setImageResource(R.mipmap.ic_favorite_pink_24px);
                    liked=true;
                }else{
                    likeBtn.setImageResource(R.mipmap.ic_favorite_white_24px);
                    liked=false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart_details, menu);
        return true;
    }
}
