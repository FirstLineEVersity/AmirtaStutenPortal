package com.shasun.studentinformationshasun;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import webservice.WebService;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ProfileFragment extends Fragment {
    TextView tvStudname, tvDob, tvMother, tvAddress, tvAdmitdate;
    TextView tvRegisterNo, tvBatch, tvFather, tvProgram, tvSemester, tvSchool;
    private long lngStudentId=0;
    private String strSchool="";
    private static String[] strParameters;
    private static String ResultString = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = getActivity();
        final SharedPreferences loginsession = context.getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        strSchool = loginsession.getString("school", "");
        strParameters = new String[] {"Long","studentid",String.valueOf(lngStudentId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "getPersonalDetails";
        new AsyncCallWS().execute();
        View v= inflater.inflate(R.layout.fragment_profile, container, false);
        return v;
        //return inflater.inflate(R.layout.fragment_profile, container, false);
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
        protected void onPostExecute(Void result){
            Log.i(TAG, "onPostExecute");
            //Toast.makeText(PersonalDetails.this, "Response : " + ResultString.toString(), Toast.LENGTH_LONG).show();
            Context context = getActivity();
            SharedPreferences loginsession = context.getSharedPreferences("SessionLogin", 0);
            SharedPreferences.Editor ed = loginsession.edit();
            try {
                JSONObject object = new JSONObject(ResultString);
                if (!object.isNull("studentid")){
                    tvSchool = getView().findViewById(R.id.txtSchool);
                    tvSchool.setText(strSchool);

                    tvStudname = getView().findViewById(R.id.txtStudentName);
                    tvStudname.setText(object.getString("studentname"));

                    tvRegisterNo = getView().findViewById(R.id.txtRegisterNo);
                    tvRegisterNo.setText(object.getString("registerno"));

                    tvBatch = getView().findViewById(R.id.txtBatch);
                    tvBatch.setText(object.getString("academicyear"));

                    tvDob = getView().findViewById(R.id.txtDob);
                    tvDob.setText(object.getString("dob")+","+object.getString("sex"));

//                    tvFather = (TextView) getView().findViewById(R.id.txtFather);
//                    tvFather.setText(object.getString("father"));
//
//                    tvMother = (TextView) getView().findViewById(R.id.txtMother);
//                    tvMother.setText(object.getString("mother"));

                    tvAddress = getView().findViewById(R.id.txtAddress);
                    tvAddress.setText(object.getString("address"));

                    tvSemester = getView().findViewById(R.id.txtSemesterSection);
                    tvSemester.setText(object.getString("semester") + "-" + object.getString("sectiondesc"));

                    tvProgram = getView().findViewById(R.id.txtProgram);
                    tvProgram.setText(object.getString("program")); //+ "-" + object.getString("universityname"));

                    tvAdmitdate = getView().findViewById(R.id.txtAdmittedDate);
                    tvAdmitdate.setText(object.getString("admitteddate"));

//                    tvEmail = (TextView) getView().findViewById(R.id.txtEmail);
//                    tvEmail.setText(object.getString("email"));
//
//                    tvHouseName = (TextView) getView().findViewById(R.id.txtHouseName);
//                    tvHouseName.setText(object.getString("housename"));

                    //Intent intent = new Intent(loginActivity.this, mainMenu.class);
                    //startActivity(intent);
                } else {
                    //Toast.makeText(PersonalDetails.this, "No Personal Details Found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {

            }
        }
    }
}
