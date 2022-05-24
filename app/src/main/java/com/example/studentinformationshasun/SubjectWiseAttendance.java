package com.example.studentinformationshasun;

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
import android.view.Gravity;
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

public class SubjectWiseAttendance extends AppCompatActivity {
    private long lngStudentId=0;
    private static String[] strParameters;
    private static String ResultString = "";
    private TextView tvPageTitle, tvLastUpdated,txtNoData;
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);
    private String strResultMessage="";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_attendancedetails);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Attendance");

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
            WebService.METHOD_NAME = "getSubjectwiseAttendance";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        displaySubjectAttendance();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void displaySubjectAttendance(){
        db = controllerdb.getReadableDatabase();
        String strSubjectAbbrevation="SUBJECT LEGEND,";
        try {
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,* FROM subjectattendance WHERE studentid=" + lngStudentId, null);
            if (cursor.moveToFirst()){
                txtNoData.setVisibility(View.GONE);
                addHeaderData();
                do {
                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    String strRow = cursor.getString(cursor.getColumnIndex("subjectcode")) + "##" + cursor.getString(cursor.getColumnIndex("presenthrs"))+"##"+cursor.getString(cursor.getColumnIndex("absenthrs"))+"##"+cursor.getString(cursor.getColumnIndex("totalhrs"))+"##"+cursor.getString(cursor.getColumnIndex("presentpercentage"));
                    String strAbbreviation = "<font color='#2e76b2'><b>"+cursor.getString(cursor.getColumnIndex("subjectcode"))  + " </b></font> - " + cursor.getString(cursor.getColumnIndex("subjectdesc"));
                    String[] strColumns = strRow.split("##");
                    addData(strColumns);
                    addAbbrivationData(strAbbreviation);
                    //strSubjectAbbrevation=strSubjectAbbrevation + strAbbreviation+",";
                } while (cursor.moveToNext());
                  addLegend();
            } else {
                txtNoData.setVisibility(View.VISIBLE);

                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getSubjectwiseAttendance";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
            cursor.close();
        }catch (Exception e){
            txtNoData.setVisibility(View.VISIBLE);

            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getSubjectwiseAttendance";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(SubjectWiseAttendance.this);

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
                SqlliteController sc = new SqlliteController(SubjectWiseAttendance.this);
                sc.deleteSubjectAttendance(lngStudentId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    sc.insertSubjectAttendance(lngStudentId,object.getString("subjectcode"),object.getString("subjectdesc"),object.getString("present"),object.getString("absent"),object.getString("total"),object.getString("presentpercentage"));
                }
                TableLayout tl = findViewById(R.id.tblViewsubjectattendance);
                TableLayout t2 = findViewById(R.id.abbrevation);
                int count = tl.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = tl.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
                count = t2.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = t2.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
                displaySubjectAttendance();
            } catch (Exception e) {
                txtNoData.setVisibility(View.VISIBLE);
                txtNoData.setText(strResultMessage);
                    Toast.makeText(SubjectWiseAttendance.this, "Response: "+strResultMessage, Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }
        }
    }

    public void addHeaderData(){
        String[] str;
        str = new String[]{"CODE", "TH", "PH", "AH", "%"};
        TableLayout tl = findViewById(R.id.tblViewsubjectattendance);
        TableRow tr = new TableRow(SubjectWiseAttendance.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(2, 0, 0, 2);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        tr.setBackgroundResource(R.color.cardColoro);
        for (int i = 0; i < str.length; i++){
            TextView tv = new TextView(SubjectWiseAttendance.this);
            tv.setLayoutParams(params1);
            tv.setTextColor(0xFFFFFFFF);
            tv.setPadding(20, 10, 20, 10);
            tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(16);
            tv.setText(str[i]);
            tr.addView(tv);
        }
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }

    public void addData(String[] str) {
        TableLayout tl = findViewById(R.id.tblViewsubjectattendance);
        TableRow tr = new TableRow(SubjectWiseAttendance.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(10, 0, 0, 10);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        tr.setBackgroundResource(R.color.cardColorp);
        for (int i = 0; i < str.length; i++) {
            TextView tv = new TextView(SubjectWiseAttendance.this);
            tv.setLayoutParams(params1);
            tv.setText(str[i]);
            tv.setPadding(20, 10, 20, 10);
            if (i == 0){
                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            }else{
                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            }
            tv.setTextSize(14);
            tr.addView(tv);
        }
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }

    public void addLegend(){
        String[] str = new String[]{"LEGEND","TH - Total Hours", "PH - Present Hours", "AH - Absent Hours"};
        TableLayout tl = findViewById(R.id.tblViewsubjectattendance);
        String strhtml="";
        for (int i=0; i<str.length; i++){
            String sourceSting = str[i];
            if(i==0)
//                strhtml= "<br><b>" + str[0] + "</b><br><br>";
                strhtml= "<br><font color='#2e76b2'><b><u>" + str[0] + "</u></b></font><br><br>";
            else{
                String[] strSplit =str[i].split("-");
                if(i==(str.length-1))
                    strhtml= strhtml +"<b>" + strSplit[0] + "</b>" + " - " + strSplit[1]+ "<br>";
                else
                    strhtml= strhtml +"<b>" + strSplit[0] + "</b>" + " - " + strSplit[1] + "<br><br>";
            }
        }

        TableRow tr = new TableRow(SubjectWiseAttendance.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(2, 0, 0, 2);
        params1.weight = 1;
        params1.span=5;
        tr.setLayoutParams(params1);
        tr.setBackgroundResource(R.color.colornormal);
        TextView tv = new TextView(SubjectWiseAttendance.this);
        tv.setLayoutParams(params1);
        tv.setText(Html.fromHtml(strhtml));
        tv.setPadding(20, 10, 20, 10);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tv.setTextSize(14);
        tr.addView(tv);
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }

//    public void addSubjectLegend(String strSubjectLegend) {
//
//        String str[]= strSubjectLegend.split(",");
//
//        TableLayout tl = findViewById(R.id.tblViewsubjectattendance);
//        String strhtml="";
//
//        for (int i=0; i<str.length; i++)
//        {
//            String sourceSting = str[i];
//            if(i==0)
////                strhtml= "<br><b>" + str[0] + "</b><br><br>";
//                strhtml= "<br><font color='#2e76b2'><b><u>" + str[0] + "</u></b></font><br><br>";
//            else
//            {
//                String strSplit[]=str[i].split("-");
//                strhtml= strhtml +"<b>" + strSplit[0] + "</b>" + " - " + strSplit[1] + "<br><br>";
//            }
//        }
//
//        TableRow tr = new TableRow(SubjectWiseAttendance.this);
//        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//        params1.setMargins(2, 0, 0, 2);
//        params1.weight = 1;
//        params1.span=5;
//        tr.setLayoutParams(params1);
//        tr.setBackgroundResource(R.color.colornormal);
//        TextView tv = new TextView(SubjectWiseAttendance.this);
//        tv.setLayoutParams(params1);
//        tv.setText(Html.fromHtml(strhtml));
//        tv.setPadding(20, 10, 20, 10);
//        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//        tv.setTextSize(16);
//        tr.addView(tv);
//        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//    }
//    public void addLegendAbbreviation(){
//        String str[];
//        str = new String[]{"TH - Total Hours", "PH - Present Hours", "AH - Absent Hours"};
//
//        TableLayout tl = findViewById(R.id.tblLegendabbrevation);
//        TableRow tr = new TableRow(this);
//        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
////        TextView tv = new TextView(this);
////        tv.setLayoutParams(params1);
//        for (int i = 0; i < str.length; i++) {
//            TextView tv = new TextView(SubjectWiseAttendance.this);
//            tv.setLayoutParams(params1);
//            tv.setText(str[i]);
//            tv.setPadding(20, 10, 20, 10);
//            tv.setTextSize(16);
//            tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//            tr.addView(tv);
//        }
//        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//    }

    public void addAbbrivationData(String str){
        TableLayout tl = findViewById(R.id.abbrevation);
        TableRow tr = new TableRow(this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
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
