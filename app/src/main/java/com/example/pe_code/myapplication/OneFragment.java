package com.example.pe_code.myapplication;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class OneFragment extends Fragment {


    public OneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        String[] activityArray = {
                "Scan field parameters",
                "Weather Forecast",
                "News Feeds",
                "Advisory Services"

        };
        List<String> activity = new ArrayList<>(
                Arrays.asList(activityArray)
        );
//        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item,R.id.list_item_textView,activity);
        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getActivity(), activityArray);

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0: {
                        Intent intent = new Intent(getActivity(), SensorActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 1: {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 2: {
                        Notification();
                        break;
                    }
                    case 3: {
                        Intent intent = new Intent(getActivity(), AdvisoryActivity.class);
                        startActivity(intent);
                        break;
                    }

                    default: {
                        Toast.makeText(getActivity(),
                                "Click ListItem Number " + position, Toast.LENGTH_LONG)
                                .show();
                    }
                }

            }
        });

        return rootView;
    }
    public void Notification() {
        // Set Notification Title
        String strtitle = getString(R.string.notificationtitle);
        // Set Notification Text
        String strtext = getString(R.string.notificationtext);

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(getActivity(), SensorActivity.class);
        // Send data to NotificationView Class
        intent.putExtra("title", strtitle);
        intent.putExtra("text", strtext);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(getActivity(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(getActivity())
                // Set Icon
                .setSmallIcon(R.drawable.ic_tab_favourite)
                        // Set Ticker Message
                .setTicker(getString(R.string.notificationticker))
                        // Set Title
                .setContentTitle(getString(R.string.notificationtitle))
                        // Set Text
                .setContentText(getString(R.string.notificationtext))
                        // Add an Action Button below Notification
                .addAction(R.drawable.ic_launcher, "Action Button", pIntent)
                        // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                        // Dismiss Notification
                .setAutoCancel(true);

        // Create Notification Manager
        NotificationManager notificationmanager =
                (NotificationManager) getContext().getSystemService(getContext().NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }
}