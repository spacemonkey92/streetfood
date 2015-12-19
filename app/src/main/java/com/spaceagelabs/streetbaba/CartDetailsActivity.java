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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsDetailsModel;
import com.spaceagelabs.streetbaba.clientSDK.APIManager;
import com.spaceagelabs.streetbaba.clientSDK.OnComplete;
import com.spaceagelabs.streetbaba.clientSDK.model.Cart;
import com.spaceagelabs.streetbaba.clientSDK.model.Review;
import com.spaceagelabs.streetbaba.util.ApplicationConstants;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class CartDetailsActivity extends AppCompatActivity {

    Menu menu;
    public static final String EXTRA_NAME = "cheese_name";
    public static final String TAG= "cartDetailsActivity";
    boolean liked,ownCart;
    String cartID,userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_detail);
        liked=false;


        Intent intent = getIntent();
        userID = ParseUser.getCurrentUser().getObjectId().toString();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadBackdrop();
        getCartReviews();
    }

    private void loadBackdrop() {
        final FloatingActionButton likeBtn = (FloatingActionButton) findViewById(R.id.like_button);
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra(ApplicationConstants.CART_IMG_BUNDLE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Glide.with(this).load(byteArray).centerCrop().into(imageView);
        cartID =  intent.getStringExtra(ApplicationConstants.CART_ID_BUNDLE);
        setDetailsLoading(true);
        APIManager.getInstance().getCartDetails(cartID, userID, new OnComplete<CartsDetailsModel>() {
            @Override
            public void done(CartsDetailsModel var1, Exception e) {
                setDetailsLoading(false);
                if (e == null) {
                    Cart cart = var1.getCart();

                    TextView cartName = (TextView) findViewById(R.id.cart_name_details_TV);
                    cartName.setText(cart.getName());
                    TextView likes = (TextView) findViewById(R.id.likes_TV);
                    likes.setText(cart.getLikesCount() + " likes");
                    TextView address = (TextView) findViewById(R.id.address_TV);
                    address.setText(cart.getAddress());
                    TextView fulAddress = (TextView) findViewById(R.id.full_address_TV);
                    fulAddress.setText(cart.getFullAddress());
                    cart.getCreatedBy().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            TextView foundBy = (TextView) findViewById(R.id.found_by_TV);
                            foundBy.setText((String) parseObject.get("name"));
                            Log.d(TAG, "got user name " + parseObject.get("name"));
                        }
                    });

                    TextView aboutCart = (TextView) findViewById(R.id.about_cart_TV);
                    aboutCart.setText(cart.getAbout());

                    if (var1.isLiked()) {
                        likeBtn.setImageResource(R.mipmap.ic_favorite_pink_24px);
                        liked = true;
                    } else {
                        likeBtn.setImageResource(R.mipmap.ic_favorite_white_24px);
                        liked = false;
                    }
                    MenuItem menuItem = menu.findItem(R.id.action_settings);
                    if (cart.getCreatedBy().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                        menuItem.setTitle("Delete");
                        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                deleteCart();
                                return true;
                            }
                        });
                        ownCart = true;
                    } else {
                        menuItem.setTitle("Report");
                        ownCart = false;
                    }
                } else {
                    //TODO. handle error.
                }
            }
        });


        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!liked) {
                    likeBtn.setImageResource(R.mipmap.ic_favorite_pink_24px);
                    liked = true;
                    likePhoto();
                } else {
                    likeBtn.setImageResource(R.mipmap.ic_favorite_white_24px);
                    liked = false;
                    dislikePhoto();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart_details, menu);
        this.menu= menu;

        return true;
    }

    public  void likePhoto(){
        if(APIManager.getInstance().isUserLoggedIn()){
            APIManager.getInstance().likeCart(cartID, userID, new OnComplete<Boolean>() {
                @Override
                public void done(Boolean var1, Exception e) {
                    //done
                }
            });
        }else{
            //TODO. handle anonymous users.
        }
    }

    public  void dislikePhoto(){
        if(APIManager.getInstance().isUserLoggedIn()){
            APIManager.getInstance().dislikeCart(cartID, userID, new OnComplete<Boolean>() {
                @Override
                public void done(Boolean var1, Exception e) {
                    //done
                }
            });
        }else{
            //TODO. handle anonymous users.
        }
    }

    public  void deleteCart(){
        if(APIManager.getInstance().isUserLoggedIn()){
            APIManager.getInstance().deleteCart(cartID, new OnComplete<Boolean>() {
                @Override
                public void done(Boolean var1, Exception e) {
                    if (e == null) {
                        Toast.makeText(CartDetailsActivity.this, "Deleted you post.", Toast.LENGTH_SHORT).show();
                        finish();
                        //TODO. deleted cart.
                    }
                }
            });
        }else{
            //TODO. handle anonymous users.
        }
    }

    public void setDetailsLoading(boolean status){
        View details= findViewById(R.id.cart_details);
        CircularProgressView progressView = (CircularProgressView) findViewById(R.id.progress_view);
        if(status){
            details.setVisibility(View.INVISIBLE);
            progressView.setVisibility(View.VISIBLE);
        }else{
            details.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.INVISIBLE);
        }
    }

    public void getCartReviews(){
        cartID =  getIntent().getStringExtra(ApplicationConstants.CART_ID_BUNDLE);
        APIManager.getInstance().getCartReview(cartID, new OnComplete<ArrayList<Review>>() {
            @Override
            public void done(ArrayList<Review> var1, Exception e) {

            }
        });

    }
}
