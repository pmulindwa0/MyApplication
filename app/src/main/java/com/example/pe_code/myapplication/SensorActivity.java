package com.example.pe_code.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pe_code.myapplication.models.QuestionModel;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

public class SensorActivity extends IOIOActivity implements AppCompatCallback{

    public TextView tempView;
    public ArcProgress hProgress;
    public ArcProgress mProgress;
    public ProgressBar tProgress;
    public RatingBar pHRate;
    public TextView pHText;
    TwiMaster twi;
    byte[] response;
    int i;
    String responseString;
    private AppCompatDelegate delegate;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

//        implimenting the appCompact activity.
        delegate = AppCompatDelegate.create(this, this);
        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.activity_sensor);

        //Finally, let's add the Toolbar
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        delegate.setSupportActionBar(toolbar);
        delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        delegate.getSupportActionBar().setIcon(R.drawable.ic_launcher);

        tempView = (TextView) findViewById(R.id.textView);
        hProgress = (ArcProgress) findViewById(R.id.humidity_progress);
        mProgress = (ArcProgress) findViewById(R.id.moisture_progress);
        tProgress = (ProgressBar) findViewById(R.id.progressBar);
        pHRate = (RatingBar) findViewById(R.id.ratingBar);
        pHText = (TextView) findViewById(R.id.pHtextView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Peter's Final Year Trojan", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    class SensorLooper extends BaseIOIOLooper{

        public SensorLooper() {
            super();
        }

        @Override
        protected void setup() throws ConnectionLostException, InterruptedException {
            super.setup();
            twi = ioio_.openTwiMaster(1, TwiMaster.Rate.RATE_100KHz, false);
        }

        @Override
        public void loop() throws ConnectionLostException, InterruptedException {
            super.loop();
            byte[] request = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 };
            response = new byte[9];
            TwiMaster.Result result = twi.writeReadAsync(8, false, request, request.length, response, response.length);
// ...do some stuff while the transaction is taking place...
            result.waitReady();  // blocks until response is available

            try {
                responseString = new String(response,"ASCII");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    tempView.setText(responseString.substring(0,2));
                    tProgress.setProgress(Integer.parseInt(responseString.substring(0, 2)));
                    hProgress.setProgress(Integer.parseInt(responseString.substring(2, 4)));
                    mProgress.setProgress(Integer.parseInt(responseString.substring(4,6)));
                    pHRate.setRating(Float.parseFloat(responseString.substring(6)));
                    pHText.setText(responseString.substring(6));
                }
            });

            Thread.sleep(100);

        }

        @Override
        public void disconnected() {
            super.disconnected();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SensorActivity.this, "IOIO disconnected", Toast.LENGTH_LONG).show();
                }
            });

        }


    }
    @Override
    protected IOIOLooper createIOIOLooper() {
        return new SensorLooper();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sensor, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;

            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;

            case R.id.action_advice:
                Intent intent = new Intent(this, AdvisoryActivity.class);
                startActivity(intent);
                break;

            case R.id.action_send:{

                QuestionModel model = new QuestionModel();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("sensor", responseString);
                editor.commit();
                String http = "http://192.168.1.124/restfullapi/request.php";
                String charset = "UTF-8";
                String query = null;
                String imei= model.getIMEI(this);
                String location = sp.getString("location", "N/A");
                String crop = sp.getString("crop", "N/A");
                String activity = sp.getString("activity","1");
                String sensorReading = sp.getString("sensor", "255465");
                String temp = sensorReading.substring(0,2);
                String moisture = sensorReading.substring(4,6);
                String humidity = sensorReading.substring(2,4);


                try {
                    query = String.format("imei=%s&location=%s&crop=%s&activity=%s&temp=%s&humidity=%s&moisture=%s",
                            URLEncoder.encode(imei, charset),
                            URLEncoder.encode(location, charset),
                            URLEncoder.encode(crop, charset),
                            URLEncoder.encode(activity, charset),
                            URLEncoder.encode(temp, charset),
                            URLEncoder.encode(humidity, charset),
                            URLEncoder.encode(moisture, charset));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String url = http + "?" + query;
                RequestTask request = new RequestTask();
                request.execute(url);
            }

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private class RequestTask extends AsyncTask<String,String,String > {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Sending parameters....",Toast.LENGTH_LONG).show();

        }
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                StringBuilder sb = new StringBuilder();
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Content-Type","application/json");
                connection.connect();

                //Create JSONObject here

                int HttpResult =connection.getResponseCode();
                if(HttpResult ==HttpURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(),"utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    return sb.toString();

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                if(connection!=null)
                    connection.disconnect();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),"Done!", Toast.LENGTH_SHORT).show();
        }
    }


}

