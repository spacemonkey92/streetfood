package com.spaceagelabs.streetbaba;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.spaceagelabs.streetbaba.UI.ReviewsAdapter;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsDetailsModel;
import com.spaceagelabs.streetbaba.clientSDK.APIManager;
import com.spaceagelabs.streetbaba.clientSDK.OnComplete;
import com.spaceagelabs.streetbaba.clientSDK.model.Cart;
import com.spaceagelabs.streetbaba.clientSDK.model.Review;
import com.spaceagelabs.streetbaba.util.ApplicationConstants;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class CartDetailsActivity extends AppCompatActivity implements View.OnLongClickListener {

    Menu menu;
    public static final String TAG = "cartDetailsActivity";
    boolean liked, ownCart;
    String cartID, userID, ownReview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_detail);
        liked = false;
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            userID = user.getObjectId();
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        cartID = intent.getStringExtra(ApplicationConstants.CART_ID_BUNDLE);
        setDetailsLoading(true);
        APIManager.getInstance().getCartDetails(cartID, userID, new OnComplete<CartsDetailsModel>() {
            @Override
            public void done(CartsDetailsModel var1, Exception e) {
                setDetailsLoading(false);
                if (e == null) {
                    Cart cart = var1.getCart();
                    TextView cartName = (TextView) findViewById(R.id.cart_name_details_TV);
                    cartName.setText(cart.getName());

                    /**
                     * Setting Likes
                     */
                    TextView likes = (TextView) findViewById(R.id.likes_TV);
                    int likeCount = cart.getLikesCount();
                    String likesStr;
                    if (likeCount == 1) likesStr = " Like";
                    else likesStr = " Likes";

                    likes.setText(likeCount + likesStr);

                    /**
                     * setting address and found By.
                     */

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

                    /**
                     * setting like button.
                     */
                    TextView aboutCart = (TextView) findViewById(R.id.about_cart_TV);
                    aboutCart.setText(cart.getAbout());

                    if (var1.isLiked()) {
                        likeBtn.setImageResource(R.mipmap.ic_favorite_pink_24px);
                        liked = true;
                    } else {
                        likeBtn.setImageResource(R.mipmap.ic_favorite_white_24px);
                        liked = false;
                    }

                    /**
                     * setting menu. If the users owns the cart then he can delete or else report.
                     */
                    MenuItem menuItem = menu.findItem(R.id.action_settings);
                    if (cart.getCreatedBy().getObjectId().equals(userID)) {
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

                    /**
                     * Replace thumbnail with Large image
                     */
                    ParseFile fullImage = cart.getFullImage();
                    if (fullImage != null) {
                        fullImage.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] imageBytes, ParseException e) {
                                Glide.with(CartDetailsActivity.this).load(imageBytes).centerCrop().into(imageView);
                            }
                        });
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
                    changeLikeCount(true);
                } else {
                    likeBtn.setImageResource(R.mipmap.ic_favorite_white_24px);
                    liked = false;
                    dislikePhoto();
                    changeLikeCount(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart_details, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "action bar clicked");
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Log.d(TAG, "action bar clicked 1");
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void likePhoto() {
        if (APIManager.getInstance().isUserLoggedIn()) {
            APIManager.getInstance().likeCart(cartID, userID, new OnComplete<Boolean>() {
                @Override
                public void done(Boolean var1, Exception e) {
                    //done
                }
            });
        } else {
            //TODO. handle anonymous users.
        }
    }

    public void dislikePhoto() {
        if (APIManager.getInstance().isUserLoggedIn()) {
            APIManager.getInstance().dislikeCart(cartID, userID, new OnComplete<Boolean>() {
                @Override
                public void done(Boolean var1, Exception e) {
                    //done
                }
            });
        } else {
            //TODO. handle anonymous users.
        }
    }

    public void deleteCart() {
        if (APIManager.getInstance().isUserLoggedIn()) {
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
        } else {
            //TODO. handle anonymous users.
        }
    }

    public void setDetailsLoading(boolean status) {
        View details = findViewById(R.id.cart_details_full);
        CircularProgressView progressView = (CircularProgressView) findViewById(R.id.progress_view);
        if (status) {
            details.setVisibility(View.INVISIBLE);
            progressView.setVisibility(View.VISIBLE);
        } else {
            details.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.INVISIBLE);
        }
    }

    public void getCartReviews() {
        cartID = getIntent().getStringExtra(ApplicationConstants.CART_ID_BUNDLE);
        APIManager.getInstance().getCartReview(cartID, new OnComplete<ArrayList<Review>>() {
            @Override
            public void done(ArrayList<Review> reviews, Exception e) {
                if (e == null) {
                    Log.d(TAG, "displaying reviews");
                    LinearLayout reviewLayout = (LinearLayout) findViewById(R.id.review_list);
                    LinearLayout postReviewLayout = (LinearLayout) findViewById(R.id.review_post_ll);
                    ReviewsAdapter adapter = new ReviewsAdapter(getApplicationContext(), reviewLayout, reviews, userID, postReviewLayout,CartDetailsActivity.this);
                    ownReview = adapter.setupView();
                }
            }
        });
    }

    public void sendReview(View view) {
        EditText reviewBoodyET = (EditText) findViewById(R.id.review_ET);
        String body = reviewBoodyET.getText().toString();
        if (body != null) {
            if (body.length() > 0) {
                disableReviews(true);
                if (APIManager.getInstance().isUserLoggedIn()) {
                    APIManager.getInstance().reviewCart(cartID, userID, body, new OnComplete<Boolean>() {
                        @Override
                        public void done(Boolean var1, Exception e) {
                            if (e == null) {
                                getCartReviews();
                                //Disable Reviews
                            }else{
                                disableReviews(false);
                                Toast.makeText(CartDetailsActivity.this, "Sorry could not post your review, try again later !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    // TODO: 22/12/15 manage login.
                }
            }
        }
    }

    public void disableReviews(boolean state) {
        LinearLayout reviewLayout = (LinearLayout) findViewById(R.id.review_post_ll);
        if (state) {
            if (reviewLayout != null) {
                reviewLayout.setVisibility(View.INVISIBLE);
            }
        } else {
            if (reviewLayout != null) {
                reviewLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    public void changeLikeCount(boolean count) {
        TextView likes = (TextView) findViewById(R.id.likes_TV);
        String likeStr = likes.getText().toString();
        if (likeStr != null) {
            String likeCount = likeStr.split("\\s+")[0];
            int cnt = Integer.valueOf(likeCount);
            if (count) {
                cnt++;
            } else if (!count && (cnt != 0)) {
                cnt--;
            }
            String likesStr;
            if (cnt == 1) {
                likesStr = " Like";
            } else {
                likesStr = " Likes";
            }
            likes.setText(cnt + likesStr);
        }

    }


    @Override
    public boolean onLongClick(View view) {
        if(ownReview !=null){
            Log.d(TAG, "on long click" + ownReview);
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme);
            builder.setTitle("Delete Review");
            builder.setMessage("Are you sure you want to delete this review ?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    APIManager.getInstance().deleteReview(ownReview, new OnComplete<Boolean>() {
                        @Override
                        public void done(Boolean var1, Exception e) {
                            if (e == null) {
                                Toast.makeText(CartDetailsActivity.this, "Successfully deleted your review.", Toast.LENGTH_SHORT).show();
                                getCartReviews();
                            } else {
                                Toast.makeText(CartDetailsActivity.this, "Failed to deleted your review, try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("CANCEL", null);
            builder.show();
        }
        return true;

    }
}
