package com.sommerengineering.recipes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

    // constants
    private static final String LOG_TAG = WidgetProvider.class.getSimpleName();
    private static final String GRID_ITEM_CLICKED = "GRID_ITEM_CLICKED";
    public static final String INGREDIENT_ID = "INGREDIENT_ID";

    // system broadcast calls this method
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(LOG_TAG, "~~ onReceive");
        Log.e(LOG_TAG, "~~ " + intent.getAction());

        // onReceive() is called for many more intent <ACTIONS> produced by the Android system
        // filter for the custom defined action
        if (intent.getAction() != null && intent.getAction().equals(GRID_ITEM_CLICKED)) {

            // extract the ID
            int ingredientId = intent.getIntExtra(INGREDIENT_ID, 0);

            Log.e(LOG_TAG, "~~ ingredientID " + ingredientId);

            // start main activity
            Intent intentToStartMainActivity = new Intent(context, MainActivity.class);
            intentToStartMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentToStartMainActivity);

        }

        // proceed to normal onRecieve() behavior
        super.onReceive(context, intent);
    }

    // Android system calls this every 30 minutes!
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {

        Log.e(LOG_TAG, "~~ onUpdate");

        // loop through all widgets associated with this app
        for (int widgetId : widgetIds) {

            Log.e(LOG_TAG, "~~ onUpdate: widgetID = " + widgetId);

            // intent to start the remote grid service with the widget ID as an extra
            Intent intent = new Intent(context, WidgetGridService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            // construct a remoteview associated to layout holding the gridview
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_grid);

            // set the remote adapter on the gridview using the intent for the grid service
            remoteViews.setRemoteAdapter(R.id.gd_widget_grid, intent);

            // define an empty state for gridview
            remoteViews.setEmptyView(R.id.gd_widget_grid, R.id.rl_widget_grid_empty_view);

            // base intent for the widget provider class is applied to every grid item
            Intent toastIntent = new Intent(context, WidgetProvider.class);

            // add custom action string and widget ID to the intent
            toastIntent.setAction(GRID_ITEM_CLICKED);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            // put the intent into the pending intent format
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(
                    context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // the "pending intent template" means this pending intent is applied to every item in the grid
            remoteViews.setPendingIntentTemplate(R.id.gd_widget_grid, toastPendingIntent);

            // call into Android framework manager to refresh the widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

            // this framework call triggers WidgetGridService onDataSetChanged()
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.gd_widget_grid);
        }
    }

    @Override
    public void onEnabled(Context context) {

        Log.e(LOG_TAG, "~~ onEnabled");

    }

    @Override
    public void onDisabled(Context context) {
        Log.e(LOG_TAG, "~~ onDisabled");
    }
}
