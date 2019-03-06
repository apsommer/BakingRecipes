package com.sommerengineering.recipes;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;

public class WidgetGridService extends RemoteViewsService {

    // constants
    private static final String LOG_TAG = WidgetGridService.class.getSimpleName();

    // go to inner class
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    // this class serves as the adapter for the remote widget grid
    class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        // member variables
        private Context mContext;
        private ArrayList<Dessert> mDesserts;
        private Dessert mDessert;
        private ArrayList<Ingredient> mIngredients;
        private int mWidgetId;

        // set member variables
        GridRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // refresh the desserts list
        // NOTE: the synchronous network call is allowed and best practice
        @Override
        public void onDataSetChanged() {

            // get Udacity URL from utilities class
            URL url = Utilities.getUdacityUrl();

            // perform the HTTP network request
            String responseJson = Utilities.getJsonResponseFromHttp(url);

            // extract Dessert objects from the JSON payload
            mDesserts = Utilities.extractDessertsFromJson(responseJson);

            // get the persistent shared preferences
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(mContext);

            // get the widget dessert preference key string
            String widgetDessertKey = getString(R.string.widget_dessert_key);
            String widgetDessertDefaultValue = getString(R.string.widget_dessert_default_value);

            // get the desired widget dessert from the shared preferences
            String widgetDessertTitle =
                    sharedPreferences.getString(widgetDessertKey, widgetDessertDefaultValue);

            Log.e("~~ ", widgetDessertTitle);

            mDessert = mDesserts.get(0);

            // TODO setup title textviews

            // get ingredients list
            mIngredients = mDessert.getIngredients();
        }

        // behaves as onBindViewHolder() in a recycler adapter
        @Override
        public RemoteViews getViewAt(int position) {

            // if the ingredients list has not been initialized properly then return
            if (mIngredients == null) return null;

            // get the ingredient at this position and extract its metadata
            Ingredient ingredient = mIngredients.get(position);
            String name = StringUtils.capitalize(ingredient.getName().toLowerCase());
            String quantity = String.valueOf(ingredient.getQuantity()) + " ";
            String measure = ingredient.getMeasure().toLowerCase();

            // create a RemoteViews object associated with the widget_item layout
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

            // set text in the various textviews
            views.setTextViewText(R.id.tv_widget_name, name);
            views.setTextViewText(R.id.tv_widget_quantity, quantity);
            views.setTextViewText(R.id.tv_widget_measure, measure);

            // put a bundle into a "fill in" intent
            // this intent is added to the pending intent created in WidgetProvider updateWidgets()
            Intent fillInIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(WidgetProvider.INGREDIENT_ID, ingredient.getId());
            fillInIntent.putExtras(bundle);
            views.setOnClickFillInIntent(R.id.rl_widget_item_container, fillInIntent);

            return views;
        }

        // return size of ingredients arraylist
        @Override
        public int getCount() {
            if (mIngredients == null) return 0;
            return mIngredients.size();
        }

        // all views in this gridview are of the same type
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        // return current position
        @Override
        public long getItemId(int position) {
            return position;
        }

        // yes IDs are stable
        @Override
        public boolean hasStableIds() {
            return false;
        }

        // not used
        @Override
        public void onCreate() {

        }

        // not used
        @Override
        public void onDestroy() {

        }

        // not used
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }
    }
}
