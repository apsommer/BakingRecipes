package com.sommerengineering.bakingrecipes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.net.URL;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    // simple tag for log messages
    private static final String LOG_TAG = Utilities.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final URL url = Utilities.getUdacityUrl();
        Log.e(LOG_TAG, "~~ " + url.toString());

        new Thread(new Runnable() {
            public void run() {
                String responseJson = Utilities.getResponseFromHttp(url);
                Log.e(LOG_TAG, "~~ " + responseJson);
            }
        }).start();

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
