package com.amirta.studentinformation;

import android.app.ProgressDialog;
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


public class InternalMarkDetails extends AppCompatActivity {
    private long lngStudentId=0;
    private static String[] strParameters;
    private static String ResultString = "";
    private TextView tvPageTitle, tvLastUpdated,txtNoData,m                                                                                  ;
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);
    private String strResultMessage="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_internalmarkdetails);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Internal Marks");
        tvLastUpdated = findViewById(R.id.txtLastUpdated);
        txtNoData = findViewById(R.id.txtNoData);
        Button btnBack= findViewById(R.id.button_back);
        Button btnRefresh= findViewById(R.id.button_refresh);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
           onBackPressed();
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getInternalMarkDetails";
                if (CheckNetwork.isInternetAvailable(InternalMarkDetails.this)) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();

                } else {
                    Toast.makeText(InternalMarkDetails.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
                }  }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        displayInternalMarkDetails();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void displayInternalMarkDetails(){
        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,* FROM internalmarkdetails WHERE studentid=" + lngStudentId, null);
            if (cursor.moveToFirst()){
                addHeaderData();
                int i = 0;
                do {
                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    String strRow = cursor.getString(cursor.getColumnIndex("subjectcode"))+ "##"+
//                            cursor.getString(cursor.getColumnIndex("subjectdesc"))+ "##"+
                            cursor.getString(cursor.getColumnIndex("markobtained"))+"##"+
                            cursor.getString(cursor.getColumnIndex("maxmarks"));
                    String[] strColumns = strRow.split("##");
                    String strAbbreviation = "<font color='#2e76b2'><b>"+cursor.getString(cursor.getColumnIndex("subjectcode")) + " </b></font> - " + cursor.getString(cursor.getColumnIndex("subjectdesc"));
                    addData(strColumns,i);
                    i++;
                    addAbbrivationData(strAbbreviation);
                } while (cursor.moveToNext());
            } else {
                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getInternalMarkDetails";
                if (CheckNetwork.isInternetAvailable(InternalMarkDetails.this)) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();

                } else {
                    Toast.makeText(InternalMarkDetails.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
                }          }
            cursor.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getInternalMarkDetails";
            if (CheckNetwork.isInternetAvailable(InternalMarkDetails.this)) {
                AsyncCallWS task = new AsyncCallWS();
                task.execute();

            } else {
                Toast.makeText(InternalMarkDetails.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(InternalMarkDetails.this);

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
            txtNoData.setVisibility(View.VISIBLE);
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
                SqlliteController sc = new SqlliteController(InternalMarkDetails.this);
                sc.deleteInternalMarkDetails(lngStudentId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    sc.insertInternalMarkDetails(lngStudentId,object.getString("subjectcode"),object.getString("subjectdesc"),object.getString("sumofmarks"),object.getString("sumofmaxmarks"));
                }
                TableLayout tl = findViewById(R.id.tblViewInternalmarkdetails);
                int count = tl.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = tl.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
                displayInternalMarkDetails();
            } catch (Exception e) {
                Toast.makeText(InternalMarkDetails.this, "Response: " + strResultMessage, Toast.LENGTH_LONG).show();
                txtNoData.setVisibility(View.VISIBLE);
                txtNoData.setText(strResultMessage);

            }
        }
    }

    public void addHeaderData(){
        String[] str;
        str = new String[]{"Code", "Marks Obtained", "Maximum Marks"};
        TableLayout tl = findViewById(R.id.tblViewInternalmarkdetails);
        TableRow tr = new TableRow(InternalMarkDetails.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(2, 0, 0, 2);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        tr.setBackgroundResource(R.color.cardColoro);
        for (int i = 0; i < str.length; i++){
            TextView tv = new TextView(InternalMarkDetails.this);
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
        TableLayout tl = findViewById(R.id.tblViewInternalmarkdetails);
        TableRow tr = new TableRow(InternalMarkDetails.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(10, 10, 10, 10);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        if(pos % 2 == 0) {
            tr.setBackgroundResource(R.color.colorWhite);
        }else{
            tr.setBackgroundResource(R.color.colorGrey);

        }for (int i = 0; i < str.length; i++){
            TextView tv = new TextView(InternalMarkDetails.this);
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

    public void addAbbrivationData(String str){
        TableLayout tl = findViewById(R.id.tbSubjectAbbrevation);
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
