package com.varunkashyap.wheresthemoney.data;

import android.database.Cursor;

import com.varunkashyap.wheresthemoney.Utils;

import java.util.Calendar;

/**
 * Created by varun on 24-11-2016.
 */

public class Expense {
    private long id;
    private String description;
    private double amount;
    private int month;
    private int year;
    private int day;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public int getDay() {
        return day;
    }


    public void setDay(int day) {
        this.day = day;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setTime(long theTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(theTime);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
    }

    public String getFormattedAmount() {
        return Utils.formatCurrency(amount);
    }

    public String getFormattedDate() {
        return (getDay() + "/" + (getMonth() + 1) + "/" + getYear());
    }

    public static Expense from(final String description, final double amount, long theTime) {
        Expense expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setTime(theTime);
        return expense;
    }

    public static Expense from(long id, String desc, double amount, long theTime) {
        Expense expense = new Expense();
        expense.setId(id);
        expense.setAmount(amount);
        expense.setDescription(desc);
        expense.setTime(theTime);
        return expense;
    }

    public static Expense from(Cursor cursor) {
        Expense expense = new Expense();
        expense.setId(cursor.getInt(ExpensesProvider.COLUMN_IDX_ID));
        expense.setAmount(cursor.getDouble(ExpensesProvider.COLUMN_IDX_AMOUNT));
        expense.setDescription(cursor.getString(ExpensesProvider.COLUMN_IDX_DESC));
        expense.setMonth(cursor.getInt(ExpensesProvider.COLUMN_IDX_MONTH));
        expense.setDay(cursor.getInt(ExpensesProvider.COLUMN_IDX_DAY));
        expense.setYear(cursor.getInt(ExpensesProvider.COLUMN_IDX_YEAR));
        return expense;
    }

    @Override
    public String toString() {
        return description + " " + amount;
    }
}
