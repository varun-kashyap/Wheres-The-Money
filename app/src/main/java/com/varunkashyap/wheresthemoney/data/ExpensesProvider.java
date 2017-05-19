package com.varunkashyap.wheresthemoney.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.varunkashyap.wheresthemoney.data.ExpensesContract.ExpensesDbHelper;
import com.varunkashyap.wheresthemoney.data.ExpensesContract.ExpensesEntry;

import java.util.Arrays;

/**
 * Created by varun on 24-11-2016.
 */

public class ExpensesProvider extends ContentProvider {
    private static final String LOG_TAG = ExpensesProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private ExpensesDbHelper mDBHelper;

    static final int INSERT_EXPENSE = 100;
    static final int EXPENSE_WITH_ID = 101;
    static final int ALL_EXPENSES = 102;
    static final int GET_TOTAL_SPENT = 103;
    static final int GET_SUMMARY = 104;

    public static String[] sAllColumns = {
            ExpensesEntry._ID,
            ExpensesEntry.COLUMN_AMOUNT,
            ExpensesEntry.COLUMN_DESC,
            ExpensesEntry.COLUMN_MONTH,
            ExpensesEntry.COLUMN_YEAR,
            ExpensesEntry.COLUMN_DAY,
            ExpensesEntry.COLUMN_EMAIL
    };

    public static int COLUMN_IDX_ID = 0;
    public static int COLUMN_IDX_AMOUNT = 1;
    public static int COLUMN_IDX_DESC = 2;
    public static int COLUMN_IDX_MONTH = 3;
    public static int COLUMN_IDX_YEAR = 4;
    public static int COLUMN_IDX_DAY = 5;
    public static int COLUMN_IDX_EMAIL = 6;

    private static final SQLiteQueryBuilder sExpenseQueryBuilder;

    static {
        sExpenseQueryBuilder = new SQLiteQueryBuilder();
        sExpenseQueryBuilder.setTables(ExpensesEntry.TABLE_NAME);
    }

    private static String[] getSelectionArgsFromUri(Uri uri) {

        return new String[]{
                ExpensesContract.getEmailFromUri(uri),
                ExpensesContract.getYearFromUri(uri),
                ExpensesContract.getMonthFromUri(uri),
                ExpensesContract.getDayFromUri(uri),
        };
    }

    private static String[] getIdFromUri(Uri uri) {
        return new String[]{ExpensesContract.getIdFromUri(uri)};
    }


    private static final String sEmail = ExpensesEntry.COLUMN_EMAIL + " = ? ";

    private Cursor getExpenses(Uri uri) {
        final String yearMonth = ExpensesEntry.COLUMN_YEAR + " DESC, " + ExpensesEntry.COLUMN_MONTH + " DESC, " + ExpensesEntry.COLUMN_DAY + " DESC";
        Log.i(LOG_TAG, "querying: " + getSelectionArgsFromUri(uri)[3]);
        String email = ExpensesContract.getEmailFromUri(uri);
        String[] test = {email};
        return sExpenseQueryBuilder.query(mDBHelper.getReadableDatabase(),
                sAllColumns,
                sEmail, test,
                null, null, yearMonth);
    }

    private Cursor getExpense(Uri uri) {
        return sExpenseQueryBuilder.query(mDBHelper.getReadableDatabase(),
                sAllColumns,
                ExpensesEntry._ID + " = ?", getIdFromUri(uri),
                null, null, null);

    }

    private Cursor getSummary(Uri uri) {
        final String yearMonth = ExpensesEntry.COLUMN_YEAR + ", " + ExpensesEntry.COLUMN_MONTH;
        String email = ExpensesContract.getEmailFromUri(uri);
        String[] test = {email};
        return sExpenseQueryBuilder.query(mDBHelper.getReadableDatabase(),
                new String[]{ExpensesEntry.COLUMN_YEAR, ExpensesEntry.COLUMN_MONTH, "SUM(" + ExpensesEntry.COLUMN_AMOUNT + ")"}, sEmail, test, yearMonth, null, yearMonth);
    }

    private Cursor getSpent(Uri uri) {
        String email = ExpensesContract.getEmailFromUri(uri);
        String[] test = {email};
        return sExpenseQueryBuilder.query(mDBHelper.getReadableDatabase(),
                new String[]{"SUM(" + ExpensesEntry.COLUMN_AMOUNT + ")"},
                sEmail, test,
                null, null, null);
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ExpensesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, ExpensesContract.PATH_EXPENSE, INSERT_EXPENSE);
        uriMatcher.addURI(authority, ExpensesContract.PATH_EXPENSE + "/#", EXPENSE_WITH_ID);
        uriMatcher.addURI(authority, ExpensesContract.PATH_EXPENSE + "/*/#/#/#/all", ALL_EXPENSES);
        uriMatcher.addURI(authority, ExpensesContract.PATH_EXPENSE + "/*/#/#/#/spent", GET_TOTAL_SPENT);
        uriMatcher.addURI(authority, ExpensesContract.PATH_EXPENSE + "/*/#/#/#/summary", GET_SUMMARY);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new ExpensesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.i(LOG_TAG, "querying: " + uri);

        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case EXPENSE_WITH_ID:
                cursor = getExpense(uri);
                break;
            case ALL_EXPENSES:
                cursor = getExpenses(uri);
                break;
            case GET_TOTAL_SPENT:
                cursor = getSpent(uri);
                break;
            case GET_SUMMARY:
                cursor = getSummary(uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case EXPENSE_WITH_ID:
                return ExpensesEntry.CONTENT_ITEM_TYPE;
            case ALL_EXPENSES:
                return ExpensesEntry.CONTENT_TYPE;
            case GET_TOTAL_SPENT:
                return ExpensesEntry.CONTENT_ITEM_TYPE;
            case GET_SUMMARY:
                return ExpensesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case INSERT_EXPENSE:
                long _id = db.insert(ExpensesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ExpensesContract.buildExpenseUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(ExpensesEntry.CONTENT_URI, null);
        db.close();
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "delete(" + uri + ", " + selection + ", " + Arrays.toString(selectionArgs));

        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case EXPENSE_WITH_ID:
                selection = ExpensesEntry._ID + " = ?";
                selectionArgs = new String[]{ExpensesContract.getIdFromUri(uri)};
                rowsDeleted = db.delete(ExpensesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            Log.d(LOG_TAG, "notifying content resolver that " + rowsDeleted + " rows were deleted");
            getContext().getContentResolver().notifyChange(ExpensesEntry.CONTENT_URI, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "update " + uri + ", values: " + values);
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case EXPENSE_WITH_ID:
                selection = ExpensesEntry._ID + " = ?";
                selectionArgs = new String[]{ExpensesContract.getIdFromUri(uri)};
                rowsUpdated = db.update(ExpensesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(ExpensesEntry.CONTENT_URI, null);
        }

        return rowsUpdated;
    }
}