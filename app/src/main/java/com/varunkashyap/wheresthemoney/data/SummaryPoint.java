package com.varunkashyap.wheresthemoney.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.varunkashyap.wheresthemoney.Utils;

/**
 * Created by varun on 24-11-2016.
 */

public class SummaryPoint implements Parcelable {
    private final int year;
    private final int month;
    private final double expenses;

    public static SummaryPoint of(Cursor cursor) {
        return new SummaryPoint(cursor.getInt(0), cursor.getInt(1), cursor.getDouble(2));
    }

    public SummaryPoint(int year, int month, double expenses) {
        this.year = year;
        this.month = month;
        this.expenses = expenses;
    }

    public int getMonth() {
        return month;
    }

    public double getExpenses() {
        return expenses;
    }

    @Override
    public String toString() {
        return Utils.formatMonth(month) + ": " + Utils.formatCurrency(expenses);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeDouble(expenses);
    }

    public static final Parcelable.Creator<SummaryPoint> CREATOR
            = new Parcelable.Creator<SummaryPoint>() {

        @Override
        public SummaryPoint createFromParcel(Parcel source) {
            int year = source.readInt();
            int month = source.readInt();
            double expenses = source.readDouble();

            return new SummaryPoint(year, month, expenses);
        }

        @Override
        public SummaryPoint[] newArray(int size) {
            return new SummaryPoint[0];
        }
    };
}

