package com.spaceagelabs.streetbaba;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.spaceagelabs.streetbaba.clientSDK.model.Cart;
import com.spaceagelabs.streetbaba.clientSDK.model.Review;

import io.fabric.sdk.android.Fabric;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Nitin on 1/9/15.
 */
public class App extends android.app.Application {


    // Debugging switch
    public static final boolean APPDEBUG = false;

    // Debugging tag for the application
    public static final String APPTAG = "StreetBaba";


    public static final int PARSE_FB = 1992;


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        String APP_ID = this.getApplicationContext().getResources().getString(R.string.parse_app_id);
        String PARSE_CLIENT_KEY = this.getApplicationContext().getResources().getString(R.string.parse_client_key);

        ParseObject.registerSubclass(Cart.class);
        ParseObject.registerSubclass(Review.class);
        Parse.initialize(this, APP_ID, PARSE_CLIENT_KEY);
        ParseFacebookUtils.initialize(this, PARSE_FB);
        printHash();

    }

    public void printHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.spaceagelabs.streetbaba",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

}
