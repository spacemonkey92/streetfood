package com.spaceagelabs.streetbaba.UI.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.spaceagelabs.streetbaba.R;


import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Nitin on 27/8/15.
 */
public class LeadersAdapter extends RecyclerView.Adapter<LeadersAdapter.MyViewHolder>{

    private static final String TAG = "LeadersAdapter";
    private final LayoutInflater inflater;
    private List<ParseUser> data = Collections.emptyList();
    private RVClickListener clickListener;

    public LeadersAdapter(Context context, List<ParseUser> data){
        inflater = LayoutInflater.from(context);
        this.data=data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =inflater.inflate(R.layout.leader_cell,viewGroup,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onViewAttachedToWindow(MyViewHolder holder) {
        Log.d(TAG,"view attached to window");
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        Log.d(TAG, "detached ");
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int i) {
        ParseUser currentUser = data.get(i);
        viewHolder.userName.setText(currentUser.getString("name"));
        viewHolder.userPoints.setText(String.valueOf(currentUser.getInt("points")));
        HashMap<String, Object> profile = new HashMap<>();
        profile = (HashMap) currentUser.get("profile");
        final String fbUserId = (String) profile.get("facebookId");
        String fbPictureUrl = "https://graph.facebook.com/" + fbUserId + "/picture?type=large";
        new ImageDownloaderTask(viewHolder.userImage).execute(fbPictureUrl);
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

        ImageView userImage;
        TextView userName;
        TextView userPoints;
        View mView;
        View cell;


        public MyViewHolder(View itemView) {

            super(itemView);
            Log.d(TAG, "setting images and items");
            mView=itemView;
            cell = (View) itemView.findViewById(R.id.leaders_relate_layout);
            userImage =(ImageView) itemView.findViewById(R.id.dp_image);
            userName =(TextView) itemView.findViewById(R.id.profile_name);
            userPoints=(TextView) itemView.findViewById(R.id.points_textView);



            cell.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG,"on click");
            if(clickListener!=null){
                Log.d(TAG,"listner notnull");
                clickListener.itemClick(view,getAdapterPosition());
            }
        }
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != 200) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }


    public interface RVClickListener{
        //OnClick will work, but for better coding standard i made this listener to call the onclick from the fragment it self..
        public void itemClick(View view, int position);
    }

    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.mipmap.ic_name_hat);
                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }
    }

}
