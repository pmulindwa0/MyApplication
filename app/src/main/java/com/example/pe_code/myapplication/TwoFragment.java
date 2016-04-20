package com.example.pe_code.myapplication;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.example.pe_code.myapplication.models.QuestionModel;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends Fragment {
    public ListView qnList;

    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_two, container, false);
        qnList = (ListView) v.findViewById(R.id.answerList);

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_solution, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                /********on refreshing******************/
                QuestionModel model = new QuestionModel();
                Context contx;
                contx=getActivity();
                String http = "http://192.168.1.124/restfullapi/get_answers.php";
                String charset = "UTF-8";
                String query = null;
                String imei= model.getIMEI(getActivity());

                try {
                    query = String.format("imei=%s", URLEncoder.encode(imei, charset));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String url = http + "?" + query;
                new AnswerTask(contx).execute(url);

                /********on refreshing******************/

                break;
            case R.id.action_settings:

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public class AnswerTask extends AsyncTask<String,String,List<QuestionModel> >{
        private Context mContext;
        ProgressDialog pDialog;
        public AnswerTask(Context contx) {
            super();
            this.mContext=contx;
        }

        @Override
        protected List<QuestionModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader br = null;
            String finalString = null;
            StringBuffer finalBuffer = null;
            String date = null;
            String answer;
            String question = null;
            int x;
            List<QuestionModel> questionModelList = null;
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
                    JSONObject parentOject = new JSONObject(finalJson);
                    JSONArray parentArray = parentOject.getJSONArray("questions");

                     finalBuffer = new StringBuffer();
                     questionModelList = new ArrayList<>();
                    for ( x=0; x < parentArray.length(); x++){
                        QuestionModel questionModel = new QuestionModel();
                        JSONObject finalObject = parentArray.getJSONObject(x);

                        questionModel.setQuestion(finalObject.getString("question"));
                        questionModel.setAnswer(finalObject.getString("answer"));
                        questionModel.setDate(finalObject.getString("answer_date"));

                        finalBuffer.append(question + " - " + date + "\n");
                      /****  adding the final object in a list****/
                        questionModelList.add(questionModel);
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
            return questionModelList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressbar initials
            pDialog = new ProgressDialog(this.mContext);
            pDialog.setMessage("Connecting to the server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(List<QuestionModel> s) {
            pDialog.dismiss();
            super.onPostExecute(s);
            QuestionAdapter adapter = new QuestionAdapter(getActivity(), R.layout.row,s);
            qnList.setAdapter(adapter);

        }
    }
    public class QuestionAdapter extends ArrayAdapter{

        private List<QuestionModel> questionModelList;
        private  int resource;
        private LayoutInflater inflater;

        public QuestionAdapter(Context context, int resource, List<QuestionModel> objects) {
            super(context, resource, objects);
            questionModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView = inflater.inflate(resource, null);
            }

            TextView question = (TextView)convertView.findViewById(R.id.question);
            TextView answer = (TextView)convertView.findViewById(R.id.answer);
            TextView date = (TextView)convertView.findViewById(R.id.date);

            question.setText(questionModelList.get(position).getQuestion());
            question.setMovementMethod(new ScrollingMovementMethod());
            answer.setText(questionModelList.get(position).getAnswer());
            answer.setMovementMethod(new ScrollingMovementMethod());
            date.setText(questionModelList.get(position).getDate());

            return convertView;
        }
    }

}
