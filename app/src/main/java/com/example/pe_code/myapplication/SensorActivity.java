package com.example.pe_code.myapplication;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.io.UnsupportedEncodingException;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

public class SensorActivity extends IOIOActivity implements AppCompatCallback {

    public TextView tempView;
    String temp;
    String moisture;
    TwiMaster twi;
    byte[] response;
    int i;
    String responseString;
    private AppCompatDelegate delegate;

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
//        ArcProgress prg = (ArcProgress) findViewById(R.id.arc_progress);
//        prg.setProgress(22);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            response = new byte[4];
            TwiMaster.Result result = twi.writeReadAsync(8, false, request, request.length, response, response.length);
// ...do some stuff while the transaction is taking place...
            result.waitReady();  // blocks until response is available

            try {
                responseString = new String(response,"ASCII");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            temp = responseString.substring(0,2);
            moisture = responseString.substring(2,4);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tempView.setText(temp);
                    tempView.setEnabled(true);
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
                    tempView.setEnabled(false);
                }
            });

        }


    }
    @Override
    protected IOIOLooper createIOIOLooper() {
        return new SensorLooper();
    }

}

