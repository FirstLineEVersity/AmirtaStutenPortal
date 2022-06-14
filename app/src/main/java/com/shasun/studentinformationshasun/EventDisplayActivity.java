
package com.shasun.studentinformationshasun;

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

import com.shasun.studentinformationshasun.R;

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
        TextView tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText(getResources().getString(R.string.menu_other_details));
        Button btnBack= findViewById(R.id.button_back);
        Button btnRefresh= findViewById(R.id.button_refresh);
        btnRefresh.setVisibility(View.INVISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
        onBackPressed();    }
        });
        txtNoData = findViewById(R.id.txtNoData);

        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
       long lngStudentId = loginsession.getLong("userid", 1);

        WebService.strParameters = new String[]{"Long", "name", String.valueOf(lngStudentId)};
        WebService.METHOD_NAME = "getUpcomingEventsforStudents";

        mRecyclerView = findViewById(R.id.rvVideoList);
        if (!CheckNetwork.isInternetAvailable(EventDisplayActivity.this)) {
            Toast.makeText(EventDisplayActivity.this, getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
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
            Log.i("TEST : ", WebService.METHOD_NAME );
            Log.i("TEST : ",ResultString );

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if(ResultString.length()>1) {
                leavestatus_list.clear();

                try {
                    JSONObject JSobject = new JSONObject(ResultString);
                    if (JSobject.has("Status") && JSobject.getString("Status").equalsIgnoreCase("Success")) {
                        JSONArray temp = new JSONArray(JSobject.getString("Data"));

                        for (int i = 0; i <= temp.length() - 1; i++) {
                            JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                            if (object.getString("youtubelinkurl").length() > 0) {
                                String test = "https://www.youtube.com/embed/Cs3caRySDq4";
                                //youTubeVideos.add(new YouTubeVideos("<html><body><font size=8><b>"+test+"</b></font><br><iframe width=\"100%\" height=\"100%\" src="+test+" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>") );
                                youTubeVideos.add(new YouTubeVideos("<html><body><font size=8><b>" + object.getString("eventname") + "</b></font><br><iframe width=\"100%\" height=\"100%\" src=" + object.getString("youtubelinkurl") + " allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>"));
                            } else {
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
                        if (leavestatus_list.size() > 0 || youTubeVideos.size() > 0) {
                            //  Log.i("TEST","eventname" + "##" );
                            mRecyclerView.setVisibility(View.VISIBLE);
                            txtNoData.setVisibility(View.GONE);

                            mLayoutManager = new LinearLayoutManager(getApplicationContext());                 // Creating a layout Manager
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            EventsAdapter TVA = new EventsAdapter(leavestatus_list, R.layout.eventslistitem);
                            TVA.notifyDataSetChanged();
                            // mRecyclerView.setAdapter(TVA);
                            videoAdapter = new VideoAdapter(youTubeVideos);

                            ConcatAdapter concatAdapter = new ConcatAdapter(TVA, videoAdapter);
                            mRecyclerView.setAdapter(concatAdapter);


                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                            txtNoData.setVisibility(View.VISIBLE);

                        }


                    } else {
                        mRecyclerView.setVisibility(View.GONE);
                        txtNoData.setVisibility(View.VISIBLE);
                        txtNoData.setText(JSobject.getString("Message"));
                        Toast.makeText(EventDisplayActivity.this, JSobject.getString("Message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    mRecyclerView.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                    txtNoData.setText(ResultString);
                    Toast.makeText(EventDisplayActivity.this, ResultString, Toast.LENGTH_LONG).show();
                }
            }else{
                mRecyclerView.setVisibility(View.GONE);
                txtNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}