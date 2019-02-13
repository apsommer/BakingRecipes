package com.sommerengineering.bakingrecipes;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

// final access modifier because no objects of this class will ever be created
// this class is a holder for various static methods related to the network connection
public final class Utilities {

    // simple tag for log messages
    private static final String LOG_TAG = Utilities.class.getSimpleName();

    // URL constants
    private static final String UDACITY_BASE_URL =
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

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
    public static String getJsonResponseFromHttp(URL url){

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

    // convert JSON payload to an list of desserts
    public static ArrayList<Dessert> extractDessertsFromJson(String responseJson) {

        // initialize an empty ArrayList
        ArrayList<Dessert> desserts = new ArrayList<>();

        // parse JSON string can throw JSONException
        try {

            // go down one level of the JSON payload
            JSONArray allDeserts = new JSONArray(responseJson);

            // loop through the desserts
            for (int i = 0; i < allDeserts.length(); i++) {

                // current dessert
                JSONObject dessert = allDeserts.getJSONObject(i);

                // extract the dessert's basic attributes
                int id = dessert.getInt("id");
                String name = dessert.getString("name");
                int servings = dessert.getInt("servings");
                String imagePath = dessert.getString("image");

                // initialize an empty ArrayList
                ArrayList<Ingredient> ingredients = new ArrayList<>();

                // go down one level of the JSON payload
                JSONArray allIngredients = dessert.getJSONArray("ingredients");

                // loop through the ingredients
                for (int j = 0; j < allIngredients.length(); j++) {

                    // current ingredient
                    JSONObject ingredient = allIngredients.getJSONObject(j);

                    // extract the ingredient's basic attributes
                    int ingredientId = j;
                    int quantity = ingredient.getInt("quantity");
                    String measure = ingredient.getString("measure");
                    String ingredientName = ingredient.getString("ingredient");

                    // create new Ingredient object and add it to the ArrayList
                    ingredients.add(new Ingredient(ingredientId, ingredientName, quantity, measure));

                }

                // initialize an empty ArrayList
                ArrayList<Step> steps = new ArrayList<>();

                // go down one level of the JSON payload
                JSONArray allSteps = dessert.getJSONArray("steps");

                // loop through the steps
                for (int j = 0; j < allSteps.length(); j++) {

                    // current step
                    JSONObject step = allSteps.getJSONObject(j);

                    // extract the steps's basic attributes
                    int stepId = step.getInt("id");
                    String shortDescription = step.getString("shortDescription");
                    String description = step.getString("description");
                    String videoPath = step.getString("videoURL");
                    String thumbnailPath = step.getString("thumbnailURL");

                    // create new Ingredient object and add it to the ArrayList
                    steps.add(new Step(stepId, shortDescription, description, videoPath, thumbnailPath));

                }

                desserts.add(new Dessert(id, name, servings, imagePath, ingredients, steps));

            }


        // log exception stack trace
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the movies JSON results: ", e);
        }

        return desserts;
    }

    // calculate number of columns for the recycler using the device pixel density
    // the column width will always be approximately the same physical size regardless of device or orientation
    public static int calculateNumberOfColumns(Context context) {

        // get the current device screen dimensions
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        // get current device width in dp
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        // conversion is 160 dp = 1 inch
        float itemWidth = context.getResources().getInteger(R.integer.recycler_item_width);

        // number of columns is how many R.dimen.button can fit in this device width
        return (int) (dpWidth / itemWidth);

    }

    // convert dp to raw pixels
    public static int dpToPx(Context context, float dp) {

        // get the current device screen dimensions
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        return Math.round(dp * displayMetrics.density);
    }

}
