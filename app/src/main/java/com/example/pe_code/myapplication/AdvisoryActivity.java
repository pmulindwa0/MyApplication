package com.example.pe_code.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pe_code.myapplication.models.QuestionModel;
import com.example.pe_code.myapplication.models.SensorDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AdvisoryActivity extends AppCompatActivity {
    public ListView adviseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adviseList = (ListView) findViewById(R.id.listAdvise);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_advice, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                /********on refreshing******************/
                QuestionModel model = new QuestionModel();
                String http = "http://192.168.1.124/restfullapi/send_response.php";
                String charset = "UTF-8";
                String query = null;
                String imei= model.getIMEI(getApplicationContext());

                    try {
                        query = String.format("imei=%s", URLEncoder.encode(imei, charset));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String url = http + "?" + query;

                    AdviseTask ATask = new AdviseTask();
                    ATask.execute(url);

                /********on refreshing******************/

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class AdviseTask extends AsyncTask<String,String,List<SensorDataModel>>{
        ProgressDialog pDialog;

        public AdviseTask() {
            super();
        }

        @Override
        protected List<SensorDataModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader br = null;
            String hum = null;
            StringBuffer finalBuffer = null;
            String date = null;
            String temp = null;
            String advice = null;
            int x;
            List<SensorDataModel> adviceModelList = null;
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
                    br = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(),"utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    String finalJson = sb.toString();
                    JSONObject parentObject = new JSONObject(finalJson);
                    JSONArray parentArray = parentObject.getJSONArray("advice");

                    adviceModelList = new ArrayList<>();
                    for ( x=0; x < parentArray.length(); x++){
                        SensorDataModel adviceModel = new SensorDataModel();
                        JSONObject finalObject = parentArray.getJSONObject(x);

                        adviceModel.setAdvice(finalObject.getString("advise"));
                        adviceModel.setTdate(finalObject.getString("tdate"));
                        adviceModel.setHumidity(finalObject.getString("humidity"));
                        adviceModel.setMoisture(finalObject.getString("moisture"));
                        adviceModel.setTemperature(finalObject.getString("temp"));

                        /****  adding the final object in a list****/
                        adviceModelList.add(adviceModel);
                    }



                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if(connection!=null) {
                    connection.disconnect();
                }
                try {
                    if (br!=null) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return adviceModelList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Connecting to the server...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(List<SensorDataModel> s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), "download complete...", Toast.LENGTH_LONG).show();
            AdviceAdapter adapter = new AdviceAdapter(getApplicationContext(), R.layout.advise_row,s);
            adviseList.setAdapter(adapter);
        }
    }

    public class AdviceAdapter extends ArrayAdapter {

        private List<SensorDataModel> adviceModelList;
        private  int resource;
        private LayoutInflater inflater;

        public AdviceAdapter(Context context, int resource, List<SensorDataModel> objects) {
            super(context, resource, objects);
            adviceModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView = inflater.inflate(resource, null);
            }

            TextView temperature = (TextView)convertView.findViewById(R.id.temp);
            TextView humidity = (TextView)convertView.findViewById(R.id.humidity);
            TextView date = (TextView)convertView.findViewById(R.id.date);
            TextView advice = (TextView)convertView.findViewById(R.id.advice);
            TextView moisture = (TextView)convertView.findViewById(R.id.moisture);

            temperature.setText("Temperature: " + adviceModelList.get(position).getTemperature());
            humidity.setText("Humidity: " + adviceModelList.get(position).getHumidity());
            date.setText(adviceModelList.get(position).getTdate());
            advice.setText(adviceModelList.get(position).getAdvice());
            moisture.setText("Moisture: " + adviceModelList.get(position).getMoisture());

            return convertView;
        }
    }

}
