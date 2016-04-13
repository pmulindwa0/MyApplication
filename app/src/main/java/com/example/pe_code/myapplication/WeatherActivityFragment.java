package com.example.pe_code.myapplication;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class WeatherActivityFragment extends Fragment {

    View rootView;
    ListView weatherListView;
   ListAdapter mForecastAdapter;

    public WeatherActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        weatherListView = (ListView)rootView.findViewById(R.id.weatherList);
//        String[] weatherItems = {
//                "Sunday.. Sunny..10/60",
//                "Monday.. Sunny..10/60",
//                "Tuesday.. Sunny..10/60",
//                "wed.. Sunny..10/60",
//                "Friday.. Sunny..10/60"
//        };

//        mForecastAdapter = new ArrayAdapter(getActivity(),
//                R.layout.list_item_forecast,
//                R.id.list_item_forecast_textView,
//                weatherItems);
//
//        weatherListView.setAdapter(mForecastAdapter);
        return rootView;
    }

    public class downloadJSON extends AsyncTask<Void,Void,String >{

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        String format = "json";
        String units = "metric";
        int numDays = 14;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(),"Starting download",Toast.LENGTH_LONG).show();

        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, "Mabarara")
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, "22a83fef2bb8ad91ac485328c5284b80")
                        .build();

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null){
                    //Do nothing
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null){

                    buffer.append(line +"\n");
                }
                if(buffer.length() == 0){
                    //Stream was empty
                    return null;

                }
                forecastJsonStr = buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return forecastJsonStr;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);

            Toast.makeText(getActivity(),"Downlload Complete", Toast.LENGTH_LONG).show();
            Log.d("DOWNLOAD", jsonString);
        }
    }
}