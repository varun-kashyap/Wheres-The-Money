package com.varunkashyap.wheresthemoney;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.varunkashyap.wheresthemoney.data.Expense;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.android.gms.internal.zzs.TAG;

public class ExpenseFragment extends DialogFragment {
    private static final String ARG_DESC = "desc";
    private static final String ARG_AMOUNT = "amount";
    private static final String ARG_NEW = "new";
    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";
    private static final String ARG_DAY = "day";

    private String mDescription;
    private double mAmount;
    private boolean mNewExpense;
    private int mYear;
    private int mMonth;
    private int mDay;
    final static Calendar c = Calendar.getInstance();
    static int year = c.get(Calendar.YEAR);
    static int month = c.get(Calendar.MONTH);
    static int day = c.get(Calendar.DAY_OF_MONTH);
    @BindView(R.id.edit_expense_amount)
    EditText mAmountTxt;
    @BindView(R.id.edit_expense_description)
    EditText mDescriptionText;


    private ExpenseDialogListener mListener;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ExpenseFragment.
     */
    private static ExpenseFragment newInstance(String description, double amount, boolean newExpense, int y, int m, int d) {
        ExpenseFragment fragment = new ExpenseFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_AMOUNT, amount);
        args.putString(ARG_DESC, description);
        args.putBoolean(ARG_NEW, newExpense);
        args.putInt(ARG_YEAR, y);
        args.putInt(ARG_MONTH, m);
        args.putInt(ARG_DAY, d);
        fragment.setArguments(args);
        return fragment;
    }

    public static ExpenseFragment newInstance() {
        return newInstance("", 0, true, year, month, day);
    }

    public static ExpenseFragment newInstance(Expense expense) {
        return newInstance(expense.getDescription(), expense.getAmount(), false, expense.getYear(), expense.getMonth(), expense.getDay());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDescription = getArguments().getString(ARG_DESC);
            mAmount = getArguments().getDouble(ARG_AMOUNT);
            mNewExpense = getArguments().getBoolean(ARG_NEW);
            mYear = getArguments().getInt(ARG_YEAR);
            mMonth = getArguments().getInt(ARG_MONTH);
            mDay = getArguments().getInt(ARG_DAY);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int y, int m, int d) {
            // Do something with the date chosen by the user
            Context context = getActivity().getApplicationContext();
            Toast.makeText(context, "Selected date: " + d + "/" + (m + 1) + "/" + y, Toast.LENGTH_LONG).show();
            year = y;
            month = m;
            day = d;
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_expense, null);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        ButterKnife.bind(this, view);

        mDescriptionText.setText(mDescription);
        mAmountTxt.setText(String.valueOf(mAmount));


        if (ExpenseDialogListener.class.isInstance(getActivity())) {
            mListener = ExpenseDialogListener.class.cast(getActivity());
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement ExpenseDialogListener");
        }


        builder.setView(view)

                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final double amount = Double.parseDouble(mAmountTxt.getText().toString());
                        final String description = mDescriptionText.getText().toString();
                        int y = year;
                        int m = month;
                        int d = day;
                        Log.i(TAG, "dAY" + d + " Month " + m);
                        if (amount != mAmount || !description.equals(mDescription)) {
                            mListener.onOk(amount, description, y, m, d);
                        } else {
                            ExpenseFragment.this.getDialog().cancel();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ExpenseFragment.this.getDialog().cancel();
                    }
                });
        if (!mNewExpense) {
            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onDelete();
                }
            });
        } else {
            Context context = getActivity().getApplicationContext();
            Toast.makeText(context, "Selected date: " + day + "/" + month + 1 + "/" + year + " (Today)", Toast.LENGTH_LONG).show();
        }

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (ExpenseDialogListener.class.isInstance(context)) {
            mListener = ExpenseDialogListener.class.cast(context);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ExpenseDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ExpenseDialogListener {
        void onOk(double amount, String description, int year, int month, int date);

        void onDelete();
    }
}
