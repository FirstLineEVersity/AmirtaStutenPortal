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

public class CummulativAttendance extends AppCompatActivity {
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
        setContentView(R.layout.fragment_cummulativattendance);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Cummulative Attendance");
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
            WebService.METHOD_NAME = "getCummulativeAttendance";
                if (CheckNetwork.isInternetAvailable(CummulativAttendance.this)) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();
                } else {
                    Toast.makeText(CummulativAttendance.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        displayCummulativeAttendance();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void displayCummulativeAttendance(){
        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,* FROM cummulativeattendance WHERE studentid=" + lngStudentId, null);
            if (cursor.moveToFirst()){
                txtNoData.setVisibility(View.GONE);
int i = 0;
                addHeaderData();
                do {

                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    String strRow = cursor.getString(cursor.getColumnIndex("attendancemonthyear")) + "##" +
                            cursor.getString(cursor.getColumnIndex("presenthrs"))+"##"+
                            cursor.getString(cursor.getColumnIndex("absenthrs"))+"##"+
                            cursor.getString(cursor.getColumnIndex("odpresent"))+"##"+
                            cursor.getString(cursor.getColumnIndex("odabsent"))+"##"+
                            cursor.getString(cursor.getColumnIndex("medical"));
                    String[] strColumns = strRow.split("##");
                    addData(strColumns,i);
                    i++;
                } while (cursor.moveToNext());
                addLegend();
//                addLegendAbbreviation();
            } else {
      strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getCummulativeAttendance";
                if (CheckNetwork.isInternetAvailable(CummulativAttendance.this)) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();
                } else {
                    Toast.makeText(CummulativAttendance.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
                }
            }
            cursor.close();
        }catch (Exception e){

            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getCummulativeAttendance";
            if (CheckNetwork.isInternetAvailable(CummulativAttendance.this)) {
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            } else {
                Toast.makeText(CummulativAttendance.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
            }        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(CummulativAttendance.this);

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
        protected void onPostExecute(Void result){
            try{
                JSONObject jsonObject = new JSONObject(ResultString);
                if (jsonObject.getString("Status").equals("Error"))
                    strResultMessage = jsonObject.getString("Message");
            }
            catch (Exception e){}
            try {
                JSONArray temp = new JSONArray(ResultString);
                SqlliteController sc = new SqlliteController(CummulativAttendance.this);
                sc.deleteCummulativeAttendance(lngStudentId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    sc.insertCummulativeAttendance(lngStudentId,object.getString("attendancemonthyear"),object.getString("present"),object.getString("absent"),object.getString("odpresent"),object.getString("odabsent"),object.getString("medical"));
                }
                TableLayout tl = findViewById(R.id.tblViewcummulativeattendance);
                int count = tl.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = tl.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
                displayCummulativeAttendance();
            } catch (Exception e) {
                txtNoData.setVisibility(View.VISIBLE);
                txtNoData.setText(strResultMessage);

                Toast.makeText(CummulativAttendance.this, "Response: " + strResultMessage, Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }

    public void addHeaderData(){
        String[] str;
        str = new String[]{"Month/Year", "Pre.", "Abs.", "ODP", "ODA", "Med."};
        TableLayout tl = findViewById(R.id.tblViewcummulativeattendance);
        TableRow tr = new TableRow(CummulativAttendance.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(2, 0, 0, 2);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        tr.setBackgroundResource(R.color.cardColoro);
        for (int i = 0; i < str.length; i++){
            TextView tv = new TextView(CummulativAttendance.this);
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
        TableLayout tl = findViewById(R.id.tblViewcummulativeattendance);
        TableRow tr = new TableRow(CummulativAttendance.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(10, 10, 10, 10);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        if(pos % 2 == 0) {
            tr.setBackgroundResource(R.color.colorWhite);
        }else{
            tr.setBackgroundResource(R.color.colorGrey);

        }   for (int i = 0; i < str.length; i++) {
            TextView tv = new TextView(CummulativAttendance.this);
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

    public void addLegend() {
        String[] str = new String[]{"LEGEND","Pre - Present", "Abs - Absent", "ODP - OD Present", "ODA - OD Absent","Med - Medical"};
        TableLayout tl = findViewById(R.id.tblViewcummulativeattendance);
        String strhtml="";
        for (int i=0; i<str.length; i++){
            String sourceSting = str[i];
            if(i==0)
                strhtml= "<br><font color='#2e76b2'><b><u>" + str[0] + "</u></b></font><br><br>";
            else {
                String[] strSplit =str[i].split("-");
                strhtml= strhtml +"<b>" + strSplit[0] + "</b>" + " - " + strSplit[1] + "<br><br>";
            }
        }
        TableRow tr = new TableRow(CummulativAttendance.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(2, 0, 0, 2);
        params1.weight = 1;
        params1.span=5;
        tr.setLayoutParams(params1);
        tr.setBackgroundResource(R.color.colornormal);
        TextView tv = new TextView(CummulativAttendance.this);
        tv.setLayoutParams(params1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            tv.setText(Html.fromHtml(strhtml,Html.FROM_HTML_MODE_LEGACY));
        }
        else {
            tv.setText(Html.fromHtml(strhtml));
        }
        tv.setPadding(20, 10, 20, 10);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tv.setTextSize(14);
        tr.addView(tv);
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }

//    public void addLegendAbbreviation(){
//        String str[];
//        str = new String[]{"Pre - Present", "Abs - Absent", "ODP - OD Present", "ODA - OD Absent","Med - Medical"};
//        TableLayout tl = findViewById(R.id.tblLegendabbrevation);
//        TableRow tr = new TableRow(this);
//        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
////        TextView tv = new TextView(this);
////        tv.setLayoutParams(params1);
//        for (int i = 0; i < str.length; i++) {
//            TextView tv = new TextView(CummulativAttendance.this);
//            tv.setLayoutParams(params1);
//            tv.setText(str[i]);
//            tv.setPadding(20, 10, 20, 10);
//            tv.setTextSize(16);
//            tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//            tr.addView(tv);
//        }
//        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//    }
}
