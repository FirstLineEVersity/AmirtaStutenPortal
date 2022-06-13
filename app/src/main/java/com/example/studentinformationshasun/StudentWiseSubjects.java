package com.example.studentinformationshasun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import webservice.SqlliteController;
import webservice.WebService;

import static android.content.ContentValues.TAG;

public class StudentWiseSubjects extends AppCompatActivity {
    private long lngStudentId = 0;
    private static String[] strParameters;
    private static String ResultString = "";
    private TextView tvPageTitle, tvLastUpdated,txtNoData;
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);
    private String strResultMessage = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_studentwisesubject);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Subjects Registered");
        tvLastUpdated = findViewById(R.id.txtLastUpdated);
        txtNoData = findViewById(R.id.txtNoData);
        Button btnBack = findViewById(R.id.button_back);
        Button btnRefresh = findViewById(R.id.button_refresh);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getSubjects";
                if (CheckNetwork.isInternetAvailable(StudentWiseSubjects.this)) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();

                } else {
                    Toast.makeText(StudentWiseSubjects.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        displaySubjects();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void displaySubjects() {
        db = controllerdb.getReadableDatabase();
        try {
            int i = 0;
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,* FROM studentsubjects WHERE studentid=" + lngStudentId, null);
            if (cursor.moveToFirst()) {
                txtNoData.setVisibility(View.GONE);

                addHeaderData();
                do {
                    i++;
                    tvLastUpdated.setText("Last Updated: " + cursor.getString(cursor.getColumnIndex("lastupdated")));
                    String strRow = cursor.getString(cursor.getColumnIndex("semester")) + "##" + cursor.getString(cursor.getColumnIndex("subjectcode")) + "##" + cursor.getString(cursor.getColumnIndex("subjectdesc")) + "##" + cursor.getString(cursor.getColumnIndex("credit"));
//                    String strAbbreviation = cursor.getString(cursor.getColumnIndex("subjectcode")) + " - " + cursor.getString(cursor.getColumnIndex("subjectdesc"));
                    String[] strColumns = strRow.split("##");
                    addData(strColumns,i);
//                    addAbbrivationData(strAbbreviation);
                } while (cursor.moveToNext());
            } else {
                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getSubjects";
                if (CheckNetwork.isInternetAvailable(StudentWiseSubjects.this)) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();

                } else {
                    Toast.makeText(StudentWiseSubjects.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
                } }
            cursor.close();
        } catch (Exception e) {

            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getSubjects";
            if (CheckNetwork.isInternetAvailable(StudentWiseSubjects.this)) {
                AsyncCallWS task = new AsyncCallWS();
                task.execute();

            } else {
                Toast.makeText(StudentWiseSubjects.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
            }}
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(StudentWiseSubjects.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading......");
            //show dialog
            dialog.show();
            }

        @Override
        protected Void doInBackground(Void... params) {
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                JSONObject jsonObject = new JSONObject(ResultString);
                if (jsonObject.getString("Status").equals("Error"))
                    strResultMessage = jsonObject.getString("Message");
            } catch (Exception e) {
            }
            try {
                JSONArray temp = new JSONArray(ResultString);
                SqlliteController sc = new SqlliteController(StudentWiseSubjects.this);
                sc.deleteStudentSubjects(lngStudentId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    String strRow = object.getString("subjectdetails");
                    String[] strColumns = strRow.split("##");
                    sc.insertStudentSubjects(lngStudentId, strColumns[0], strColumns[1], strColumns[2], strColumns[3]);
                }
                TableLayout tl = findViewById(R.id.tblViewSubjects);
                int count = tl.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = tl.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
                displaySubjects();
            } catch (Exception e) {
                txtNoData.setVisibility(View.VISIBLE);
txtNoData.setText(strResultMessage);
                Toast.makeText(StudentWiseSubjects.this, "Response: " + strResultMessage, Toast.LENGTH_LONG).show();
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }

    public void addHeaderData() {
        String[] str;
        str = new String[]{"Sem", "Code", "Sub. Desc", "Credit"};
        TableLayout tl = findViewById(R.id.tblViewSubjects);
        TableRow tr = new TableRow(StudentWiseSubjects.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(10, 0, 0, 10);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        tr.setBackgroundResource(R.color.cardColoro);
        for (int i = 0; i < str.length; i++) {
            TextView tv = new TextView(StudentWiseSubjects.this);
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
        TableLayout tl = findViewById(R.id.tblViewSubjects);
        TableRow tr = new TableRow(StudentWiseSubjects.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(10, 10, 10, 10);
        params1.weight = 1;

        tr.setLayoutParams(params1);
        if(pos % 2 == 0){
            tr.setBackgroundResource(R.color.colorGrey);
        }else{
            tr.setBackgroundResource(R.color.colorWhite);
        }
        for (int i = 0; i < str.length; i++) {
            TextView tv = new TextView(StudentWiseSubjects.this);
            tv.setLayoutParams(params1);
            tv.setText(str[i]);
            tv.setPadding(20, 10, 20, 20);
            tv.setTextSize(14);
            if (i == 3) {
                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            }
            tr.addView(tv);
        }
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }
//    public void addAbbrivationData(String str){
//        TableLayout tl = findViewById(R.id.abbrevation);
//        TableRow tr = new TableRow(this);
//        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//        TextView tv = new TextView(this);
//        tv.setLayoutParams(params1);
//        tv.setText(str);
//        tv.setPadding(20,10,20,10);
//        tv.setTextSize(16);
//        tr.addView(tv);
//        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//    }
}
