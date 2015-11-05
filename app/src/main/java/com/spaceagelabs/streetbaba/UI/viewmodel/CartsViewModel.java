package com.spaceagelabs.streetbaba.UI.viewmodel;

import android.graphics.Bitmap;

/**
 * Created by Nitin on 27/8/15.
 */
public class CartsViewModel {

    Bitmap image;
    String cartName;
    String cartAddress;
    String reviewCount;
    String rating;


    public CartsViewModel(String cartname,String cartAddress,String reviewCount,String rating){
        this.cartName=cartname;
        this.cartAddress=cartAddress;
        this.reviewCount=reviewCount;
        this.rating=rating;
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
}
