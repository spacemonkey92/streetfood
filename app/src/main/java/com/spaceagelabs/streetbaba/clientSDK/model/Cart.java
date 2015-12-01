package com.spaceagelabs.streetbaba.clientSDK.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by Nitin on 2/9/15.
 */
@ParseClassName("cart")
public class Cart extends ParseObject {
    public String getName() {
        return getString("cart_name");
    }

    public void setName(String value) {
        put("cart_name", value);
    }

    public String getCartType() {
        return getString("cart_type");
    }

    public void setCartType(String value) {
        put("cart_type", value);
    }

    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String value) {
        put("address", value);
    }


    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("cart_location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("cart_location", value);
    }

    public int getReviewCount() {
        return getInt("review_count");
    }

    public void setReviewCount(int value) {
        put("review_count", value);
    }

    public String getRating() {
        return String.valueOf(get("rating"));
    }

    public void setRating(String value) {
        put("cart_location", value);
    }


    public static ParseQuery<Cart> getQuery() {
        return ParseQuery.getQuery(Cart.class);
    }
}