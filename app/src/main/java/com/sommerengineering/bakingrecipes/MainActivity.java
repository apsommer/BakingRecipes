package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
