package com.amirta.studentinformation;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

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


public class HourWiseAttendance extends AppCompatActivity {
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
        setContentView(R.layout.fragment_hourwiseattendance);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Hour Attendance");
        tvLastUpdated = findViewById(R.id.txtLastUpdated);
        txtNoData = findViewById(R.id.txtNoData);
        Button btnBack= findViewById(R.id.button_back);
        Button btnRefresh= findViewById(R.id.button_refresh);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
       onBackPressed();     }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getHourwiseAttendance";
                if (CheckNetwork.isInternetAvailable(HourWiseAttendance.this)) {
                    AsyncCallWS task =new  AsyncCallWS();
                    task.execute();

                } else {
                    Toast.makeText(HourWiseAttendance.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
                }  }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        displayHourwiseAttendance();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void displayHourwiseAttendance(){
        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,attendancedate,coalesce(h1,'-') as h1,"+
                    " coalesce(h2,'-') as h2,coalesce(h3,'-') as h3,coalesce(h4,'-') as h4,coalesce(h5,'-') as h5," +
                    " coalesce(h6,'-') as h6,coalesce(h7,'-') as h7,coalesce(h8,'-') as h8"+
                    " FROM hourwiseattendance WHERE studentid=" + lngStudentId, null);
            if (cursor.moveToFirst()){
                txtNoData.setVisibility(View.GONE);
                addHeaderData();
                int i = 0;
                do {
                    i++;
                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    String strRow = cursor.getString(cursor.getColumnIndex("attendancedate")) + "##" +
                            cursor.getString(cursor.getColumnIndex("h1"))+"##"+
                            cursor.getString(cursor.getColumnIndex("h2"))+"##"+
                            cursor.getString(cursor.getColumnIndex("h3"))+"##"+
                            cursor.getString(cursor.getColumnIndex("h4"))+"##"+
                            cursor.getString(cursor.getColumnIndex("h5"))+"##"+
                            cursor.getString(cursor.getColumnIndex("h6"))+"##"+
                            cursor.getString(cursor.getColumnIndex("h7"))+"##"+
                            cursor.getString(cursor.getColumnIndex("h8"));
                    String[] strColumns = strRow.split("##");
                    addData(strColumns,i);
                } while (cursor.moveToNext());
                cursor.close();
            } else {
                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getHourwiseAttendance";
                if (CheckNetwork.isInternetAvailable(HourWiseAttendance.this)) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();

                } else {
                    Toast.makeText(HourWiseAttendance.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
                }}

        }catch (Exception e){

            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getHourwiseAttendance";
            if (CheckNetwork.isInternetAvailable(HourWiseAttendance.this)) {
                AsyncCallWS task = new AsyncCallWS();
                task.execute();

            } else {
                Toast.makeText(HourWiseAttendance.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(HourWiseAttendance.this);

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
                String strH1="",strH2="",strH3="",strH4="",strH5="",strH6="",strH7="",strH8="";
                SqlliteController sc = new SqlliteController(HourWiseAttendance.this);
                sc.deleteHourwiseAttendance(lngStudentId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
//                    System.out.println(temp.getJSONObject(i).length());
                    if (!object.has("h1")) strH1="-"; else strH1=object.getString("h1");
                    if (!object.has("h2")) strH2="-"; else strH2=object.getString("h2");
                    if (!object.has("h3")) strH3="-"; else strH3=object.getString("h3");
                    if (!object.has("h4")) strH4="-"; else strH4=object.getString("h4");
                    if (!object.has("h5")) strH5="-"; else strH5=object.getString("h5");
                    if (!object.has("h6")) strH6="-"; else strH6=object.getString("h6");
                    if (!object.has("h7")) strH7="-"; else strH7=object.getString("h7");
                    if (!object.has("h8")) strH8="-"; else strH8=object.getString("h8");
                    sc.insertHourwiseAttendance(lngStudentId,object.getString("attendancedate"),strH1,strH2,strH3,strH4,strH5,strH6,strH7,strH8);
                }
                TableLayout tl = findViewById(R.id.tblViewHourwiseattendance);
                int count = tl.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = tl.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
                displayHourwiseAttendance();
            } catch (Exception e){
                txtNoData.setVisibility(View.VISIBLE);
                if(!strResultMessage.equalsIgnoreCase("")) {
                    txtNoData.setText(strResultMessage);
                }

                Toast.makeText(HourWiseAttendance.this, "Response: " + strResultMessage, Toast.LENGTH_LONG).show();
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }

    public void addHeaderData(){
        String[] str;
        str = new String[]{"Date", "1", "2", "3", "4", "5", "6", "7", "8"};
        TableLayout tl = findViewById(R.id.tblViewHourwiseattendance);
        TableRow tr = new TableRow(HourWiseAttendance.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(2, 0, 0, 2);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        tr.setBackgroundResource(R.color.cardColoro);
        for (int i = 0; i < str.length; i++){
            TextView tv = new TextView(HourWiseAttendance.this);
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

    public void addData(String[] str, int pos) {
        TableLayout tl = findViewById(R.id.tblViewHourwiseattendance);
        TableRow tr = new TableRow(HourWiseAttendance.this);
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
            TextView tv = new TextView(HourWiseAttendance.this);
            tv.setLayoutParams(params1);
            tv.setText(str[i]);
            tv.setPadding(20, 10, 20, 10);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tv.setTextSize(14);
            tr.addView(tv);
        }
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }
}
