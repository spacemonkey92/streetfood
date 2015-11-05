package com.spaceagelabs.streetbaba.UI.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spaceagelabs.streetbaba.R;
import com.spaceagelabs.streetbaba.UI.viewmodel.HomeCardsModel;

import java.util.Collections;
import java.util.List;

/**
 * Created by Nitin on 27/8/15.
 */
public class HomeRVAdapter extends RecyclerView.Adapter<HomeRVAdapter.MyViewHolder>{

    private static final String TAG = "HomeRVAdapter";
    private final LayoutInflater inflater;
    private List<HomeCardsModel> data = Collections.emptyList();
    private RVClickListener clickListener;

    public HomeRVAdapter(Context context,List<HomeCardsModel> data ){
        inflater = LayoutInflater.from(context);
        this.data=data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =inflater.inflate(R.layout.home_cells,viewGroup,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        HomeCardsModel currentData = data.get(i);
        viewHolder.icon.setImageResource(currentData.getInconId());
        viewHolder.heading.setText(currentData.getHeading());
        viewHolder.subheading.setText(currentData.getSubheading());

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
        CardView cv;
        ImageView icon;
        TextView heading;
        TextView subheading;
        RelativeLayout layout;
        View mView;


        public MyViewHolder(View itemView) {

            super(itemView);


            Log.d(TAG, "setting images and items");
            mView=itemView;
            cv = (CardView)itemView.findViewById(R.id.home_cv);
            layout = (RelativeLayout) itemView.findViewById(R.id.card_rl);
            icon =(ImageView) itemView.findViewById(R.id.home_rv_icon);
            heading =(TextView) itemView.findViewById(R.id.heading);
            subheading =(TextView) itemView.findViewById(R.id.subhead);
            layout.setOnClickListener(this);

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
        public void itemClick(View view, int position );


    }

}
