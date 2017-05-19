package com.varunkashyap.wheresthemoney;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import com.varunkashyap.wheresthemoney.data.ExpensesContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class ExpenseAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = ExpenseAppWidgetProvider.class.getSimpleName();

    public static final String ACTION_UPDATE_TOTAL_SPENT = "update.spent";
    public static final String EXTRA_EMAIL = "email";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "onUpdate called on " + Arrays.toString(appWidgetIds));

        for (int widgetId : appWidgetIds) {
            String email = WidgetConfigureActivity.loadEmailPref(context, widgetId);
            updateAppWidget(context, appWidgetManager, widgetId, email);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (ACTION_UPDATE_TOTAL_SPENT.equals(action)) {
            String email = intent.getStringExtra(EXTRA_EMAIL);
            Log.i("AppWidgetProvider", "update widget for " + email);

            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, ExpenseAppWidgetProvider.class);
            int[] appWidgetIds = mgr.getAppWidgetIds(cn);

            List<Integer> filteredWidgetIds = new ArrayList<>();
            for (int appWidgetId : appWidgetIds) {
                if (email.equals(WidgetConfigureActivity.loadEmailPref(context, appWidgetId))) {
                    filteredWidgetIds.add(appWidgetId);
                }
            }
            appWidgetIds = new int[filteredWidgetIds.size()];
            int idx = 0;
            for (int appWidgetId : filteredWidgetIds) {
                appWidgetIds[idx++] = appWidgetId;
                updateAppWidget(context, mgr, appWidgetId, email);
            }
        }

        super.onReceive(context, intent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String email) {
        Log.d(TAG, "updateAppWidget appWidgetId=" + appWidgetId + " email=" + email);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.expense_appwidget);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(MainActivity.ACTION_ADD_EXPENSE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_button, pendingIntent);

        double totalSpent = 0;
        {
            final Calendar cal = Calendar.getInstance();
            final int month = cal.get(Calendar.MONTH);
            final int year = cal.get(Calendar.YEAR);
            final int day = cal.get(Calendar.DAY_OF_MONTH);
            //TODO use async task instead
            Cursor spent = context.getContentResolver().query(
                    ExpensesContract.buildSpent(email, month, year, day), null, null, null, null);
            if (spent.moveToFirst()) {
                totalSpent = spent.getDouble(0);
            }
            spent.close();
        }
        remoteViews.setTextViewText(R.id.widget_text, Utils.formatCurrency(totalSpent));

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

}

