package com.sommerengineering.recipes;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.net.URL;
import java.util.ArrayList;

class DessertsLoader extends AsyncTaskLoader<ArrayList<Dessert>> {

    // initialize a member variable for the Udacity server URL
    private final URL mUrl;

    // constructor is passed the URL and sets it to the member variable
    public DessertsLoader(Context context, URL url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {

        // immediately move to loadInBackground()
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<Dessert> loadInBackground() {

        // ensure that the URL was initialized properly
        if (mUrl == null) {
            return null;
        }

        // perform the HTTP network request on this background thread
        String responseJson = Utilities.getJsonResponseFromHttp(mUrl);

        // extract Dessert objects from the JSON payload
        return Utilities.extractDessertsFromJson(responseJson);
    }
}
