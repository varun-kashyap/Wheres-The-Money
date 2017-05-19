package com.varunkashyap.wheresthemoney;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyActivity extends AppCompatActivity {

    private EditText mBaseEditText;
    private TextView mCurrencyTextView;
    private Button mEnterButton;
    private String mCurrencyDataJSON;

    private TextView mBaseTextView;
    private TextView mDateTextView;
    private TextView mRateTextView1;
    private TextView mRateTextView2;
    private TextView mRateTextView3;
    private TextView mRateTextView4;
    private TextView mRateTextView5;
    public static final String TAG = MainActivity.class.getSimpleName();
    double inr, usd, gbp, eur, jpy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);
        double[] money = {0, 0, 0, 0, 0};
        mBaseEditText = (EditText) findViewById(R.id.baseEditText);
        mCurrencyTextView = (TextView) findViewById(R.id.currencyTextView);
        mEnterButton = (Button) findViewById(R.id.enterButton);
        mBaseTextView = (TextView) findViewById(R.id.baseTextView);
        mDateTextView = (TextView) findViewById(R.id.dateTextView);
        mRateTextView1 = (TextView) findViewById(R.id.rateTextView1);
        mRateTextView2 = (TextView) findViewById(R.id.rateTextView2);
        mRateTextView3 = (TextView) findViewById(R.id.rateTextView3);
        mRateTextView4 = (TextView) findViewById(R.id.rateTextView4);
        mRateTextView5 = (TextView) findViewById(R.id.rateTextView5);
        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String base = mBaseEditText.getText().toString();
                getCurrency(base);
            }
        });
    }

    /**
     * To retrieve a list of currency exchange rates based on the provided base currency
     */
    private void getCurrency(String base) {
        // API/URL (with base parameter) provided by fixer.io
        String currencyUrl = "http://api.fixer.io/latest?base=" + base;

        if (isNetworkAvailable()) {
            //Initialize client object
            OkHttpClient client = new OkHttpClient();
            //Initialize request object
            Request request = new Request.Builder()
                    .url(currencyUrl)
                    .build();
            //Initialize call object to send the request
            Call call = client.newCall(request);
            /*
                enqueue() method supports asynchronous processing (process in background thread) so that user
                can still interact with the app while it is getting the data from the API

            */
            call.enqueue(new Callback() {
                @Override
                /*onFailure() will be triggered if the callback was a failure exception*/
                public void onFailure(Call call, IOException e) {
                    // To execute certain action in the UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CurrencyActivity.this, "Opps! Please try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                /*onResponse() will be triggered if the callback was a HTTP response*/
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        // Get the data in JSON format from the response object
                        mCurrencyDataJSON = response.body().string();
                        // To check the status of the response object
                        if (response.isSuccessful()) {
                            try {
                                // Create a new JSONObject based on the response JSON data
                                JSONObject current = new JSONObject(mCurrencyDataJSON);
                                // Retrieve the rates JSON object
                                JSONObject rates = current.getJSONObject("rates");
                                // Retrieve the value via the key

                                final String base = current.getString("base");
                                final String date = current.getString("date");
                                if (base.equals("INR")) {


                                    inr = 1.0;
                                    usd = rates.getDouble("USD");
                                    gbp = rates.getDouble("GBP");
                                    eur = rates.getDouble("EUR");
                                    jpy = rates.getDouble("JPY");
                                } else if (base.equals("USD")) {
                                    inr = rates.getDouble("INR");
                                    usd = 1.0;
                                    gbp = rates.getDouble("GBP");
                                    eur = rates.getDouble("EUR");
                                    jpy = rates.getDouble("JPY");
                                } else if (base.equals("GBP")) {
                                    inr = rates.getDouble("INR");
                                    usd = rates.getDouble("USD");
                                    gbp = 1.0;
                                    eur = rates.getDouble("EUR");
                                    jpy = rates.getDouble("JPY");
                                } else if (base.equals("EUR")) {
                                    inr = rates.getDouble("INR");
                                    usd = rates.getDouble("USD");
                                    gbp = rates.getDouble("GBP");
                                    eur = 1.0;
                                    jpy = rates.getDouble("JPY");
                                } else if (base.equals("JPY")) {
                                    inr = rates.getDouble("INR");
                                    usd = rates.getDouble("USD");
                                    gbp = rates.getDouble("GBP");
                                    eur = rates.getDouble("EUR");
                                    jpy = 1.0;
                                }
                                // To execute certain action in the UI thread
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //mCurrencyTextView.setText(mCurrencyDataJSON);
                                        mBaseTextView.setText("Base: " + base + "");
                                        mDateTextView.setText("Date: " + date + "");
                                        mRateTextView1.setText("INR: " + inr + "");
                                        mRateTextView2.setText("USD: " + usd + "");
                                        mRateTextView3.setText("GBP: " + gbp + "");
                                        mRateTextView4.setText("EUR: " + eur + "");
                                        mRateTextView5.setText("JPY: " + jpy + "");
                                    }
                                });
                            } catch (JSONException e) {
                                Log.e(TAG, "Exception caught: ", e);
                            }
                        } else {
                            // To execute certain action in the UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CurrencyActivity.this, "Opps! Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, "Device is currently offline", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * To check if the device is currently connected to the Internet
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }
}


