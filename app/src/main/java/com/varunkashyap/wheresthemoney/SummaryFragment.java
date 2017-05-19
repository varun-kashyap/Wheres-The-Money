package com.varunkashyap.wheresthemoney;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.varunkashyap.wheresthemoney.data.SummaryPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SummaryFragment extends DialogFragment {

    private static final String ARG_SUMMARY = "summary";

    private SummaryPoint[] summary;

    @BindView(R.id.chart)
    BarChart mChart;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param summary list of summary expenses for the past 6 months.
     * @return A new instance of fragment ExpenseFragment.
     */
    public static SummaryFragment newInstance(SummaryPoint[] summary) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_SUMMARY, summary);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            summary = (SummaryPoint[]) getArguments().getParcelableArray(ARG_SUMMARY);
        } else {
            summary = new SummaryPoint[0];
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_summary, null);

        ButterKnife.bind(this, view);

        initChart();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because it's going in the dialog layout
        builder.setView(view)
                // add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SummaryFragment.this.getDialog().dismiss();
                    }
                });
        return builder.create();
    }

    private void initChart() {
        Log.i("SummaryFragment", "expenses summary: " + Arrays.toString(summary));

//        mChart.setDescription("chart description");
        mChart.setDescription("");
        mChart.setData(generateChartData());
    }

    private BarData generateChartData() {
        List<String> labels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < summary.length; i++) {
            final SummaryPoint sp = summary[i];
            labels.add(Utils.formatMonth(sp.getMonth()));
            entries.add(new BarEntry((float) sp.getExpenses(), i));
        }

        IBarDataSet ds = new BarDataSet(entries, "expenses");

        return new BarData(labels, ds);
    }
}
