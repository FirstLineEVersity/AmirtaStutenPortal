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
import static webservice.WebService.strParameters;

public class NotificationView extends AppCompatActivity {
    private static String ResultString = "";
    private long lngStudentId=0;
    TextView tvPageTitle, tvLastUpdated,txtNoData;
    SQLiteDatabase db;
    private final ArrayList<String> notification_list = new ArrayList<String>(200);
    private final HashMap<String, List<String>> child_notificationlist = new HashMap<String,List<String>>();
    SqlliteController controllerdb = new SqlliteController(this);
    private String strResultMessage="";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notificationview);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Notification List");
        tvLastUpdated = findViewById(R.id.txtLastUpdated);
        txtNoData = findViewById(R.id.txtNoData);
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
                WebService.METHOD_NAME = "getViewNotificationListJson";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        displayNotificationDetails();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void displayNotificationDetails(){
        db = controllerdb.getReadableDatabase();
        try {
            String strNotificationDetails="";
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated," +
                    " notificationdate as groupcolumn," +
                    " group_concat(sendingemployee||'##'||notificationtime||'##'||designation||'##'||department||'##'||notificationmessage) as notificationdetails " +
                    " FROM notificationdetails WHERE studentid=" + lngStudentId + " GROUP BY notificationdate ORDER BY notificationdate desc", null);
            if (cursor.moveToFirst()){

                txtNoData.setVisibility(View.GONE);
                do {
                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    notification_list.add(cursor.getString(cursor.getColumnIndex("groupcolumn")));
                    StringTokenizer st = new StringTokenizer(cursor.getString(cursor.getColumnIndex("notificationdetails")), ",");
                    List<String> child_listInner = new ArrayList<String>();
                    while (st.hasMoreTokens()){
                        strNotificationDetails = st.nextToken().trim();
                        child_listInner.add(strNotificationDetails);
                        child_notificationlist.put(cursor.getString(cursor.getColumnIndex("groupcolumn")), child_listInner);
                    }
                } while (cursor.moveToNext());
                if (notification_list.size() == 0){
                    Toast.makeText(NotificationView.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                } else {
                    ExpandableListView lstCT = findViewById(R.id.lvStudentNotification);
                    NotificationViewLVAdapter NVLVA = new NotificationViewLVAdapter (NotificationView.this, notification_list, child_notificationlist);
                    lstCT.setAdapter(NVLVA);
//                    int count = NVLVA.getGroupCount();
//                    for ( int i = 0; i < count; i++ )
//                        lstCT.expandGroup(i);
                }
            } else {

                strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getViewNotificationListJson";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
            cursor.close();
        }catch (Exception e){

            strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getViewNotificationListJson";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(NotificationView.this);

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
                SqlliteController sc = new SqlliteController(NotificationView.this);
                sc.deleteNotificationDetails(lngStudentId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    sc.insertNotificationDetails(lngStudentId,object.getString("notificationdate") ,
                            object.getString("notificationtime"),object.getString("notificationtitle"),
                            object.getString("message") ,object.getString("sendingemployee"),
                            object.getString("division"),object.getString("designation"));
                }
                notification_list.clear();
                child_notificationlist.clear();
                displayNotificationDetails();
            } catch (Exception e) {
                txtNoData.setVisibility(View.VISIBLE);
                txtNoData.setText(strResultMessage);
                Toast.makeText(NotificationView.this, "Response: " + strResultMessage, Toast.LENGTH_LONG).show();
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }
}
