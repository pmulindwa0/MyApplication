package com.example.pe_code.myapplication;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.pe_code.myapplication.models.QuestionModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThreeFragment extends Fragment {
private static final String DIALOG_PERSON = "person";



    public ThreeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_three, container, false);
        final EditText qnEditText = (EditText) v.findViewById(R.id.question);
        Button btnPerson = (Button) v.findViewById(R.id.person_choice);

        btnPerson.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                PersonDialog dialog = new PersonDialog();
                dialog.show(fm, DIALOG_PERSON);

            }
        });
        ImageButton btnSend = (ImageButton) v.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                QuestionModel model = new QuestionModel();
                Context contx;
                contx=getActivity();
                String http = "http://192.168.1.122/restfullapi/question.php";
                String charset = "UTF-8";
                String query = null;
                String imei= model.getIMEI(getActivity());
                String question = qnEditText.getText().toString();
                try {
                   query = String.format("imei=%s&question=%s", URLEncoder.encode(imei, charset),
                           URLEncoder.encode(question, charset));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String url = http + "?" + query;
                new QuestionTask(contx).execute(url);
            }
        });
        return v;
    }


}
 class QuestionTask extends AsyncTask<String,String,String >{
    private Context mContext;
    ProgressDialog pDialog;
    public QuestionTask(Context contx) {
        this.mContext=contx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        progressbar initials
        pDialog = new ProgressDialog(this.mContext);
        pDialog.setMessage("Sending question..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

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
        pDialog.dismiss();
        Toast.makeText(this.mContext," Question submitted successfully", Toast.LENGTH_SHORT).show();
    }
}
