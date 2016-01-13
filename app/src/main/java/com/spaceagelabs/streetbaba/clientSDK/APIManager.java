package com.spaceagelabs.streetbaba.clientSDK;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.spaceagelabs.streetbaba.R;
import com.spaceagelabs.streetbaba.UI.DividerItemDecoration;
import com.spaceagelabs.streetbaba.UI.adapters.CartsAdapter;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsDetailsModel;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsViewModel;
import com.spaceagelabs.streetbaba.clientSDK.model.Cart;
import com.spaceagelabs.streetbaba.clientSDK.model.Review;
import com.spaceagelabs.streetbaba.util.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nitin on 1/12/15.
 */
public class APIManager {

    private static final String TAG = "APIManager";

    private static final APIManager instance = new APIManager();
    private static final String API_VERSION = "1";

    public static APIManager getInstance() {
        return instance;
    }

    public void getCarts(final double lat, double lng, final OnComplete<ArrayList<CartsViewModel>> onComplete) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("version", API_VERSION);
        params.put("range", 1);
        params.put("lat", lat);
        params.put("long", lng);
        final ArrayList<CartsViewModel> allCarts = new ArrayList<>();
        ParseCloud.callFunctionInBackground("getCarts", params, new FunctionCallback<List<Cart>>() {

            @Override
            public void done(List<Cart> carts, com.parse.ParseException e) {
                if (e == null) {
                    if (carts != null) {
                        for (Cart cart : carts) {
                            int likes = cart.getLikesCount();
                            String rating;
                            if (likes == 1) {
                                rating = "" + String.valueOf(likes) + " Like";
                            } else {
                                rating = "" + String.valueOf(likes) + " Likes";
                            }

                            final CartsViewModel mCart = new CartsViewModel(cart.getObjectId(), cart.getName(), cart.getAddress(), String.valueOf(cart.getReviewCount()), rating, cart.getImage(), cart.getLocation());
                            allCarts.add(mCart);
                        }
                    }
                } else {
                    Log.d(TAG, "error " + e.getMessage());
                    onComplete.done(null, e);
                }
                onComplete.done(allCarts, null);
            }
        });
    }

    public void getCartDetails(String cartId, String userId, final OnComplete<CartsDetailsModel> onComplete) {
        Log.d(TAG, "cet cart details " + cartId + " " + userId);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("version", API_VERSION);
        params.put("cartId", cartId);
        params.put("userId", userId);

        final ArrayList<CartsViewModel> allCarts = new ArrayList<>();
        ;
        ParseCloud.callFunctionInBackground("getCartDetails", params, new FunctionCallback<ArrayList<Object>>() {
            @Override
            public void done(ArrayList response, ParseException e) {

                if (e == null) {
                    ArrayList<Cart> carts = (ArrayList<Cart>) response.get(0);
                    Cart cart = carts.get(0);
                    Integer liked = (Integer) response.get(1);
                    boolean like;
                    if (liked == 1) {
                        like = true;
                    } else {
                        like = false;
                    }
                    CartsDetailsModel detailsModel = new CartsDetailsModel(cart, null, null, like);
                    onComplete.done(detailsModel, null);

                } else {
                    Log.d(TAG, "oops !" + e.getMessage());
                    onComplete.done(null, e);
                }
            }
        });
    }

    public void getCartReview(String cartId, final OnComplete<ArrayList<Review>> onComplete) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("version", API_VERSION);
        params.put("cartId", cartId);
        Log.d(TAG, "getting reviews");
        ParseCloud.callFunctionInBackground("getReviews", params, new FunctionCallback<ArrayList<Review>>() {
            @Override
            public void done(ArrayList<Review> response, ParseException e) {

                if (e == null) {
                    Log.d(TAG, "gor review :" + response.toString());
                    onComplete.done(response, null);

                } else {
                    Log.d(TAG, "failed getting reviews");
                    Log.d(TAG, "oops !" + e.getMessage());
                }
            }
        });
    }

    public void likeCart(String cartId, String userId, final OnComplete<Boolean> onComplete) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("version", API_VERSION);
        params.put("cartId", cartId);
        params.put("userId", userId);
        ParseCloud.callFunctionInBackground("likeCart", params, new FunctionCallback<String>() {
            @Override
            public void done(String aBoolean, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "awesome , Liked it!");
                    onComplete.done(true, null);

                } else {
                    Log.d(TAG, "oops !" + e.getMessage());
                }
            }
        });
    }

    public void reviewCart(String cartId, String userId, String body, final OnComplete<Boolean> onComplete) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("version", API_VERSION);
        params.put("cartId", cartId);
        params.put("userId", userId);
        params.put("body", body);
        ParseCloud.callFunctionInBackground("postReview", params, new FunctionCallback<String>() {
            @Override
            public void done(String aBoolean, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "posted review successfully");
                    onComplete.done(true, null);

                } else {
                    Log.d(TAG, "oops !" + e.getMessage());
                }
            }
        });
    }

    public void dislikeCart(String cartId, String userId, final OnComplete<Boolean> onComplete) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("version", API_VERSION);
        params.put("cartId", cartId);
        params.put("userId", userId);
        ParseCloud.callFunctionInBackground("dislikeCart", params, new FunctionCallback<String>() {
            @Override
            public void done(String aBoolean, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "awesome , disliked it!");
                    onComplete.done(true, null);

                } else {
                    Log.d(TAG, "oops !" + e.getMessage());
                }
            }
        });
    }

    public void deleteCart(String cartId, final OnComplete<Boolean> onComplete) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("version", API_VERSION);
        params.put("cartId", cartId);
        ParseCloud.callFunctionInBackground("deleteCart", params, new FunctionCallback<String>() {
            @Override
            public void done(String aBoolean, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "awesome , Liked it!");
                    onComplete.done(true, null);

                } else {
                    Log.d(TAG, "oops !" + e.getMessage());
                }
            }
        });
    }

    public void deleteReview(String reviewId, final OnComplete<Boolean> onComplete) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("version", API_VERSION);
        params.put("reviewId", reviewId);
        ParseCloud.callFunctionInBackground("deleteReview", params, new FunctionCallback<String>() {
            @Override
            public void done(String aBoolean, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "awesome , Liked it!");
                    onComplete.done(true, null);

                } else {
                    Log.d(TAG, "oops !" + e.getMessage());
                    onComplete.done(false, e);
                }
            }
        });
    }

    public void saveFBUserInParse(final OnComplete<String> onComplete) {

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        Log.d(TAG, jsonObject.toString());
                        if (jsonObject != null) {
                            JSONObject userProfile = new JSONObject();

                            try {
                                userProfile.put("facebookId", jsonObject.getLong("id"));
                                userProfile.put("name", jsonObject.getString("name"));

                                if (jsonObject.getString("gender") != null)
                                    userProfile.put("gender", jsonObject.getString("gender"));

                                if (jsonObject.getString("email") != null)
                                    userProfile.put("email", jsonObject.getString("email"));

                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.put("name", jsonObject.getString("name"));
                                currentUser.setEmail(jsonObject.getString("email"));
                                currentUser.put("profile", userProfile);
                                currentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {

                                            onComplete.done("okay", null);
                                        }
                                    }
                                });


                            } catch (JSONException e) {
                                Log.d(TAG,
                                        "Error parsing returned user data. " + e);
                            }
                        } else if (graphResponse.getError() != null) {
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    Log.d(TAG,
                                            "Authentication error: " + graphResponse.getError());
                                    break;

                                case TRANSIENT:
                                    Log.d(TAG,
                                            "Transient error. Try again. " + graphResponse.getError());
                                    break;

                                case OTHER:
                                    Log.d(TAG,
                                            "Some other error: " + graphResponse.getError());
                                    break;
                            }
                        }
                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,gender,name");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public boolean isUserLoggedIn() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            Log.d(TAG, "user already exists ");
            return true;
            //updateViewsWithProfileInfo();
        } else {
            Log.d(TAG, "no user exists ");
            return false;

        }
    }

    public void submitCart(final Cart cart, final ParseFile parseImage,final ParseFile largeParseImage, final OnComplete<String> onComplete) {

        parseImage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    /**
                     * successfully saved small picture.
                     */
                    largeParseImage.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            /**
                             * successfully saved large picture
                             */
                            cart.setFullImage(largeParseImage);
                            cart.setImage(parseImage);
                            cart.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    /**
                                     * successfully saved the cart.
                                     */
                                    String cartID = cart.getObjectId();
                                    ParseUser user = ParseUser.getCurrentUser();
                                    int point = user.getInt("points");
                                    user.put("points", (point + 10));
                                    user.saveInBackground(); // can save in background, not sot so important.
                                    onComplete.done(cartID, e);
                                }
                            });
                        }
                    });

                } else {
                    onComplete.done(null, e);
                }
            }
        });

    }

}
