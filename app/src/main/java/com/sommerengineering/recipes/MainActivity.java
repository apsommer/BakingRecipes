package com.sommerengineering.recipes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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
    public static final String SELECTED_DESSERT = "SELECTED_DESSERT";
    private static final int DESSERTS_LOADER_ID = 0;

    // member variables
    private Context mContext;
    private DessertsAdapter mAdapter;
    private ArrayList<Dessert> mDesserts;
    private Dessert mDessert;

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

        // initialize the adapter with a widget_provider list and bind it to the recycler
        if (mAdapter == null) {
            ArrayList<Dessert> desserts = new ArrayList<>();
            mAdapter = new DessertsAdapter(mContext, desserts, this);
            mRecycler.setAdapter(mAdapter);
        }

        // if the internet connection is active then start a loader
        boolean isConnected = Utilities.isConnected(mContext);
        if (isConnected) {
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

        // bundle the Dessert into an explicit intent for RecipeActivity
        Intent intentToStartRecipeActivity = new Intent(this, RecipeActivity.class);
        intentToStartRecipeActivity.putExtra(SELECTED_DESSERT, dessert);
        startActivity(intentToStartRecipeActivity);
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

        // set the member variable
        mDesserts = desserts;

        // check that the list of desserts was loaded correctly
        if (mDesserts != null && !mDesserts.isEmpty()) {

            // refresh adapter with results from HTTP request --> JSON payload --> ArrayList<Dessert>
            mAdapter.addAll(mDesserts);
            mAdapter.notifyDataSetChanged();
        }

        // hide the progress bar
        mProgressBar.setVisibility(View.INVISIBLE);

        // triggers call to onPrepareOptionsMenu()
        invalidateOptionsMenu();
    }

    // previously created loader is no longer valid and its data should be removed from the UI
    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Dessert>> loader) {

        // setting the recycler adapter as null clears the UI
        mRecycler.setAdapter(null);
    }

    // inflate blank overflow menu at top right of action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return true;
    }

    // populate the overflow menu with dessert names
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // delete existing contents of overflow menu
        invalidateOptionsMenu();

        // if the desserts list has finished loading, add the dessert names as menu items
        if (mDesserts != null && !mDesserts.isEmpty()) {

            for (int i = 0; i < mDesserts.size(); i++) {
                menu.add(mDesserts.get(i).getName());
            }
        }

        // continue with normal framework behavior for options menu
        return super.onPrepareOptionsMenu(menu);
    }

    // items within the overflow menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // get name of the selected dessert menu item
        String selectedName = String.valueOf(item.getTitle());

        // find the dessert ID with same name as the preference
        int dessertId = 0;
        for (int i = 0; i < mDesserts.size(); i++) {
            if (mDesserts.get(i).getName().equals(selectedName)) {
                dessertId = i;
            }
        }

        // set the member variable to this selected dessert
        mDessert = mDesserts.get(dessertId);

        // get the widget dessert preference key strings
        String widgetNameKey = getString(R.string.widget_name_key);
        String widgetServingsKey = getString(R.string.widget_servings_key);

        // create a shared preference editor
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // persistently store the preference for the widget dessert name and servings
        editor.putString(widgetNameKey, selectedName);
        editor.putString(widgetServingsKey, String.valueOf(mDessert.getServings()));
        editor.apply();

        // create an intent to start the WidgetProvider class
        Intent intentToStartWidgetProvider = new Intent(this, WidgetProvider.class);
        intentToStartWidgetProvider.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        // get all the widget IDs
        ComponentName componentName = new ComponentName(getApplication(), WidgetProvider.class);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(componentName);

        // add the widget IDs to the intent
        intentToStartWidgetProvider.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

        // raising this broadcast triggers WidgetProvider onUpdate()
        sendBroadcast(intentToStartWidgetProvider);

        // continue with the framework default menu handling
        return super.onOptionsItemSelected(item);
    }
}

