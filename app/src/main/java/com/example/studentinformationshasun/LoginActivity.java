package com.example.studentinformationshasun;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
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
    private static final int UPDATE_REQUEST_CODE = 530;
    private AppUpdateManager appUpdateManager;
    long lngEmployeeId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dialog = new ProgressDialog(LoginActivity.this);

        inAppUpdate();

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
                         // Log.e("Token Test : ", token);
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
            Log.i("TEST",WebService.METHOD_NAME);
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Log.i("TEST",ResultString);
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


    private void inAppUpdate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {

                Log.d("AVAILABLE_VERSION_CODE", appUpdateInfo.availableVersionCode()+"");
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    // Request the update.

                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.FLEXIBLE,
                                // The current activity making the update request.
                                LoginActivity.this,
                                // Include a request code to later monitor this update request.
                                UPDATE_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException ignored) {

                    }
                }
            }
        });

        appUpdateManager.registerListener(installStateUpdatedListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        appUpdateManager.unregisterListener(installStateUpdatedListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UPDATE_REQUEST_CODE && resultCode != RESULT_OK ){
            Toast.makeText(this,"Update Canceled",Toast.LENGTH_LONG).show();
        }
    }

    //lambda operation used for below listener
    InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState installState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            } else
                Log.e("UPDATE", "Not downloaded yet");
        }
    };


    private void popupSnackbarForCompleteUpdate() {

        Snackbar snackbar =
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "New app is ready!",
                        Snackbar.LENGTH_INDEFINITE);
        //lambda operation used for below action
        snackbar.setAction(this.getString(R.string.install), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUpdateManager.completeUpdate();

                db = controllerdb.getReadableDatabase();
                try {
                    Cursor cursor = db.rawQuery("SELECT * FROM stafflogindetails ", null);
                    if (cursor.moveToFirst()) {
                        do {
                            lngEmployeeId = cursor.getLong(cursor.getColumnIndex("employeeid"));

                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }catch (Exception e){

                }
                if(lngEmployeeId >0 ) {
                    if (!CheckNetwork.isInternetAvailable(LoginActivity.this)) {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        WebService.strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
                        WebService.METHOD_NAME = getResources().getString(R.string.wsMenuAccessRights);
                        AsyncCallWSMenu task = new AsyncCallWSMenu();
                        task.execute();
                    }
                }

            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorWhite));
        snackbar.show();
    }
    private class AsyncCallWSMenu extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.loading));
            //show dialog
            dialog.show();
            //Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Log.i(TAG, "doInBackground");
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Log.i("TEST", ResultString);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                SqlliteController sc = new SqlliteController(LoginActivity.this);
               // sc.insertMenuidDetails(lngEmployeeId,ResultString);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Toast.makeText(LoginActivity.this,ResultString,Toast.LENGTH_LONG).show();
            }

        }
    }

}

