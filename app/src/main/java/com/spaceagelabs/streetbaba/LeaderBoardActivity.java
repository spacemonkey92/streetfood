package com.spaceagelabs.streetbaba;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.parse.ParseUser;
import com.spaceagelabs.streetbaba.UI.adapters.LeadersAdapter;
import com.spaceagelabs.streetbaba.clientSDK.APIManager;
import com.spaceagelabs.streetbaba.clientSDK.OnComplete;
import com.spaceagelabs.streetbaba.util.ApplicationConstants;

import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity implements LeadersAdapter.RVClickListener {

    String TAG="LeaderBoard";
    LeadersAdapter mAdapter;
    RecyclerView mRecycleView;
    LinearLayoutManager linearLayoutManager; // this is to fix the android null pointer exception.
    List<ParseUser> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mRecycleView = (RecyclerView) findViewById(R.id.leaders_recycle_view);
        fillData();
        Log.d(TAG,"on create ");
    }

    public void fillData(){
        APIManager.getInstance().getTopUsers(new OnComplete<List<ParseUser>>() {
            @Override
            public void done(List<ParseUser> users, Exception e) {
                if(e==null){
                    mUsers=users;
                    Log.d(TAG,"no error got users "+users.size());
                    mAdapter = new LeadersAdapter(LeaderBoardActivity.this,users);
                    mAdapter.setClickListener(LeaderBoardActivity.this);
                    mRecycleView.setAdapter(mAdapter);
                    linearLayoutManager = new LinearLayoutManager(LeaderBoardActivity.this);
                    mRecycleView.setLayoutManager(linearLayoutManager);
                }else{
                    Log.d(TAG,"error occurred while getting leaders "+e.getMessage());;
                }
            }
        });
    }


    @Override
    public void itemClick(View view, int position) {
        ParseUser user =mUsers.get(position);
        String userId = user.getObjectId();
        Intent intent = new Intent(LeaderBoardActivity.this,ProfileActivity.class);
        intent.putExtra(ApplicationConstants.USER_ID_BUNDLE,userId);
        startActivity(intent);
    }
}
