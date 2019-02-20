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

        //
        if (intent.getAction().equals(TOAST_ACTION)) {

            //
            int widgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            //
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);

            //
            Toast.makeText(context, "Clicked view " + viewIndex, Toast.LENGTH_LONG).show();

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

            //
            Intent intent = new Intent(context, WidgetGridService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            //
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // TODO R.layout.widget_layout
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_grid);

            // TODO R.id.stack_view
            // remoteViews.setRemoteAdapter(widgetId, R.id.gd_widget_grid, intent);
            remoteViews.setRemoteAdapter(R.id.gd_widget_grid, intent); // TODO depreciated call use this if possible

            // define an empty state (R.id.stack_view, R.id.empty_view)
            remoteViews.setEmptyView(R.id.gd_widget_grid, R.id.rl_widget_grid_empty_view);

//            // base intent for all items in the grid
//            // specific dessert added to intent in WidgetGridService
//            Intent itemClickBaseIntent = new Intent(context, RecipeActivity.class);
//            PendingIntent pendingItemClickBaseIntent =
//                    PendingIntent.getActivity(context, 0,
//                            itemClickBaseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            views.setPendingIntentTemplate(R.id.widget_grid_view, pendingItemClickBaseIntent);

            //
            Intent toastIntent = new Intent(context, WidgetGridService.class);
            toastIntent.setAction(TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            //
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(
                    context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // R.id.stack_view
            remoteViews.setPendingIntentTemplate(R.id.gd_widget_grid, toastPendingIntent);

            // call into Android framework manager to refresh the widget_item
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

        // continue to framework update sequence
        super.onUpdate(context, appWidgetManager, widgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Log.e(LOG_TAG, "~~ onEnabled");
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.e(LOG_TAG, "~~ onDisabled");
        super.onDisabled(context);
    }
}
