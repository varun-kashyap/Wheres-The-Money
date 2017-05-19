package com.varunkashyap.wheresthemoney;

import android.accounts.AccountManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.common.AccountPicker;

/**
 * Created by varun on 24-11-2016.
 */

public class WidgetConfigureActivity extends AppCompatActivity {
    private static final String TAG = WidgetConfigureActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;

    private static final String PREFS_NAME = "com.varunkashyap.wheresthemoney.ExpenseAppWidgetProvider";
    private static final String PREF_PREFIX_KEY = "prefix_";

    private int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.widget_config);

        // get the app widget ID
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent chooseAccountIntent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(chooseAccountIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            handleSignInResult(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
        }
    }

    private void handleSignInResult(String accountName) {
        Log.d(TAG, "handleSignInResult:" + accountName);

        // Signed in successfully, show authenticated UI.
        saveEmail(accountName);

        // update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.expense_appwidget);
        appWidgetManager.updateAppWidget(mAppWidgetId, views);

        // Push widget update to surface with newly set prefix
        ExpenseAppWidgetProvider.updateAppWidget(this, appWidgetManager, mAppWidgetId, accountName);

        // return a result
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private void saveEmail(String email) {
        Log.d(TAG, "saving (" + PREF_PREFIX_KEY + mAppWidgetId + ", " + email + ")");
        SharedPreferences.Editor prefs = getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + mAppWidgetId, email);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadEmailPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String prefix = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        Log.d(TAG, "loaded (" + PREF_PREFIX_KEY + appWidgetId + ", " + prefix + ")");
        return prefix;
    }

}

