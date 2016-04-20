package com.example.pe_code.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by PE-CODE on 2/23/2016.
 */
public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] activityArray;

    public MySimpleArrayAdapter(Context context, String[] activityArray) {
        super(context, R.layout.rowlayout, activityArray);
        this.context = context;
        this.activityArray = activityArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(activityArray[position]);

        // Change the icon for Windows and iPhone
        String s = activityArray[position];
//        if (s.startsWith("Overview")) {
//            imageView.setImageResource(R.mipmap.ic_overview);
//        }
        switch(s){
            case "Scan soil parameters":
                imageView.setImageResource(R.mipmap.ic_diagonalise);
                break;
            case "Weather Forecast":
                imageView.setImageResource(R.mipmap.ic_weather);
                break;
            case "News Feeds":
                imageView.setImageResource(R.mipmap.ic_risk);
                break;
            case "Advisory Services":
                imageView.setImageResource(R.mipmap.ic_analysis);
                break;
            default:imageView.setImageResource(R.mipmap.ic_launcher);

        }

        return rowView;
    }

}
