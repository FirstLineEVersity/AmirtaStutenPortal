package com.shasun.studentinformationshasun;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import webservice.WebService;
import webservice.SqlliteController;
import static android.content.ContentValues.TAG;

import com.shasun.studentinformationshasun.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivityOld extends AppCompatActivity implements View.OnClickListener{
    Button butLogin;
    EditText etUsername, etPassword;
    String editTextUsername, editTextPassword;
    private static String[] strParameters;
    private static String ResultString = "";
    private String imeiNumber="";
    private String token="";
    TelephonyManager telephonyManager;
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        FirebaseMessaging.getInstance().subscribeToTopic("evarsity");
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        // Log and toast
//                String msg = getString(R.string.msg_token_fmt, token);
//                Log.d(TAG, msg);
//                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        deviceId();

        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM studentlogindetails ", null);
            if (cursor.moveToFirst()) {
                SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
                SharedPreferences.Editor ed = loginsession.edit();
                do {
                    ed.putLong("userid", cursor.getLong(cursor.getColumnIndex("studentid")));
                    ed.putString("registerno", cursor.getString(cursor.getColumnIndex("registerno")));
                    ed.putString("studentname", cursor.getString(cursor.getColumnIndex("studentname")));
                    ed.putString("program", cursor.getString(cursor.getColumnIndex("program")));
                    ed.putString("semester", cursor.getString(cursor.getColumnIndex("semester")));
                    ed.putString("school", cursor.getString(cursor.getColumnIndex("school")));
                    ed.putLong("courseid", cursor.getInt(cursor.getColumnIndex("courseid")));
                    ed.commit();
                } while (cursor.moveToNext());
                Intent intent = new Intent(LoginActivityOld.this, HomePageGridViewLayout.class);
                startActivity(intent);
            } else {

            }
            cursor.close();
        }catch (Exception e){

        }
        if(CheckNetwork.isInternetAvailable(LoginActivityOld.this)) {
            butLogin = findViewById(R.id.loginButton);
            butLogin.setOnClickListener(this);
            hideKeyboard();
        }
        else {
            Toast.makeText(LoginActivityOld.this,"You dont have Internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void deviceId(){
        try {
            telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
//                return;
//            } else {
            imeiNumber = telephonyManager.getDeviceId();
//                Log.d(TAG, imeiNumber);
//                Toast.makeText(MainActivity.this, imeiNumber, Toast.LENGTH_LONG).show();
//            }
        }catch(SecurityException e){
            imeiNumber = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
                        return;
                    }
                    imeiNumber = telephonyManager.getDeviceId();
//                    Log.d(TAG, imeiNumber);
//                    Toast.makeText(MainActivity.this,imeiNumber,Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivityOld.this,"Without permission we check",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View v){
        etUsername = findViewById(R.id.usernameInput);
        etPassword = findViewById(R.id.passwordInput);
        editTextUsername = etUsername.getText().toString().trim();
        editTextPassword = etPassword.getText().toString().trim();
        strParameters = new String[] { "String","userid","", "String","password", ""};

        switch (v.getId()){
            case R.id.loginButton:
                if (! Utility.isNotNull(editTextUsername)){
                    etUsername.setError("username is required!");
                }
                if (! Utility.isNotNull(editTextPassword)){
                    etPassword.setError("password is required!");
                }
                if (Utility.isNotNull(editTextPassword) && Utility.isNotNull(editTextUsername)) {
                    strParameters = new String[]{"String", "username", editTextUsername, "String", "password", editTextPassword,
                            "String", "deviceid", imeiNumber, "String", "accesstoken", token};
                    new Thread(new Runnable() {
                        public void run() {
                            WebService.strParameters = strParameters;
                            WebService.METHOD_NAME = "getStudentLogin";
                            AsyncCallWS task = new AsyncCallWS();
                            task.execute();
                        }
                    }).start();
                }
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Log.i(TAG, "onPostExecute");
            SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
            SharedPreferences.Editor ed = loginsession.edit();

            try{
                JSONObject object = new JSONObject(ResultString);
                if (!object.isNull("studentid")){
                    ed.putLong("userid", object.getLong("studentid"));
                    ed.putString("registerno", object.getString("registerno"));
                    ed.putString("studentname", object.getString("studentname"));
                    ed.putString("program", object.getString("program"));
                    ed.putString("semester", object.getString("semesterid"));
                    ed.putString("school", object.getString("officename"));
                    ed.putLong("courseid", object.getLong("courseid"));
                    Toast.makeText(LoginActivityOld.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                    ed.commit();
                    SqlliteController sc = new SqlliteController(LoginActivityOld.this);
                    sc.insertLoginStudentDetails(object.getLong("studentid"),object.getString("studentname"),
                            object.getString("registerno"),object.getString("program"),object.getString("semesterid"),
                            object.getString("officename"),object.getInt("courseid"));
                    Intent intent = new Intent(LoginActivityOld.this, HomePageGridViewLayout.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivityOld.this, "Login Failed: Not a Valid User Name / Password", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                System.out.println("Error in Login Activity:"+e.getMessage());
            }
        }
    }
}
