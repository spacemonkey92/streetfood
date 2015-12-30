package com.spaceagelabs.streetbaba;


import android.content.Intent;
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


import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.spaceagelabs.streetbaba.UI.adapters.HomeRVAdapter;
import com.spaceagelabs.streetbaba.UI.viewmodel.HomeCardsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Nitin on 25/7/15.
 */
public class Tab1 extends Fragment implements HomeRVAdapter.RVClickListener {

    private RecyclerView mRecycler;
    private HomeRVAdapter mRVAdapter;
    public static final int PARSE_FB= 1992;
    private SlidingUpPanelLayout mLayout;
    private ProfilePictureView userProfilePictureView;
    LoginButton loginButton;
    private static final String TAG = "HomeTab";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_1,container,false);
        mRecycler =(RecyclerView) v.findViewById(R.id.home_recycle_view);
        mRVAdapter = new HomeRVAdapter(getActivity(),homeCardsData());
        mRVAdapter.setClickListener(this);
        mRecycler.setAdapter(mRVAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        loginButton = (LoginButton) v.findViewById(R.id.login_button);
        setupBottomPanner(v);
        //currentUser.g

        return v;
    }

    public  List<HomeCardsModel> homeCardsData(){
        HomeCardsModel exploreCard = new HomeCardsModel(getActivity().getResources().getString(R.string.share),getActivity().getResources().getString(R.string.share_sub));
        exploreCard.setInconId(R.mipmap.ic_cart_street_cc);
        HomeCardsModel shareCard = new HomeCardsModel(getActivity().getResources().getString(R.string.leader),getActivity().getResources().getString(R.string.leader_sub));
        shareCard.setInconId(R.mipmap.ic_trophy_black_36dp);
        HomeCardsModel mapCard = new HomeCardsModel(getActivity().getResources().getString(R.string.profile),getActivity().getResources().getString(R.string.profile_sub));
        mapCard.setInconId(R.mipmap.ic_walter_w_cc);

        List<HomeCardsModel> cards = new ArrayList<>();
        cards.add(exploreCard);
        cards.add(shareCard);
        cards.add(mapCard);
        return cards;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "on activity result" + requestCode);

        if(requestCode==PARSE_FB) {
            Log.d(TAG,"parse fb result");
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void itemClick(View view, int position) {

        if(position==0){
//            if(true){
            if(isUserLoggedIn()){
                proceedToAdd();
            }else{
                mLayout.setAnchorPoint(0.5f);
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        }
    }

    public void facebookLogin(){
        List<String> permissions = Arrays.asList("public_profile", "email");
        // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
        // (https://developers.facebook.com/docs/facebook-login/permissions/)

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {

            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                //progressDialog.dismiss();
                if (user == null) {
                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                    // facebookLogin();
                } else if (user.isNew()) {
                    Log.d(TAG, "User signed up and logged in through Facebook!");
                    //showUserDetailsActivity();
                    makeMeRequest();
                } else {
                    Log.d(TAG, "User logged in through Facebook!");
                    //showUserDetailsActivity();
                    makeMeRequest();
                }
            }
        });
    }

    public void setupBottomPanner(View v){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "parse fb login from button");
                facebookLogin();
            }
        });
        mLayout = (SlidingUpPanelLayout) v.findViewById(R.id.sliding_layout);
        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");

            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);


        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i(TAG, "keyCode: " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.i(TAG, "onKey Back listener is working!!!");
                    //getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    if (mLayout != null &&
                            (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)) {
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    } else {
                        getActivity().finish();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });


    }


    private void makeMeRequest() {
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
                                currentUser.saveInBackground();
                                Log.d(TAG, "saved profile");

                                // Show the user info
//                                updateViewsWithProfileInfo();
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
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        proceedToAdd();
    }


    public void proceedToAdd(){
        Intent intent = new Intent(getActivity(),AddPhotoActivity.class);
        startActivity(intent);
    }

    private boolean isUserLoggedIn(){
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



}