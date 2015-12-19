package com.spaceagelabs.streetbaba.clientSDK.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Nitin on 2/9/15.
 */
@ParseClassName("review")
public class Review extends ParseObject {
    public String getBody() {
        return getString("body");
    }

    public void setBody(String value) {
        put("body", value);
    }

    public ParseObject getCart() {
        return getParseObject("cart");
    }

    public void setCart(Cart value) {
        put("cart", value);
    }

    public ParseUser getReviewBy() {
        return getParseUser("reviewBy");
    }

    public void setReviewBy(ParseUser value) {
        put("reviewBy", value);
    }

    public static ParseQuery<Review> getQuery() {
        return ParseQuery.getQuery(Review.class);
    }




}