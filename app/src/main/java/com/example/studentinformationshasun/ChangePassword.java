package com.example.studentinformationshasun;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import webservice.SqlliteController;
import webservice.WebService;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * Created by Firstline Infotech on 30-05-2019.
 */

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    private static String[] strParameters;
    Button butSave;
    ProgressDialog prgDialog;
    TextView tvPageTitle;
    long lngStudentId=0;
    private static String ResultString = "";
    EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    String editCurrentPassword, editNewPassword, editConfirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = findViewById(R.id.pageTitle);
        tvPageTitle.setText("Change Password");
        Button btnBack= findViewById(R.id.button_back);
        Button btnRefresh= findViewById(R.id.button_refresh);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
     onBackPressed();
            }
        });
        btnRefresh.setVisibility(TextView.INVISIBLE);
        butSave = findViewById(R.id.butChangePassword);
        butSave.setOnClickListener(this);
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        hideKeyboard();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null){
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v){
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        etCurrentPassword = findViewById(R.id.currentpasswordInput);
        etNewPassword = findViewById(R.id.newpasswordInput);
        etConfirmNewPassword = findViewById(R.id.confirmnewpasswordInput);
        editCurrentPassword = etCurrentPassword.getText().toString().trim();
        editNewPassword = etNewPassword.getText().toString().trim();
        editConfirmNewPassword = etConfirmNewPassword.getText().toString().trim();
        if (validate()){
            switch (v.getId()){
                case R.id.butChangePassword:
                    if (Utility.isNotNull(editCurrentPassword) && Utility.isNotNull(editNewPassword) && Utility.isNotNull(editConfirmNewPassword)){
                        strParameters = new String[]{"Long", "studentid", String.valueOf(lngStudentId), "String", "oldpassword", editCurrentPassword, "String", "newpassword", editNewPassword, "String", "confirmnewpassword", editConfirmNewPassword};
                        new Thread(new Runnable() {
                            public void run() {
                                WebService.strParameters = strParameters;
                                WebService.METHOD_NAME = "ChangeUserPassword";
                                AsyncCallWS task = new AsyncCallWS();
                                task.execute();
                            }
                        }).start();
                    }
            }
        }
    }

    private boolean validate(){
        boolean temp=true;
        if (!Utility.isNotNull(editCurrentPassword)) {
            etCurrentPassword.setError("current password is required!");
            temp=false;
        }
        if (!Utility.isNotNull(editNewPassword)) {
            etNewPassword.setError("password is required!");
            temp=false;
        }
        if (!Utility.isNotNull(editConfirmNewPassword)) {
            etConfirmNewPassword.setError("confirm new password is required!");
            temp=false;
        }
        if(!editNewPassword.equals(editConfirmNewPassword)){
            Toast.makeText(ChangePassword.this,"Password Not matching",Toast.LENGTH_SHORT).show();
            temp=false;
        }
        return temp;
    }

    private void clearForm(ViewGroup group){
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText){
                ((EditText)view).setText("");
            }

            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearForm((ViewGroup)view);
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
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
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            Toast.makeText(ChangePassword.this, "Response : Password Changed Successfully" , Toast.LENGTH_LONG).show();
            clearForm(findViewById(R.id.changepasswordlayout));
            SharedPreferences myPrefs = getSharedPreferences("SessionLogin", MODE_PRIVATE);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.clear();
            editor.commit();
            SqlliteController sc = new SqlliteController(ChangePassword.this);
            sc.deleteLoginStudentDetails();

            Intent intent = new Intent(ChangePassword.this, LoginActivityOld.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
    }
}
