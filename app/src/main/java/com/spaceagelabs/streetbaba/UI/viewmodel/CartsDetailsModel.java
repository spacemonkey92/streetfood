package com.spaceagelabs.streetbaba.UI.viewmodel;

import android.graphics.Bitmap;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.spaceagelabs.streetbaba.clientSDK.model.Cart;

/**
 * Created by Nitin on 27/8/15.
 */
public class CartsDetailsModel {

    Cart cart;
    String createdBy;
    String createById;
    boolean liked;

    public CartsDetailsModel(Cart cart, String createdBy, String createById,boolean liked){
        this.cart=cart;
        this.createdBy=createdBy;
        this.createById=createById;
        this.liked= liked;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreateById() {
        return createById;
    }

    public void setCreateById(String createById) {
        this.createById = createById;
    }
}
