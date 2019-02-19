package com.sommerengineering.recipes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {

        // relevant functionality for when the first widget is created
    }

    // Android system runs calls this every 30 minutes!
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // update all active widgets
        for (int appWidgetId : appWidgetIds) {

            // helper method
            updateRecipeWidget(context, appWidgetManager, appWidgetId, R.drawable.play);
        }
    }

    // refresh the widget View with an intent to start MainActivity
    static void updateRecipeWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, int imageId) {

        // construct a RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        // Update image
        views.setImageViewResource(R.id.iv_placeholder, imageId);

        // Widgets allow click handlers to only launch pending intents
        views.setTextViewText(R.id.tv_placeholder, "456");

        // pending intent to be executed by the system when the imageview is clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // bind the pending intent to the View
        views.setOnClickPendingIntent(R.id.iv_placeholder, pendingIntent);

        // call into Android framework widget manager to refresh the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onDisabled(Context context) {

        // relevant functionality for when the last widget is disabled
    }
}
