package com.spaceagelabs.streetbaba.clientSDK;

/**
 * Created by Nitin on 28/10/15.
 */
public interface OnComplete<T> {
    void done(T var1, Exception e);
}
