package com.amirta.studentinformation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amirta.studentinformation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import webservice.EncryptDecrypt;
import webservice.SqlliteController;
import webservice.WebService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    Button butLogin;
    ImageButton btnInfo;
    TextInputEditText etUsername, etPassword;
    TextInputLayout passwordInputLayout,usernameInputLayout;
    String editTextUsername, editTextPassword;
    ProgressDialog dialog;
    private static String[] strParameters;
    private static String ResultString = "";
    SQLiteDatabase db;
    private String imeiNumber="";
    private String token="";
    TelephonyManager telephonyManager;
    SqlliteController controllerdb = new SqlliteController(this);
    EncryptDecrypt crypt = new EncryptDecrypt();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialog = new ProgressDialog(LoginActivity.this);


        FirebaseMessaging.getInstance().subscribeToTopic("evarsity");
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        token = task.getResult().getToken();
                          Log.e("Token Test : ", token);
                    }
                });


        deviceId();

        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
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
                Intent intent = new Intent(LoginActivity.this, HomePageGridViewLayout.class);
                startActivity(intent);
            } else {

            }
            cursor.close();
        }catch (Exception e){

        }

        if(CheckNetwork.isInternetAvailable(LoginActivity.this)) {
            butLogin = findViewById(R.id.loginButton);
            butLogin.setOnClickListener(this);
            btnInfo = findViewById(R.id.btnInfo);
            btnInfo.setOnClickListener(this);
            hideKeyboard();
        }
        else {
            Toast.makeText(LoginActivity.this,getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
        }




    }

    @Override
    public void onClick(View v){
        etUsername = findViewById(R.id.usernameInput);
        etPassword = findViewById(R.id.passwordInput);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        editTextUsername = etUsername.getText().toString().trim();
        editTextPassword = etPassword.getText().toString().trim();
        strParameters = new String[] { "String","userid","", "String","password", ""};
        SharedPreferences myPrefs = v.getContext().getSharedPreferences("SessionLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.clear();
        editor.commit();
        SqlliteController sc = new SqlliteController(v.getContext());
        //sc.deleteLoginStaffDetails();

        switch (v.getId()){
            case R.id.loginButton:
                if (!CheckNetwork.isInternetAvailable(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this,getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                    return;
                }else {

                    if (!Utility.isNotNull(editTextUsername)) {
                        usernameInputLayout.setError("username is required!");
                    }
                    if (!Utility.isNotNull(editTextPassword)) {
                        // etPassword.setError("password is required!");
                        passwordInputLayout.setError("password is required!");
                    }
                    deviceId();
                    if (Utility.isNotNull(editTextPassword) && Utility.isNotNull(editTextUsername)) {
                        strParameters = new String[]{"String", "username", editTextUsername, "String", "password", editTextPassword,
                                "String", "deviceid", imeiNumber, "String", "accesstoken", token};

                        new Thread(new Runnable() {
                            public void run() {
                                //Log.i("TEST",editTextUsername + " : " +editTextPassword );
                                WebService.strParameters = strParameters;
                                WebService.METHOD_NAME = "getStudentLogin";  //"authenticateLoginUserJsonEncrypted";
                                AsyncCallWS task = new AsyncCallWS();
                                task.execute();
                            }
                        }).start();
                        // dialog.setMessage("Loading......");
                        dialog.setMessage(getResources().getString(R.string.loading));

                        dialog.show();
                        hideKeyboard();
                        butLogin.setEnabled(false);
                    }
                }
                break;
            case R.id.btnInfo:
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginCredentials), Toast.LENGTH_LONG).show();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    private void deviceId(){
        try {
            telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            imeiNumber = telephonyManager.getDeviceId();
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
                    Toast.makeText(LoginActivity.this,getResources().getString(R.string.loginPermission),Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void hideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
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
                    Toast.makeText(LoginActivity.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                    ed.commit();
                    SqlliteController sc = new SqlliteController(LoginActivity.this);
                    sc.insertLoginStudentDetails(object.getLong("studentid"),object.getString("studentname"),
                            object.getString("registerno"),object.getString("program"),object.getString("semesterid"),
                            object.getString("officename"),object.getInt("courseid"));
                      if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    Intent intent = new Intent(LoginActivity.this, HomePageGridViewLayout.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    butLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginFailed), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                butLogin.setEnabled(true);
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.loginFailed), Toast.LENGTH_LONG).show();
                Toast.makeText(LoginActivity.this, "Error in Login: "+e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //this.finish();
        finishAffinity();
    }


}

