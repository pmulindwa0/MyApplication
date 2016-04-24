package com.example.pe_code.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pe_code.myapplication.models.QuestionModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A placeholder fragment containing a simple view.
 */
public class WeatherActivityFragment extends Fragment {

    View rootView;
    public ListView weatherListView;


    public WeatherActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        weatherListView = (ListView)rootView.findViewById(R.id.weatherList);


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_weather, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                break;

            case R.id.action_refresh:

                DownloadJSON weatherTask = new DownloadJSON();
                weatherTask.execute();
                break;

            case R.id.action_settings:

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class DownloadJSON extends AsyncTask<Void,Void,List<String> >{

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(),"Starting download",Toast.LENGTH_LONG).show();

        }

        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> weatherList = null;
            try{
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast


                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?q=Mbarara&mode=json&units=metric&cnt=7&appid=22a83fef2bb8ad91ac485328c5284b80");
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
                JSONObject weather = new JSONObject(forecastJsonStr);
                JSONArray days = weather.getJSONArray("list");

                weatherList = new ArrayList<>();
                for ( int x=0; x < days.length(); x++){
                    JSONObject dayInfo = days.getJSONObject(x);
                    /**** gettting date **************/
//                    Long time = dayInfo.getLong("dt");
//                    Date date = new Date();
                    java.util.Date today = new java.util.Date();
                    final long ONE_DAY_MILLISCONDS = ( 25 * 60 * 60 * 1000);

                    java.util.Date date = new java.util.Date((x * ONE_DAY_MILLISCONDS) + today.getTime());
                    SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
                    String dateStr = format.format(date).toString();
                    /**** gettting date **************/

                    /**** gettting temperature **************/
                    JSONObject main = dayInfo.getJSONObject("main");
                    float temp = (float) main.getDouble("temp_max");
                    /**** gettting temperature **************/

                    /**** gettting description **************/
                    JSONArray weatherArray = dayInfo.getJSONArray("weather");
                    JSONObject weatherInfo = weatherArray.getJSONObject(0);
                    String desc = weatherInfo.getString("description");
                    String finalStr = dateStr + " - " + desc + " - " + temp + " C" ;
                    weatherList.add(finalStr);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return weatherList;
        }

        @Override
        protected void onPostExecute(List<String> jsonList) {
            super.onPostExecute(jsonList);

            Toast.makeText(getActivity(),"Download Complete", Toast.LENGTH_LONG).show();
            ListAdapter mForecastAdapter;
            mForecastAdapter = new ArrayAdapter(getActivity(),
                    R.layout.list_item_forecast,
                    R.id.list_item_forecast_textView,
                    jsonList);

            weatherListView.setAdapter(mForecastAdapter);

        }
    }
}