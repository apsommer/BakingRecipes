package com.sommerengineering.recipes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetProvider extends AppWidgetProvider {

    // constants
    private static final String LOG_TAG = WidgetProvider.class.getSimpleName();
    public static final String TOAST_ACTION = "TOAST_ACTION";
    public static final String EXTRA_ITEM = "EXTRA_ITEM";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(LOG_TAG, "~~ onReceive");

        //
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        Log.e(LOG_TAG, "~~ " + intent.getAction());

        //
        if (intent.getAction().equals(TOAST_ACTION)) {

            //
            int widgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            //
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);

            //
            Toast.makeText(context, "Clicked view " + viewIndex + " Widget ID = " + widgetId, Toast.LENGTH_LONG).show();

        }

        //
        super.onReceive(context, intent);
    }

    // Android system calls this every 30 minutes!
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {

        Log.e(LOG_TAG, "~~ onUpdate");

        //
        for (int widgetId : widgetIds) {

            Log.e(LOG_TAG, "~~ onUpdate: widgetID = " + widgetId);

            //
            Intent intent = new Intent(context, WidgetGridService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            //
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_grid);
            remoteViews.setRemoteAdapter(R.id.gd_widget_grid, intent);
            remoteViews.setEmptyView(R.id.gd_widget_grid, R.id.rl_widget_grid_empty_view);

            //
            Intent toastIntent = new Intent(context, WidgetProvider.class);
            toastIntent.setAction(TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            //
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(
                    context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //
            remoteViews.setPendingIntentTemplate(R.id.gd_widget_grid, toastPendingIntent);

            // call into Android framework manager to refresh the widget_item
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
