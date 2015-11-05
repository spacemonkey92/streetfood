package com.spaceagelabs.streetbaba.UI.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spaceagelabs.streetbaba.R;
import com.spaceagelabs.streetbaba.UI.viewmodel.CartsViewModel;

import java.util.Collections;
import java.util.List;

/**
 * Created by Nitin on 27/8/15.
 */
public class CartsAdapter extends RecyclerView.Adapter<CartsAdapter.MyViewHolder>{

    private static final String TAG = "CartsAdapter";
    private final LayoutInflater inflater;
    private List<CartsViewModel> data = Collections.emptyList();
    private RVClickListener clickListener;

    public CartsAdapter(Context context, List<CartsViewModel> data){
        inflater = LayoutInflater.from(context);
        this.data=data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =inflater.inflate(R.layout.cart_cell,viewGroup,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        CartsViewModel currentData = data.get(i);
        if(currentData.getImage()==null){

        }
        viewHolder.cartName.setText(currentData.getCartName());
        viewHolder.cartAddress.setText(currentData.getCartAddress());
        viewHolder.rating.setText(currentData.getRating());
        viewHolder.reviewCount.setText(currentData.getReviewCount());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setClickListener(RVClickListener listener){
        Log.d(TAG,"listener set");
        this.clickListener= listener;

    }



    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView cartImage;
        TextView cartName;
        TextView cartAddress;
        TextView rating;
        TextView reviewCount;
        View mView;


        public MyViewHolder(View itemView) {

            super(itemView);
            Log.d(TAG, "setting images and items");
            mView=itemView;

            cartImage =(ImageView) itemView.findViewById(R.id.cart_image);
            cartName =(TextView) itemView.findViewById(R.id.cart_name_TV);
            cartAddress =(TextView) itemView.findViewById(R.id.cart_address_TV);
            rating =(TextView) itemView.findViewById(R.id.rating);
            reviewCount =(TextView) itemView.findViewById(R.id.review_count);

        }

        @Override
        public void onClick(View view) {
            //
            Log.d(TAG,"on click");
            if(clickListener!=null){
                Log.d(TAG,"listner notnull");
                clickListener.itemClick(view,getAdapterPosition());
            }

        }


    }


    public interface RVClickListener{
        //OnClick will work, but for better coding standard i made this listener to call the onclick from the fragment it self..
        public void itemClick(View view, int position);


    }

}
