package com.sommerengineering.bakingrecipes;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

// final access modifier because no objects of this class will ever be created
// this class is a holder for various static methods related to the network connection
public final class Utilities {

    // simple tag for log messages
    private static final String LOG_TAG = Utilities.class.getSimpleName();

    // URL constants
    private static final String UDACITY_BASE_URL = "http://go.udacity.com/android-baking-app-json";

    // create a URL for the given Udacity JSON server
    static URL getUdacityUrl() {

        // catch a malformed URL
        URL url = null;
        try {
            url = new URL(UDACITY_BASE_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

}
