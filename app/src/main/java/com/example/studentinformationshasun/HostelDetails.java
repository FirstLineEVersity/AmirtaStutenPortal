package com.example.studentinformationshasun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import webservice.SqlliteController;
import webservice.WebService;

import static android.content.ContentValues.TAG;

public class HostelDetails extends AppCompatActivity {
    private long lngStudentId=0;
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private static String[] strParameters;
    private static String ResultString = "";
    private final ArrayList<String> hostel_list = new ArrayList<String>(200);
    private TextView tvPageTitle, tvLastUpdated;
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);
    private String strResultMessage="";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_hosteldetails);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Hostel Details");
        tvLastUpdated = findViewById(R.id.txtLastUpdated);
        Button btnBack= findViewById(R.id.button_back);
        Button btnRefresh= findViewById(R.id.button_refresh);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            onBackPressed();}
        });
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getHostelDetails";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        displayHostelDetails();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void displayHostelDetails(){
        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,* FROM hosteldetails WHERE studentid=" + lngStudentId, null);
            if (cursor.moveToFirst()){
                do {
                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    hostel_list.add(cursor.getString(cursor.getColumnIndex("academicyear")) + "##" + cursor.getString(cursor.getColumnIndex("alloteddate")) + "##" + cursor.getString(cursor.getColumnIndex("hostelname")) + "##" + cursor.getString(cursor.getColumnIndex("roomname"))+ "##" + cursor.getString(cursor.getColumnIndex("roomtype")));
                } while (cursor.moveToNext());
            } else {
                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getHostelDetails";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
            cursor.close();
            if (hostel_list.size() == 0) {
                Toast.makeText(HostelDetails.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
            } else {
                mRecyclerView = findViewById(R.id.rvHostelDetails); // Assigning the RecyclerView Object to the xml View
                mRecyclerView.setHasFixedSize(true);
                // Letting the system know that the list objects are of fixed size
                HostelDetailsLVAdapter TVA = new HostelDetailsLVAdapter(hostel_list, R.layout.hosteldetailslistitems);
                mRecyclerView.setAdapter(TVA);
                mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
                mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getHostelDetails";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(HostelDetails.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading......");
            //show dialog
            dialog.show();
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Log.i(TAG, "onPostExecute");
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            try{
                JSONObject jsonObject = new JSONObject(ResultString);
                if (jsonObject.getString("Status").equals("Error"))
                    strResultMessage = jsonObject.getString("Message");
            }
            catch (Exception e){}
            try {
                JSONArray temp = new JSONArray(ResultString);
                SqlliteController sc = new SqlliteController(HostelDetails.this);
                sc.deleteHostelDetails(lngStudentId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    sc.insertHostelDetails(lngStudentId, object.getString("academicyear"), object.getString("alloteddate"), object.getString("hostelname"), object.getString("roomname"), object.getString("roomtype"));
                }
                displayHostelDetails();
            } catch (Exception e) {
                Toast.makeText(HostelDetails.this, "Response:"+ strResultMessage, Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }
        }
    }
}
