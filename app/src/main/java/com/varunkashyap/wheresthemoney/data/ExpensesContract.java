package com.varunkashyap.wheresthemoney.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by varun on 24-11-2016.
 */

public final class ExpensesContract {

    public static final String CONTENT_AUTHORITY = "com.varunkashyap.wheresthemoney";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_EXPENSE = "expense";

    private ExpensesContract() {
    }

    public static abstract class ExpensesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXPENSE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;

        public static final String TABLE_NAME = "expenses";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_DESC = "desc";
        public static final String COLUMN_MONTH = "month";
        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_EMAIL = "email";
    }

    public static Uri buildGetAllExpensesUri(String email, int month, int year, int day) {
        return buildUri(ExpensesEntry.CONTENT_URI, email, month, year, day, "all");
    }

    public static Uri buildSpent(String email, int month, int year, int day) {
        return buildUri(ExpensesEntry.CONTENT_URI, email, month, year, day, "spent");
    }

    public static Uri buildSummaryUri(String email, int month, int year, int day) {
        return buildUri(ExpensesEntry.CONTENT_URI, email, month, year, day, "summary");
    }

    public static Uri buildExpenseUri(long id) {
        return ContentUris.withAppendedId(ExpensesEntry.CONTENT_URI, id);
    }

    public static String getEmailFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }

    public static String getYearFromUri(Uri uri) {
        return uri.getPathSegments().get(2);
    }

    public static String getMonthFromUri(Uri uri) {
        return uri.getPathSegments().get(3);
    }

    public static String getDayFromUri(Uri uri) {
        return uri.getPathSegments().get(4);
    }

    public static String getIdFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }

    private static Uri buildUri(Uri contentUri, String email, int month, int year, int day, String post) {
        return contentUri.buildUpon()
                .appendPath(email)
                .appendPath(String.valueOf(year))
                .appendPath(String.valueOf(month))
                .appendPath(String.valueOf(day))
                .appendPath(post)
                .build();
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_EXPENSES =
            "CREATE TABLE " + ExpensesEntry.TABLE_NAME + " (" +
                    ExpensesEntry._ID + " INTEGER PRIMARY KEY," +
                    ExpensesEntry.COLUMN_AMOUNT + REAL_TYPE + COMMA_SEP +
                    ExpensesEntry.COLUMN_DESC + TEXT_TYPE + COMMA_SEP +
                    ExpensesEntry.COLUMN_MONTH + INT_TYPE + COMMA_SEP +
                    ExpensesEntry.COLUMN_YEAR + INT_TYPE + COMMA_SEP +
                    ExpensesEntry.COLUMN_DAY + INT_TYPE + COMMA_SEP +
                    ExpensesEntry.COLUMN_EMAIL + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_EXPENSES =
            "DROP TABLE IF EXISTS " + ExpensesEntry.TABLE_NAME;

    public static class ExpensesDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Expenses.db";

        public ExpensesDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(SQL_CREATE_EXPENSES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(ExpensesContract.class.getName(),
                    "Upgrading database from version " + oldVersion + " to " + newVersion +
                            ", which will destroy all old data");
            db.execSQL(SQL_DELETE_EXPENSES);
            onCreate(db);
        }
    }
}