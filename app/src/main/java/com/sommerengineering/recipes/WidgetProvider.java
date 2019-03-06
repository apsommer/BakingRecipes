package com.sommerengineering.recipes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

    // constants
    private static final String LOG_TAG = WidgetProvider.class.getSimpleName();
    private static final String GRID_ITEM_CLICKED = "GRID_ITEM_CLICKED";
    public static final String INGREDIENT_ID = "INGREDIENT_ID";

    // system broadcasts and the explicit broadcast from main activity triggers this method
    @Override
    public void onReceive(Context context, Intent intent) {

        // filter for the custom defined action of grid item clicked
        if (intent.getAction() != null && intent.getAction().equals(GRID_ITEM_CLICKED)) {

            // extract the ID
            int ingredientId = intent.getIntExtra(INGREDIENT_ID, 0);

            // just a log message for now; this shows that individual items in the grid hold their
            // own "fill-in" intents correctly
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

        Log.e("~~ ", "onUpdate");

        // loop through all widgets associated with this app
        for (int widgetId : widgetIds) {

            // intent to start the remote grid service with the widget ID as an extra
            Intent intent = new Intent(context, WidgetGridService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            // construct a remoteview associated to layout holding the gridview
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_grid);

            // set the remote adapter on the gridview using the intent for the grid service
            remoteViews.setRemoteAdapter(R.id.gd_widget_grid, intent);

            // define an empty state for gridview
            remoteViews.setEmptyView(R.id.gd_widget_grid, R.id.rl_widget_grid_empty_view);

            // get the persistent shared preferences
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);

            // get the widget dessert preference keys and default values
            String widgetNameKey = context.getString(R.string.widget_name_key);
            String widgetNameDefaultValue = context.getString(R.string.widget_name_default_value);
            String widgetServingsKey = context.getString(R.string.widget_servings_key);
            String widgetServingsDefaultValue = context.getString(R.string.widget_servings_default_value);

            // get the desired widget dessert attributes from the shared preferences
            String widgetName =
                    sharedPreferences.getString(widgetNameKey, widgetNameDefaultValue);
            String widgetServings =
                    sharedPreferences.getString(widgetServingsKey, widgetServingsDefaultValue);

            // set text in the widget title textviews
            remoteViews.setTextViewText(R.id.tv_widget_name, widgetName);
            remoteViews.setTextViewText(R.id.tv_widget_servings, widgetServings);
            remoteViews.setViewVisibility(R.id.tv_widget_name, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.rl_widget_subtitle_container, View.VISIBLE);

            // base intent for the widget provider class is applied to every grid item
            Intent intentToStartWidgetProvider = new Intent(context, WidgetProvider.class);

            // add custom action string and widget ID to the intent
            intentToStartWidgetProvider.setAction(GRID_ITEM_CLICKED);
            intentToStartWidgetProvider.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            // put the intent into the pending intent format
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, 0, intentToStartWidgetProvider, PendingIntent.FLAG_UPDATE_CURRENT);

            // the "pending intent template" means this pending intent is applied to every item in the grid
            remoteViews.setPendingIntentTemplate(R.id.gd_widget_grid, pendingIntent);

            // call into Android framework manager to refresh the widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

            // this framework call triggers WidgetGridService onDataSetChanged()
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.gd_widget_grid);
        }
    }

    // not used
    @Override
    public void onEnabled(Context context) {}

    // not used
    @Override
    public void onDisabled(Context context) {}
}
