package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        DessertsAdapter.DessertAdapterOnClickHandler, LoaderManager.LoaderCallbacks<ArrayList<Dessert>> {

    // constants
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int DESSERTS_LOADER_ID = 0;

    // member variables
    private Context mContext;
    private DessertsAdapter mAdapter;

    // Butterknife view binding
    @BindView(R.id.rv_recycler) RecyclerView mRecycler;
    @BindView(R.id.pb_progress) ProgressBar mProgressBar;
    @BindView(R.id.tv_empty_state) TextView mEmptyStateTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // initialize activity and inflate recycler layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the Butterknife view binding library
        ButterKnife.bind(this);

        // get top level application context
        mContext = getApplicationContext();

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
            mAdapter = new DessertsAdapter(mContext, desserts, this);
            mRecycler.setAdapter(mAdapter);
        }

        // if the internet connect is active then start a loader
        if (isConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(DESSERTS_LOADER_ID, null, this);
        }

        // else show the empty state of the recycler
        else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mEmptyStateTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecyclerItemClick(Dessert dessert) {

        // bundle the Dessert into an explicit intent for DetailActivity
        Intent intentToStartDetailActivity = new Intent(this, DetailActivity.class);
        intentToStartDetailActivity.putExtra("selectedDessert", dessert);
        startActivity(intentToStartDetailActivity);
    }

    @NonNull
    @Override
    public Loader<ArrayList<Dessert>> onCreateLoader(int i, @Nullable Bundle bundle) {

        // pass the Udacity server URL to the Loader class
        URL url = Utilities.getUdacityUrl();
        return new DessertsLoader(mContext, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Dessert>> loader, ArrayList<Dessert> desserts) {

        // clear the adapter of any previous results
        mAdapter.clear();

        // check that the list of desserts was loaded correctly
        if (desserts != null && !desserts.isEmpty()) {

            // refresh adapter with results from HTTP request --> JSON payload --> ArrayList<Dessert>
            mAdapter.addAll(desserts);
            mAdapter.notifyDataSetChanged();
        }

        // hide the progress bar
        mProgressBar.setVisibility(View.INVISIBLE);

    }

    // previously created loader is no longer valid and its data should be removed from the UI
    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Dessert>> loader) {

        // setting the recycler adapter as null clears the UI
        mRecycler.setAdapter(null);
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
