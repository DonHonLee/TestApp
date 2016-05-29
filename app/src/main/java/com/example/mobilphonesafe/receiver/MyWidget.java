package com.example.mobilphonesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.example.mobilphonesafe.services.UpdateWidgetService;

/**
 * Created by ${"李东宏"} on 2015/11/22.
 */
public class MyWidget extends AppWidgetProvider {
    @Override
    public void onDisabled(Context context) {
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.stopService(intent);
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
