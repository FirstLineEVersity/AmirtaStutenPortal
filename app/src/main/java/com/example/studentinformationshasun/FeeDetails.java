package com.example.studentinformationshasun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import webservice.SqlliteController;
import webservice.WebService;

import static android.content.ContentValues.TAG;
import static webservice.WebService.strParameters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class FeeDetails extends AppCompatActivity {

    private static String ResultString = "";
    private long lngStudentId=0;
    TextView tvPageTitle, tvLastUpdated,txtNoData;
    ExpandableListView lstCT;
    SQLiteDatabase db;
    private final ArrayList<String> due_list = new ArrayList<String>(200);
    private final HashMap<String, List<String>> child_duelist = new HashMap<String,List<String>>();
    SqlliteController controllerdb = new SqlliteController(this);
    private String strResultMessage="";
    FeeDueLVAdapter FDLVA;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_feedetails);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        txtNoData = findViewById(R.id.txtNoData);
        tvPageTitle.setText("Fee Dues");
        tvLastUpdated = findViewById(R.id.txtLastUpdated);
        lstCT = findViewById(R.id.lvStudentFeeDue);
        Button btnBack= findViewById(R.id.button_back);
        Button btnRefresh= findViewById(R.id.button_refresh);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
           onBackPressed(); }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getFeeDetails";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        displayFeedetails();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void displayFeedetails(){
        db = controllerdb.getReadableDatabase();
        try {
            String strFeeDueDetails="";
            //+ " ORDER BY term desc,date(duedate) desc,duename
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,term||'##'||sum(dueamount) as termwisetotal,group_concat(duedate||'##'||duename||'##'||dueamount) as feeduedetals FROM feedetails WHERE studentid=" + lngStudentId + " GROUP BY term ORDER BY term desc", null);

            //, group_concat(duedate||'##'||duename||'##'||dueamount) ASC
            if (cursor.moveToFirst()) {
                do {
                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    due_list.add(cursor.getString(cursor.getColumnIndex("termwisetotal")));
                    StringTokenizer st = new StringTokenizer(cursor.getString(cursor.getColumnIndex("feeduedetals")), ",");
                    List<String> child_listInner = new ArrayList<String>();
                    while (st.hasMoreTokens()){
                        strFeeDueDetails = st.nextToken().trim();
                        child_listInner.add(strFeeDueDetails);
                        child_duelist.put(cursor.getString(cursor.getColumnIndex("termwisetotal")), child_listInner);
                    }
                } while (cursor.moveToNext());
                if (due_list.size() == 0){
                    txtNoData.setVisibility(View.VISIBLE);
                    lstCT.setVisibility(View.GONE);
                    Toast.makeText(FeeDetails.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                } else {
                    txtNoData.setVisibility(View.GONE);
                    lstCT.setVisibility(View.VISIBLE);
                    FDLVA = new FeeDueLVAdapter (FeeDetails.this, due_list, child_duelist);
                    lstCT.setAdapter(FDLVA);
                    int count = FDLVA.getGroupCount();
                    for ( int i = 0; i < count; i++ )
                        lstCT.expandGroup(i);
                }
            } else {
                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getFeeDetails";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
            cursor.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getFeeDetails";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(FeeDetails.this);

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
                SqlliteController sc = new SqlliteController(FeeDetails.this);
                sc.deleteFeeDetails(lngStudentId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    String strRow = object.getString("feedetails");
                    String[] strColumns = strRow.split("##");
                    sc.insertFeeDetails(lngStudentId,strColumns[0],strColumns[1],strColumns[2],strColumns[3],strColumns[4],strColumns[5]);
                }
                due_list.clear();
                child_duelist.clear();
                displayFeedetails();
            } catch (Exception e) {
                txtNoData.setVisibility(View.VISIBLE);
                lstCT.setVisibility(View.GONE);
                txtNoData.setText(strResultMessage);
                Toast.makeText(FeeDetails.this, "Response: " + strResultMessage, Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }
        }
    }

//    public void addHeaderData(){
//        String str[];
//        str = new String[]{"Due Date","Fee Head", "Due Amt."};
//        TableLayout tl = findViewById(R.id.tblViewfeedetails);
//        TableRow tr = new TableRow(FeeDetails.this);
//        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//        params1.setMargins(2, 0, 0, 2);
//        params1.weight = 1;
//        tr.setLayoutParams(params1);
//        tr.setBackgroundResource(R.color.colorGreen);
//        for (int i = 0; i < str.length; i++){
//            TextView tv = new TextView(FeeDetails.this);
//            tv.setLayoutParams(params1);
//            tv.setTextColor(0xFFFFFFFF);
//            tv.setPadding(20, 10, 20, 10);
//            tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//            tv.setTextSize(20);
//            tv.setText(str[i]);
//            tr.addView(tv);
//        }
//        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//    }
//
//    public void addData(String[] str) {
//        TableLayout tl = findViewById(R.id.tblViewfeedetails);
//        TableRow tr = new TableRow(FeeDetails.this);
//        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//        params1.setMargins(2, 0, 0, 2);
//
//        params1.weight = 2;
//        tr.setLayoutParams(params1);
//        tr.setBackgroundResource(R.color.colorGrey);
//        for (int i = 0; i < str.length; i++) {
//            TextView tv = new TextView(FeeDetails.this);
//            tv.setLayoutParams(params1);
//            tv.setText(str[i]);
//            tv.setPadding(20, 10, 20, 10);
//            if (i == 0 || i == 1){
//                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//            } else {
//                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
//            }
//            tv.setTextSize(18);
//            tr.addView(tv);
//        }
//        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//    }
}
