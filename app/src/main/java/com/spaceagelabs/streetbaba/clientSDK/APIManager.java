package com.spaceagelabs.streetbaba.clientSDK;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.spaceagelabs.streetbaba.clientSDK.model.Cart;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Nitin on 1/12/15.
 */
public class APIManager {

    private static final String TAG = "APIManager";

    private static final APIManager instance= new APIManager();

    public static APIManager getInstance(){
        return instance;
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
                                        if(e==null){

                                            onComplete.done("okay",null);
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

    public boolean isUserLoggedIn(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            Log.d(TAG,"user already exists ");
            return  true;
            //updateViewsWithProfileInfo();
        }else{
            Log.d(TAG,"no user exists ");
            return  false;

        }
    }

    public void submitCart(final Cart cart, final ParseFile parseImage, final OnComplete<Boolean> onComplete) {

        parseImage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    /**
                     * successfully saved picture.
                     */
                    cart.setImage(parseImage);
                    cart.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            /**
                             * successfully saved the cart.
                             */

                            ParseUser user = ParseUser.getCurrentUser();
                            int point =user.getInt("points");
                            Log.d(TAG,"current points are :" +String.valueOf(point));
                            user.put("points",(point+10));
                            user.saveInBackground(); // can save in background, not sot so important.
                            onComplete.done(true,e);
                        }
                    });
                }else{
                    onComplete.done(false, e);
                }
            }
        });

    }

}
