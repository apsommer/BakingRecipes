package com.sommerengineering.recipes;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

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
    public class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory,
            Loader.OnLoadCompleteListener<ArrayList<Dessert>> {

        private static final int LOADER_ID = 0;

        // member variables
        private Context mContext;
        private ArrayList<Dessert> mDesserts;
        private DessertsLoader mLoader;
        private int mWidgetId;

        // required constructor sets context member variable and gets widget ID
        public GridRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // start a loader to retrieve the list of desserts
        @Override
        public void onCreate() {

            // get Udacity URL
            URL url = Utilities.getUdacityUrl();

            // initialize the Loader
            mLoader = new DessertsLoader(mContext, url);

            // this class has the listener callbacks
            mLoader.registerListener(LOADER_ID, this);

            // loaders automatically retrieve data on a background thread
            mLoader.startLoading();

        }

        @Override
        public void onLoadComplete(@NonNull Loader<ArrayList<Dessert>> loader, @Nullable ArrayList<Dessert> desserts) {

            Log.e(LOG_TAG, "~~ onLoadComplete");
            mDesserts = desserts;
        }

        // restart loader to refresh the desserts list
        @Override
        public void onDataSetChanged() {

            // loaders automatically retrieve data on a background thread
//            if (mLoader != null) {
//                mLoader.startLoading();
//            }
        }

        // behaves as onBindViewHolder() in a recycler adapter
        @Override
        public RemoteViews getViewAt(int position) {

            // if the desserts list has not been initialized properly then return
            if (mDesserts == null) return null;

            // get the dessert at this position
            Dessert dessert = mDesserts.get(position);

            // extract dessert attributes
            String name = dessert.getName();
            int servings = dessert.getServings();

            // create a RemoteViews object associated with the widget_item layout
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

            // set text in textviews
            views.setTextViewText(R.id.tv_widget_name, name);
            views.setTextViewText(R.id.tv_servings, String.valueOf(servings));

            // add the dessert to a bundle
            Bundle bundle = new Bundle();
            bundle.putInt(WidgetProvider.EXTRA_ITEM, position);
            bundle.putSerializable("selectedDessert", dessert);

            // put the bundle into a "fill in" intent
            // this is added to the pending intent created in WidgetProvider updateWidgets()
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(bundle);
            views.setOnClickFillInIntent(R.id.rl_widget_item_container, fillInIntent);

            return views;
        }

        // cancel the loader and unregister the listener
        @Override
        public void onDestroy() {

            if (mLoader != null) {
                mLoader.stopLoading();
                mLoader.cancelLoad();
                mLoader.unregisterListener(this);
            }
        }

        // return size of Desserts array
        @Override
        public int getCount() {
            if (mDesserts == null) return 0;
            return mDesserts.size();

        }

        // not used
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        // all views in this gridview are of the same type
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        // return current position
        @Override
        public long getItemId(int i) {
            return i;
        }

        // yes IDs are stable
        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
