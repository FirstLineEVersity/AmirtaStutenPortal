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

public class LibraryTransaction extends AppCompatActivity {
    private long lngStudentId=0;
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private static String[] strParameters;
    private static String ResultString = "";
    private final ArrayList<String> libtrans_list = new ArrayList<String>(200);
    private TextView tvPageTitle, tvLastUpdated,txtNoData;
    private TextView tvMemberType, tvMemberCode, tvMemberName, tvPolicyName, tvStatus;
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);
    private String strResultMessage="";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_librarytransaction);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Library Transaction");
        tvLastUpdated = findViewById(R.id.txtLastUpdated);
        txtNoData = findViewById(R.id.txtNoData);
        Button btnBack= findViewById(R.id.button_back);
        Button btnRefresh= findViewById(R.id.button_refresh);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
          onBackPressed();  }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getLibraryTransaction";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
       displayLibraryTransaction();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void displayLibraryTransaction(){
        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,* FROM memberdetails WHERE studentid=" + lngStudentId, null);
            if (cursor.moveToFirst()){
                do {
                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    tvMemberType = findViewById(R.id.txtMemberType);
                    tvMemberType.setText(cursor.getString(cursor.getColumnIndex("membertype")));

                    tvMemberCode = findViewById(R.id.txtMemberCode);
                    tvMemberCode.setText(cursor.getString(cursor.getColumnIndex("membercode")));

                    tvMemberName = findViewById(R.id.txtMemberName);
                    tvMemberName.setText(cursor.getString(cursor.getColumnIndex("membername")));

                    tvPolicyName = findViewById(R.id.txtPolicyName);
                    tvPolicyName.setText(cursor.getString(cursor.getColumnIndex("policyname")));

                    tvStatus = findViewById(R.id.txtStatus);
                    tvStatus.setText(cursor.getString(cursor.getColumnIndex("status")));

                } while (cursor.moveToNext());
            } else {
                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getLibraryTransaction";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
            cursor.close();

            cursor = db.rawQuery("SELECT * FROM librarytransaction WHERE studentid=" + lngStudentId, null);
            if (cursor.moveToFirst()){
                do {
                    libtrans_list.add(cursor.getString(cursor.getColumnIndex("issuedate")) + "##" + cursor.getString(cursor.getColumnIndex("accessionno")) + "##" + cursor.getString(cursor.getColumnIndex("title")) + "##" + cursor.getString(cursor.getColumnIndex("duedate"))+ "##" + cursor.getString(cursor.getColumnIndex("returndate"))+ "##" + cursor.getString(cursor.getColumnIndex("status"))+ "##" + cursor.getString(cursor.getColumnIndex("fine")));
                } while (cursor.moveToNext());
            }
            cursor.close();
            if (libtrans_list.size() == 0){

                txtNoData.setVisibility(View.VISIBLE);
                Toast.makeText(LibraryTransaction.this, "Response: No Library Transactions Found", Toast.LENGTH_LONG).show();
            } else {

                txtNoData.setVisibility(View.GONE);
                mRecyclerView = findViewById(R.id.rvLibraryTransaction); // Assigning the RecyclerView Object to the xml View
                mRecyclerView.setHasFixedSize(true);
                // Letting the system know that the list objects are of fixed size
                mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
                mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
                LibraryTransactionLVAdapter TVA = new LibraryTransactionLVAdapter(libtrans_list, R.layout.librarytransactionlistitems);
                mRecyclerView.setAdapter(TVA);

            }
        }catch (Exception e){

            txtNoData.setVisibility(View.VISIBLE);
            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getLibraryTransaction";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(LibraryTransaction.this);

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
            // [{"membercode":"1-19-S2-0132","membertype":"UG STUDENT","policyname":"UG STUDENT POLICY",
            // "membername":"SHIVANI H","status":"Active"},{"accessionno":"13569","returndate":"Not_Returned",
            // "duedate":"11-05-2022","fineamount":"0.00","title":"Client/Server Computing",
            // "issuedate":"26-04-2022","status":"Books in Hand"},
            // {"accessionno":"18333","returndate":"Not_Returned","duedate":"11-05-2022","fineamount":"0.00",
            // "title":"OBJECT ORIENTED ANALYSIS AND DESIGN WITH APPLICATIONS","issuedate":"26-04-2022",
            // "status":"Books in Hand"},{"accessionno":"9011","returndate":
            // "09-04-2022","duedate":"09-04-2022","fineamount":"0.00",
            // "title":"Data Comunications and Networking 4ed","issuedate":"25-03-2022","status":"Returned"},
            // {"accessionno":"13406","returndate":"09-04-2022","duedate":"09-04-2022","fineamount":"0.00",
            // "title":"Web Technology","issuedate":"25-03-2022","status":"Returned"},
            // {"accessionno":"14663","returndate":"31-08-2021","duedate":"31-08-2021",
            // "fineamount":"0.00","title":"Programming In C","issuedate":"18-12-2019","status":"Returned"},
            // {"accessionno":"8654","returndate":"31-08-2021","duedate":"31-08-2021","fineamount":"0.00",
            // "title":"Programming in ANSI C","issuedate":"18-12-2019","status":"Returned"},
            // {"accessionno":"8654","returndate":"18-12-2019","duedate":"11-12-2019","fineamount":"7.00",
            // "title":"Programming in ANSI C","issuedate":"26-11-2019","status":"Returned"},
            // {"accessionno":"14663","returndate":"18-12-2019","duedate":"11-12-2019","fineamount":"7.00",
            // "title":"Programming In C","issuedate":"26-11-2019","status":"Returned"},
            // {"accessionno":"13621","returndate":"22-11-2019","duedate":"05-11-2019","fineamount":"17.00",
            // "title":"Digital Fundamentals","issuedate":"21-10-2019","status":"Returned"},
            // {"accessionno":"12860","returndate":"22-11-2019","duedate":"05-11-2019","fineamount":"17.00",
            // "title":"Spring Board ","issuedate":"21-10-2019","status":"Returned"},
            // {"accessionno":"8511","returndate":"21-10-2019","duedate":"10-09-2019","fineamount":"41.00",
            // "title":"Allied Mathematics","issuedate":"26-08-2019","status":"Returned"},
            // {"accessionno":"13621","returndate":"21-10-2019","duedate":"10-09-2019","fineamount":"41.00",
            // "title":"Digital Fundamentals","issuedate":"26-08-2019","status":"Returned"}]

            Log.i(TAG, ResultString);
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
                SqlliteController sc = new SqlliteController(LibraryTransaction.this);
                sc.deleteMemberDetails(lngStudentId);
                sc.deleteLibraryTransaction(lngStudentId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    if (i==0){
                        sc.insertMemberDetails(lngStudentId,object.getString("membercode"),object.getString("membertype"),object.getString("membername"),object.getString("policyname"),object.getString("status"));
                    }
                    else {
                        sc.insertLibraryTransaction(lngStudentId, object.getString("issuedate"), object.getString("accessionno"), object.getString("title"), object.getString("duedate"), object.getString("returndate"), object.getString("status"), object.getString("fineamount"));
                    }
                }
                displayLibraryTransaction();
            } catch (Exception e) {

                txtNoData.setVisibility(View.VISIBLE);
                txtNoData.setText(strResultMessage);
                Toast.makeText(LibraryTransaction.this, "Response: " + strResultMessage, Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }
        }
    }
}
