
package com.example.studentinformationshasun;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

import webservice.WebService;

public class EventDisplayActivity extends AppCompatActivity {
    Vector<YouTubeVideos> youTubeVideos = new Vector<>();
    TextView  txtNoData;
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private static String ResultString = "";
    private final ArrayList<String> leavestatus_list = new ArrayList<String>(200);
    VideoAdapter videoAdapter;
    public static final String eventImagePath ="http://erp.shasuncollege.edu.in/evarsityshasun/attachment/";


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventvideoviewlist);
        TextView tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText(getResources().getString(R.string.menu_other_details));
        Button btnBack=(Button) findViewById(R.id.button_back);
        Button btnRefresh=(Button) findViewById(R.id.button_refresh);
        btnRefresh.setVisibility(View.INVISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
        onBackPressed();    }
        });
        txtNoData = findViewById(R.id.txtNoData);

        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
       long lngEmployeeId = loginsession.getLong("userid", 1);

        WebService.strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
        WebService.METHOD_NAME = "getUpcomingEvents";

        mRecyclerView = (RecyclerView) findViewById(R.id.rvVideoList);
        if (!CheckNetwork.isInternetAvailable(EventDisplayActivity.this)) {
            Toast.makeText(EventDisplayActivity.this, getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
            return;
        } else {

            AsyncCallWS task = new AsyncCallWS();
            task.execute();


        }

       //        <iframe width="560" height="315" src="https://www.youtube.com/embed/8RcItr6Ka5Q" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
/*
        youTubeVideos.add(new YouTubeVideos("<html><body><font size=8><b>Shasun Shreyas 2K18 Sneak Peek - Day 1</b></font><br><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/Cs3caRySDq4\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>") );
        youTubeVideos.add(new YouTubeVideos("<html><body><font size=8><b>Shasun Shreyas 2K18 Teaser</b></font><br><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/watch?v=pcI1qZ26ER4\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>") );
        youTubeVideos.add(new YouTubeVideos("<html><body><font size=8><b>Shreyas 2K21</b></font><br><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/watch?v=CouYjjHWLJ8\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>") );
        youTubeVideos.add(new YouTubeVideos("<html><body><font size=8><b>International Womens' Day celebrations-2022</b></font><br><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/watch?v=OfVWvn3NZmU\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>") );
        youTubeVideos.add(new YouTubeVideos("<html><body><font size=8><b>E-Content Video</b></font><br><iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/watch?v=wCy-KWDqYVU\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>") );
         videoAdapter = new VideoAdapter(youTubeVideos);
      */
        //recyclerViewVideo.setAdapter(videoAdapter);

    }
    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(EventDisplayActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.loading));
            //show dialog
            dialog.show();
            //Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Log.i(TAG, "doInBackground");
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            leavestatus_list.clear();
            Log.i("TEST",ResultString);
            // {"Status":"Success","Message":"","Data":
            // [{"venue":"Outside Campus","startsat":"01-10-2020  12:12","imageactualname":"",
            // "imagefilename":"","endsat":"01-10-2020 12:12","eventname":"qeqwqwqw","status":"No"
            // ,"youtubelinkurl":"qweqw"},
            // {"venue":"Computer Lab 3-Building 2","startsat":"05-04-2022  13:17",
            // "imageactualname":"Final Poster - Software Testing.pdf","imagefilename":"temp_83066_2
            // 022_4_5_13_18_638","endsat":"05-04-2022 21:00","eventname":"test enty","status":"No",
            // "youtubelinkurl":""},
            // {"venue":"Basement Multipurpose Hall-Building 3","startsat":"02-05-2022  15:37"
            // ,"imageactualname":"Shasun Bazaar - Stall Registration Form.pdf",
            // "imagefilename":"temp_91426_2022_5_2_15_39_911","endsat":"02-05-2022 15:37",
            // "eventname":"Depatment Day","status":"Non Paid Registration","youtubelinkurl":""},
            // {"venue":"Abhayas Hall-Building 2","startsat":"19-05-2022  10:00","imageactualname":"INVITE (7).pdf",
            // "imagefilename":"temp_100361_2022_5_18_10_6_431","endsat":"19-05-2022 12:00","eventname":"test",
            // "status":"Non Paid Registration","youtubelinkurl":"test"},
            // {"venue":"Seminar Hall-Building 2","startsat":"31-05-2022  09:00","imageactualname":"
            // Tulips.jpg","imagefilename":"temp_91758_2022_5_3_8_43_3","endsat":"31-05-2022 12:00",
            // "eventname":"test1","status":"No","youtubelinkurl":""},{"venue":"Seminar Hall-Building 2","
            // startsat":"31-05-2022  09:00","imageactualname":"Tulips.jpg","imagefilename":"temp_91759_2022_5_3_8_44_864","endsat":"31-05-2022 12:00","eventname":"test2","status":"No","youtubelinkurl":""},{"venue":"Seminar Hall-Building 2","startsat":"31-05-2022  09:00","imageactualname":"","imagefilename":"","endsat":"31-05-2022 12:30","eventname":"test","status":"Paid Registartion-Rs.500","youtubelinkurl":""},{"venue":"Seminar Hall-Building 2","startsat":"31-05-2022  10:00","imageactualname":"Tulips.jpg","imagefilename":"temp_91852_2022_5_3_12_18_635","endsat":"31-05-2022 12:00","eventname":"test","status":"No","youtubelinkurl":""}]}

            try {
                JSONObject JSobject = new JSONObject(ResultString);
                if(JSobject.has("Status") && JSobject.getString("Status").equalsIgnoreCase("Success")) {
                    JSONArray temp = new JSONArray(JSobject.getString("Data"));

                        for (int i = 0; i <= temp.length() - 1; i++) {
                            JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                            if(object.getString("youtubelinkurl").length() >0){
                                String test = "https://www.youtube.com/embed/Cs3caRySDq4";
                                //youTubeVideos.add(new YouTubeVideos("<html><body><font size=8><b>"+test+"</b></font><br><iframe width=\"100%\" height=\"100%\" src="+test+" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>") );
                                youTubeVideos.add(new YouTubeVideos("<html><body><font size=8><b>"+object.getString("eventname")+"</b></font><br><iframe width=\"100%\" height=\"100%\" src="+object.getString("youtubelinkurl")+" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>") );
                            }else {
                                String fileName = " - ";
                                if (object.getString("imageactualname").length() > 0) {
                                    fileName = object.getString("imageactualname");
                                }
                                String filePath = " - ";
                                if (object.getString("imagefilename").length() > 0) {
                                    filePath = eventImagePath + object.getString("imagefilename");
                                }
                                if (filePath.length() > 4 && !fileName.contains(".pdf")) {
                                    //   Log.i("TEST",object.getString("eventname") + "##"  + filePath);
                                    leavestatus_list.add(object.getString("eventname") + "##" + filePath);
                                }
                            }
                        }
                        if(leavestatus_list.size() > 0 || youTubeVideos.size() > 0 ) {
                          //  Log.i("TEST","eventname" + "##" );
                            mRecyclerView.setVisibility(View.VISIBLE);
                            txtNoData.setVisibility(View.GONE);

                            mLayoutManager = new LinearLayoutManager(getApplicationContext());                 // Creating a layout Manager
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            EventsAdapter TVA = new EventsAdapter(leavestatus_list, R.layout.eventslistitem);
                            TVA.notifyDataSetChanged();
                           // mRecyclerView.setAdapter(TVA);
                            videoAdapter = new VideoAdapter(youTubeVideos);

                            ConcatAdapter concatAdapter = new ConcatAdapter(TVA,videoAdapter);
                            mRecyclerView.setAdapter(concatAdapter);


                        }else {
                            mRecyclerView.setVisibility(View.GONE);
                            txtNoData.setVisibility(View.VISIBLE);

                        }



                }else{
                    mRecyclerView.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                    txtNoData.setText(JSobject.getString("Message"));
                    Toast.makeText(EventDisplayActivity.this,JSobject.getString("Message"),Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                mRecyclerView.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
                txtNoData.setText(ResultString);
                Toast.makeText(EventDisplayActivity.this,ResultString,Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}