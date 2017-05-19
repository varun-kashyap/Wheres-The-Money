package com.varunkashyap.wheresthemoney;

import android.content.ContentValues;
import android.util.Log;

import com.varunkashyap.wheresthemoney.data.ExpensesContract;

import java.text.DateFormatSymbols;
import java.text.NumberFormat;

/**
 * Created by varun on 24-11-2016.
 */

public class Utils {
    private static final String TAG = "MainActivity";
    private static String[] months = new DateFormatSymbols().getMonths();

    public static String formatMonth(int month) {
        return months[month];
    }

    public static String formatCurrency(double amount) {
        Log.i(TAG, "Formatted Currency" + amount + ", " + NumberFormat.getCurrencyInstance().format(amount));
        return NumberFormat.getCurrencyInstance().format(amount);

    }

    public static ContentValues expenseValues(String email, String desc, double amount, int month, int year, int day) {
        ContentValues values = new ContentValues();
        values.put(ExpensesContract.ExpensesEntry.COLUMN_DESC, desc);
        values.put(ExpensesContract.ExpensesEntry.COLUMN_AMOUNT, amount);
        values.put(ExpensesContract.ExpensesEntry.COLUMN_MONTH, month);
        values.put(ExpensesContract.ExpensesEntry.COLUMN_YEAR, year);
        values.put(ExpensesContract.ExpensesEntry.COLUMN_DAY, day);
        values.put(ExpensesContract.ExpensesEntry.COLUMN_EMAIL, email);
        return values;
    }

}
