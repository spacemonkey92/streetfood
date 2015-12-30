package com.spaceagelabs.streetbaba.UI.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.spaceagelabs.streetbaba.Tab1;
import com.spaceagelabs.streetbaba.Tab2;
import com.spaceagelabs.streetbaba.Tab3;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsViewModel;

import java.util.ArrayList;

/**
 * Created by Nitin on 25/7/15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter implements Tab2.OnCartsDataListener {


    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    final Tab3 tab3 = new Tab3();
    String TAG = "ViewPagerAdapter";


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            Tab1 tab1 = new Tab1();
            return tab1;
        }
        else if(position==1)// As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            Tab2 tab2 = new Tab2();
            return tab2;
        }else{
//            this.tab3 = new Tab3();
            return tab3;
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

    @Override
    public void onCartsDataReceived(ArrayList<CartsViewModel> cartsViewModels) {
        Log.d(TAG, "data received to view pager... sending to tab 3");

        if(tab3!=null){
            tab3.onCartsDataReceived(cartsViewModels);
        }else{
            Log.d(TAG, "tab3 is null");
        }
    }
}