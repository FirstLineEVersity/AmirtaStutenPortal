package com.shasun.studentinformationshasun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import webservice.SqlliteController;
import webservice.WebService;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ExamDetails extends AppCompatActivity {
    private long lngStudentId=0;
    private static String[] strParameters;
    private static String ResultString = "";
    private TextView tvPageTitle, tvLastUpdated,txtNoData;
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);
    private String strResultMessage="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_examdetails);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Exam Details");
        tvLastUpdated = findViewById(R.id.txtLastUpdated);
        txtNoData = findViewById(R.id.txtNoData);
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
            WebService.METHOD_NAME = "getExamDetails";
                if (CheckNetwork.isInternetAvailable(ExamDetails.this)) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();

                } else {
                    Toast.makeText(ExamDetails.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        displayExamDetails();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void displayExamDetails(){
        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,* FROM examdetails WHERE studentid=" + lngStudentId + " ORDER BY CAST(semester AS int),monthyear", null);
            if (cursor.moveToFirst()){
                txtNoData.setVisibility(View.GONE);

                addHeaderData();
                int i =0 ;
                do {
                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    String strRow = cursor.getString(cursor.getColumnIndex("semester"))+ "##"+
                            cursor.getString(cursor.getColumnIndex("monthyear"))+ "##"+
                            cursor.getString(cursor.getColumnIndex("subjectcode"))+ "##"+
                            cursor.getString(cursor.getColumnIndex("result"));

                    String[] strColumns = strRow.split("##");
                    String strAbbreviation = "<font color='#2e76b2'><b>"+cursor.getString(cursor.getColumnIndex("subjectcode")) + " </b></font> - " + cursor.getString(cursor.getColumnIndex("subjectdesc"));
                    addData(strColumns,i);
                    addAbbrivationData(strAbbreviation);
                    i++;
                } while (cursor.moveToNext());
            } else {
                txtNoData.setVisibility(View.VISIBLE);
                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getExamDetails";
                if (CheckNetwork.isInternetAvailable(ExamDetails.this)) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();

                } else {
                    Toast.makeText(ExamDetails.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
                }}
            cursor.close();
        }catch (Exception e){
            txtNoData.setVisibility(View.VISIBLE);

            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getExamDetails";
            if (CheckNetwork.isInternetAvailable(ExamDetails.this)) {
                AsyncCallWS task = new AsyncCallWS();
                task.execute();

            } else {
                Toast.makeText(ExamDetails.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
            }}
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(ExamDetails.this);

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
            if (dialog != null && dialog.isShowing()){
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
                SqlliteController sc = new SqlliteController(ExamDetails.this);
                sc.deleteExamDetails(lngStudentId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    sc.insertExamDetails(lngStudentId,object.getString("semester"),object.getString("monthyear"),
                            object.getString("subjectcode"),object.getString("subjectdesc"),
                            object.getString("marksobtained"),object.getString("internal"),
                            object.getString("external"),object.getString("credit"),
                            object.getString("grade"),object.getString("result"),object.getString("attempts"));
                }
                TableLayout tl = findViewById(R.id.tblViewExamdetails);
                int count = tl.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = tl.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
                displayExamDetails();
            } catch (Exception e) {
                txtNoData.setVisibility(View.VISIBLE);
                txtNoData.setText(strResultMessage);
                System.out.println(e.getMessage());
                Toast.makeText(ExamDetails.this, "Response: " + strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addHeaderData(){
        String[] str;
        str = new String[]{"Sem." , "Month/Year", "Code", "Result"};
        TableLayout tl = findViewById(R.id.tblViewExamdetails);
        TableRow tr = new TableRow(ExamDetails.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(2, 0, 0, 2);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        tr.setBackgroundResource(R.color.cardColoro);
        for (int i = 0; i < str.length; i++){
            TextView tv = new TextView(ExamDetails.this);
            tv.setLayoutParams(params1);
            tv.setTextColor(0xFFFFFFFF);
            tv.setPadding(20, 10, 20, 10);
            tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tv.setTextSize(16);
            tv.setText(str[i]);
            tr.addView(tv);
        }
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }

    public void addData(String[] str,int pos) {
        TableLayout tl = findViewById(R.id.tblViewExamdetails);
        TableRow tr = new TableRow(ExamDetails.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(10, 20, 10, 20);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        if(pos % 2 == 0) {
            tr.setBackgroundResource(R.color.colorWhite);
        }else{
            tr.setBackgroundResource(R.color.colorGrey);

        }
        for (int i = 0; i < str.length; i++) {
            TextView tv = new TextView(ExamDetails.this);
            tv.setLayoutParams(params1);
            tv.setText(str[i]);
            tv.setPadding(20, 10, 20, 10);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tv.setTextSize(14);
            tr.addView(tv);
        }
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }

    public void addAbbrivationData(String str){
        TableLayout tl = findViewById(R.id.tbExamDetailsSubjectAbbrevation);
        TableRow tr = new TableRow(this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(this);
        tv.setLayoutParams(params1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            tv.setText(Html.fromHtml(str,Html.FROM_HTML_MODE_LEGACY));
        }
        else{
            tv.setText(Html.fromHtml(str));
        }
        tv.setPadding(20,10,20,10);
        tv.setTextSize(14);
        tr.addView(tv);
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }
}
