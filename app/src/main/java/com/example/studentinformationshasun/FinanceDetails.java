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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class FinanceDetails extends AppCompatActivity {
    private long lngStudentId=0;
    private static String[] strParameters;
    private static String ResultString = "";
    private TextView tvPageTitle, tvLastUpdated,txtNoData;
    SQLiteDatabase db;
    private final ArrayList<String> paid_list = new ArrayList<String>(200);
    private final HashMap<String, List<String>> child_paidlist = new HashMap<String,List<String>>();
    SqlliteController controllerdb = new SqlliteController(this);
    private String strResultMessage="";
    ExpandableListView lstFD;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_financedetails);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Fee Paid");
        tvLastUpdated = findViewById(R.id.txtLastUpdated);
        txtNoData = findViewById(R.id.txtNoData);
         lstFD = findViewById(R.id.lvStudentFeePaid);

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
                WebService.METHOD_NAME = "getFinanceDetails";
                if (CheckNetwork.isInternetAvailable(FinanceDetails.this)) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();

                } else {
                    Toast.makeText(FinanceDetails.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        displayFinanceDetails();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
       }

    private void displayFinanceDetails(){
        db = controllerdb.getReadableDatabase();
        try {
            String strFeePaidDetails="";
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,term||'##'||sum(amountcollected) as termwisetotal,group_concat(receiptdate||'##'||duename||'##'||amountcollected||'##'||mode) as feepaiddetals FROM financedetails WHERE studentid=" + lngStudentId + " GROUP BY term ORDER BY term desc", null);
//, group_concat(receiptdate||'##'||duename||'##'||amountcollected||'##'||mode) ASC
            if (cursor.moveToFirst()) {
                do {
                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    paid_list.add(cursor.getString(cursor.getColumnIndex("termwisetotal")));
                    StringTokenizer st = new StringTokenizer(cursor.getString(cursor.getColumnIndex("feepaiddetals")), ",");
                    List<String> child_listInner = new ArrayList<String>();
                    while (st.hasMoreTokens()){
                        strFeePaidDetails = st.nextToken().trim();
                        child_listInner.add(strFeePaidDetails);
                        child_paidlist.put(cursor.getString(cursor.getColumnIndex("termwisetotal")), child_listInner);
                    }
                } while (cursor.moveToNext());
                if (paid_list.size() == 0) {
                    txtNoData.setVisibility(View.VISIBLE);
                    lstFD.setVisibility(View.GONE);
                    Toast.makeText(FinanceDetails.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                } else {
                    txtNoData.setVisibility(View.GONE);
                    lstFD.setVisibility(View.VISIBLE);
                    FinanceLVAdapter TVA = new FinanceLVAdapter(FinanceDetails.this, paid_list,child_paidlist);
                    lstFD.setAdapter(TVA);
                }
            } else {
                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getFinanceDetails";
                if (CheckNetwork.isInternetAvailable(FinanceDetails.this)) {
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();

                } else {
                    Toast.makeText(FinanceDetails.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
                }}
            cursor.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getFinanceDetails";
            if (CheckNetwork.isInternetAvailable(FinanceDetails.this)) {
                AsyncCallWS task = new AsyncCallWS();
                task.execute();

            } else {
                Toast.makeText(FinanceDetails.this, "You dont have Internet connection", Toast.LENGTH_LONG).show();
            }}
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(FinanceDetails.this);

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
                SqlliteController sc = new SqlliteController(FinanceDetails.this);
                sc.deleteFinanceDetails(lngStudentId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    sc.insertFinanceDetails(lngStudentId, object.getString("term"), object.getString("duename"), object.getString("dueamount"), object.getString("duedate"), object.getString("receiptdate"), object.getString("amountcollected"), object.getString("modeoftransaction"), object.getString("receiptnum"));
                }
                final ExpandableListView lstCT = findViewById(R.id.lvStudentFeePaid);
                paid_list.clear();
                child_paidlist.clear();
                displayFinanceDetails();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                txtNoData.setVisibility(View.VISIBLE);
                lstFD.setVisibility(View.GONE);
txtNoData.setText(strResultMessage);
                Toast.makeText(FinanceDetails.this, "Response: " + strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

//    public void addHeaderData(){
//        String str[];
//        str = new String[]{"Term", "Due Name", " Due Date", "Due Amount", "Receipt Date", "Amount Collected", "Rec. Mode"};
//        TableLayout tl = findViewById(R.id.tblViewFinanceDetails);
//        TableRow tr = new TableRow(FinanceDetails.this);
//        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//        params1.setMargins(2, 0, 0, 2);
//        params1.weight = 1;
//        tr.setLayoutParams(params1);
//        tr.setBackgroundResource(R.color.colorhower);
//        for (int i = 0; i < str.length; i++){
//            TextView tv = new TextView(FinanceDetails.this);
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
//        TableLayout tl = findViewById(R.id.tblViewFinanceDetails);
//        TableRow tr = new TableRow(FinanceDetails.this);
//        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//        params1.setMargins(2, 0, 0, 2);
//        params1.weight = 1;
//        tr.setLayoutParams(params1);
//        tr.setBackgroundResource(R.color.colornormal);
//        for (int i = 0; i < str.length; i++) {
//            TextView tv = new TextView(FinanceDetails.this);
//            tv.setLayoutParams(params1);
//            tv.setText(str[i]);
//            tv.setPadding(20, 10, 20, 10);
//            if (i == 3 || i == 5){
//                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
//            }else{
//                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//            }
//            tv.setTextSize(18);
//            tr.addView(tv);
//        }
//        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//    }
}
