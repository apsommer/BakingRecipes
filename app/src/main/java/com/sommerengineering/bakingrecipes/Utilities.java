package com.sommerengineering.bakingrecipes;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

// final access modifier because no objects of this class will ever be created
// this class is a holder for various static methods related to the network connection
public final class Utilities {

    // simple tag for log messages
    private static final String LOG_TAG = Utilities.class.getSimpleName();

    // URL constants
    private static final String UDACITY_BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

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

    // obtain JSON response string from API endpoint
    public static String getResponseFromHttp(URL url){

        // initialize response to null
        String responseJson = null;

        // HTTP connections can throw IOException
        try {

            // open the network connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // create an input stream from the opened connection
            InputStream inputStream = urlConnection.getInputStream();

            // "\\A" delimiter denotes the end of server response
            // therefore the entire response is read in one shot
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            // check if there is anything in the stream
            if (scanner.hasNext()) {
                responseJson = scanner.next();
            }

            // close the connection
            urlConnection.disconnect();

        } catch (IOException e) {
            Log.e(LOG_TAG, "~~ " + e.toString());
        }

        return responseJson;
    }

}
