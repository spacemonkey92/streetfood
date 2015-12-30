package com.spaceagelabs.streetbaba.UI;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.spaceagelabs.streetbaba.App;
import com.spaceagelabs.streetbaba.R;
import com.spaceagelabs.streetbaba.clientSDK.model.Review;

import java.util.ArrayList;

/**
 * Created by Nitin on 21/12/15.
 */
public class ReviewsAdapter {


    LinearLayout reviewLayout;
    LinearLayout postReviewLayout;
    ArrayList<Review> reviewsData;
    Context context;
    final String TAG = "reviewAdapter";
    String userId;


    public ReviewsAdapter(Context context,LinearLayout view ,ArrayList<Review> reviews,String userid,LinearLayout postReviewLaout){
        this.userId=userid;
        this.context = context;
        this.postReviewLayout=postReviewLaout;
        this.reviewLayout=(LinearLayout)view;
        this.reviewsData = reviews;
    }

     public void setupView() {
         Log.d(TAG, "setup view 1");
        if ( reviewsData !=null) {
            Log.d(TAG,"setup view 2");
            if(reviewsData.size()>0){
                Log.d(TAG,"setup view 3");
                for (final Review review : reviewsData){
                    final View cellView = LayoutInflater.from(context).inflate(R.layout.review_cells, null);
                    Log.d(TAG,"setup view 4");
                    if(cellView!=null){
                        Log.d(TAG,"setup view 5");
                        ParseUser user = review.getReviewBy();
                        String userid = user.getObjectId();
                        if(this.userId!=null){
                            if(userId.equals(userid)){
                                postReviewLayout.setVisibility(View.INVISIBLE);
                            }
                        }
                        user.fetchInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                TextView reviewBy = (TextView)cellView.findViewById(R.id.review_by_TV);
                                TextView reviewBody = (TextView) cellView.findViewById(R.id.review_body_TV);
                                reviewBody.setText(review.getBody());
                                reviewBy.setText((String) parseObject.get("name"));
                            }
                        });
                        reviewLayout.addView(cellView);
                    }
                }
            }
        }else {
            if (reviewLayout==null){
                Log.d(TAG,"layout null");

            }
            if(reviewsData==null){
                Log.d(TAG,"review data");
            }
        }
    }



}
