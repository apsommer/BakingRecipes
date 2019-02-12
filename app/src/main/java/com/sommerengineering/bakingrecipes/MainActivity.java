package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DessertAdapter.DessertAdapterOnClickHandler {

    // simple tag for log messages
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // member variables
    private Context mContext;
    private RecyclerView mRecycler;
    private DessertAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // initialize activity and inflate recycler layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize member variables
        mContext = getApplicationContext();
        mRecycler = findViewById(R.id.rv_recycler);
        mProgressBar = findViewById(R.id.pb_progress);

        // empty state of recycler
        TextView emptyStateTv = findViewById(R.id.tv_empty_state);

        // set the number of columns in the recycler grid using a layout manager
        int numOfColumns = Utilities.calculateNumberOfColumns(mContext);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, numOfColumns);
        mRecycler.setLayoutManager(gridLayoutManager);

        // all items in the recycler are the same size
        // notifying the system of this allows for performance improvements
        mRecycler.hasFixedSize();

        // initialize the adapter with a blank list and bind it to the recycler
        if (mAdapter == null) {
            ArrayList<Dessert> desserts = new ArrayList<>();
            mAdapter = new DessertAdapter(mContext, desserts, this);
            mRecycler.setAdapter(mAdapter);
        }



        // TODO background thread for testing
        new Thread(new Runnable() {

            public void run() {

                URL url = Utilities.getUdacityUrl();
                Log.e(LOG_TAG, "~~ " + url.toString());

                String responseJson = Utilities.getJsonResponseFromHttp(url);
                Log.e(LOG_TAG, "~~ " + responseJson);

                ArrayList<Dessert> desserts = Utilities.extractDessertsFromJson(responseJson);
                Log.e(LOG_TAG, "~~ " + desserts.get(0));

            }
        }).start();

    }

    @Override
    public void onRecyclerItemClick(Dessert dessert) {

        // bundle the Dessert into an explicit intent for DetailActivity
        Intent intentToStartDetailActivity = new Intent(this, DetailActivity.class);
        intentToStartDetailActivity.putExtra("selectedDessert", dessert);
        startActivity(intentToStartDetailActivity);
    }

    // check status of internet connectivity
    private boolean isConnected() {

        // get internet connectivity status as a boolean
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // get network connection metadata
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        // boolean representing internet is connected, or in progress connecting
        return (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();
    }
}
