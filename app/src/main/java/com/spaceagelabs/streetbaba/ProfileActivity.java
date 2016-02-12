package com.spaceagelabs.streetbaba;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.spaceagelabs.streetbaba.UI.SlidingTabLayout;
import com.spaceagelabs.streetbaba.UI.adapters.ProfileViewPagerAdapter;
import com.spaceagelabs.streetbaba.UI.adapters.ViewPagerAdapter;
import com.spaceagelabs.streetbaba.util.ApplicationConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    ViewPager pager;
    ProfileViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"POSTS", "LIKES"};
    int Numboftabs = 2;
    String userId;
    String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        adapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);
        pager = (ViewPager) findViewById(R.id.profile_viewPager);
        pager.setAdapter(adapter);
        Intent i = getIntent();
        userId = i.getStringExtra(ApplicationConstants.USER_ID_BUNDLE);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.profile_tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.primaryColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
        displayProfileDetails(userId);


    }

    public void displayProfileDetails(String userId) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId", userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    ParseUser user = (ParseUser) list.get(0);
                    HashMap<String, Object> profile = new HashMap<>();
                    profile = (HashMap) user.get("profile");
                    final String fbUserId = (String) profile.get("facebookId");
                    final int points = user.getInt("points");
                    final String name= (String) user.get("name");

                    setDisplayPicture(fbUserId);
                    setUserPoints(points);
                    setDisplayName(name);

                }
            }
        });
    }


    public void setDisplayPicture(final String facebookId){
        Log.d(TAG, "got fb id " + facebookId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL imageURL = new URL("https://graph.facebook.com/" + facebookId + "/picture?type=large");
                    final Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "got fb image");
                            CircleImageView imageView = (CircleImageView) findViewById(R.id.dp_image);
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public void setDisplayName(final String name){
        TextView nameTV = (TextView) findViewById(R.id.profile_name);
        if(nameTV!=null){
            nameTV.setText(name);
        }
    }

    public void setUserPoints(final int points){
        TextView pointsTV = (TextView) findViewById(R.id.points_textView);
        if(pointsTV!=null){
            pointsTV.setText(String.valueOf(points));
        }
    }


}
