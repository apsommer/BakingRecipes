package com.sommerengineering.recipes;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

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

    //
    public class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private static final int LOADER_ID = 0;

        // member variables
        private Context mContext;
        private ArrayList<Dessert> mDesserts;
        private Dessert mDessert;
        private ArrayList<Ingredient> mIngredients;
        private int mWidgetId;

        // required constructor sets context member variable and gets widget ID
        public GridRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // refresh the desserts list
        // synchronous network call is allowed and best practice
        @Override
        public void onDataSetChanged() {

            Log.e(LOG_TAG, "~~ onDataSetChanged");

            // get Udacity URL
            URL url = Utilities.getUdacityUrl();

            // perform the HTTP network request
            String responseJson = Utilities.getJsonResponseFromHttp(url);

            // extract Dessert objects from the JSON payload
            mDesserts = Utilities.extractDessertsFromJson(responseJson);

            // TODO arbitrarily display ingredients for Dessert 0 in widget
            mDessert = mDesserts.get(0);

            // get ingredients list
            mIngredients = mDessert.getIngredients();

        }

        // behaves as onBindViewHolder() in a recycler adapter
        @Override
        public RemoteViews getViewAt(int position) {

            Log.e(LOG_TAG, "~~ getViewAt");

            // if the ingredients list has not been initialized properly then return
            if (mIngredients == null) return null;

            // get the ingredient at this position and extract its basic attributes
            Ingredient ingredient = mIngredients.get(position);
            String name = StringUtils.capitalize(ingredient.getName().toLowerCase());
            String quantity = String.valueOf(ingredient.getQuantity()) + " ";
            String measure = ingredient.getMeasure().toLowerCase();

            // create a RemoteViews object associated with the widget_item layout
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

            // set text in textviews
            views.setTextViewText(R.id.tv_widget_name, name);
            views.setTextViewText(R.id.tv_widget_quantity, quantity);
            views.setTextViewText(R.id.tv_widget_measure, measure);

            // put the bundle into a "fill in" intent
            // this is added to the pending intent created in WidgetProvider updateWidgets()
            Intent fillInIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(WidgetProvider.INGREDIENT_ID, ingredient.getId());
            fillInIntent.putExtras(bundle);
            views.setOnClickFillInIntent(R.id.rl_widget_item_container, fillInIntent);

            return views;
        }

        // return size of Desserts array
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
