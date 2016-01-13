package com.spaceagelabs.streetbaba;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.spaceagelabs.streetbaba.UI.SlidingTabLayout;
import com.spaceagelabs.streetbaba.UI.adapters.ProfileViewPagerAdapter;
import com.spaceagelabs.streetbaba.UI.adapters.ViewPagerAdapter;

public class ProfileActivity extends AppCompatActivity {


    ViewPager pager;
    ProfileViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"POSTS", "LIKES"};
    int Numboftabs = 2;
    String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);
        pager = (ViewPager) findViewById(R.id.profile_viewPager);
        pager.setAdapter(adapter);

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


    }

}
