package com.spaceagelabs.streetbaba.UI.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.spaceagelabs.streetbaba.LikesTab;
import com.spaceagelabs.streetbaba.PostsTab;
import com.spaceagelabs.streetbaba.Tab1;
import com.spaceagelabs.streetbaba.Tab2;
import com.spaceagelabs.streetbaba.Tab3;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsViewModel;

import java.util.ArrayList;

/**
 * Created by Nitin on 25/7/15.
 */
public class ProfileViewPagerAdapter extends FragmentStatePagerAdapter {


    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    final Tab3 tab3 = new Tab3();
    String TAG = "ViewPagerAdapter";


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ProfileViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            PostsTab tab1 = new PostsTab();
            return tab1;
        }
        else // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            LikesTab tab2 = new LikesTab();
            return tab2;
        }

    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

}