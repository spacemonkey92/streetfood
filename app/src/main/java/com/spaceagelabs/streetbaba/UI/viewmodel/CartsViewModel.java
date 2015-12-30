package com.spaceagelabs.streetbaba.UI.viewmodel;

import android.graphics.Bitmap;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

/**
 * Created by Nitin on 27/8/15.
 */
public class CartsViewModel {

    Bitmap image;
    String cartName;
    String cartAddress;
    String reviewCount;
    String rating;
    String id;
    ParseFile parseImage;
    ParseGeoPoint geoPoint;


    public CartsViewModel(String id,String cartname,String cartAddress,String reviewCount,String rating,ParseFile parseImage,ParseGeoPoint geoPoint){
        this.cartName=cartname;
        this.cartAddress=cartAddress;
        this.reviewCount=reviewCount;
        this.rating=rating;
        this.parseImage=parseImage;
        this.id= id;
        this.geoPoint = geoPoint;
    }


    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getCartName() {
        return cartName;
    }

    public void setCartName(String cartName) {
        this.cartName = cartName;
    }

    public String getCartAddress() {
        return cartAddress;
    }

    public void setCartAddress(String cartAddress) {
        this.cartAddress = cartAddress;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public ParseFile getParseImage() {
        return parseImage;
    }

    public void setParseImage(ParseFile parseImage) {
        this.parseImage = parseImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ParseGeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(ParseGeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }
}
