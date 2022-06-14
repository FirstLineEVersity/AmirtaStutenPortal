package com.shasun.studentinformationshasun;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import webservice.WebService;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class FeeDetailsFragment extends Fragment {
    private long lngStudentId=0;
    private static String[] strParameters;
    private static String ResultString = "";

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        Context context = getActivity();
        final SharedPreferences loginsession = context.getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        strParameters = new String[] {"Long","studentid",String.valueOf(lngStudentId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "getFeeDetails";
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
        View v= inflater.inflate(R.layout.fragment_feedetails, container, false);
        return v;
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(getActivity());

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
//            TableLayout tl = getView().findViewById(R.id.tblViewfeedetails);
//            try {
//                JSONArray temp = new JSONArray(ResultString.toString());
//                addHeaderData();
//                for (int i = 0; i <= temp.length() - 1; i++) {
//                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
//                    String strRow = object.getString("feedetails");
//                    String[] strColumns = strRow.split("##");
//
////                    addData(strColumns);
//                }
//
//            } catch (Exception e) {
//            }
        }
    }

//    public void addHeaderData(){
//        String str[];
//        str = new String[]{"Fee Head", "Fixed Amount", "Amount Collected", "Due Date"};
//        TableLayout tl = getView().findViewById(R.id.tblViewfeedetails);
//        TableRow tr = new TableRow(getActivity());
//        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//        params1.setMargins(2, 0, 0, 2);
//        params1.weight = 1;
//        tr.setLayoutParams(params1);
//        tr.setBackgroundResource(R.color.colorhower);
//        for (int i = 0; i < str.length; i++){
//            TextView tv = new TextView(getActivity());
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
//        TableLayout tl = getView().findViewById(R.id.tblViewfeedetails);
//        TableRow tr = new TableRow(getActivity());
//        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//        params1.setMargins(2, 0, 0, 2);
//        params1.weight = 1;
//        tr.setLayoutParams(params1);
//        tr.setBackgroundResource(R.color.colornormal);
//        for (int i = 0; i < str.length; i++) {
//            TextView tv = new TextView(getActivity());
//            tv.setLayoutParams(params1);
//            tv.setText(str[i]);
//            tv.setPadding(20, 10, 20, 10);
//            if (i == 0 || i == str.length){
//                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//            } else {
//                tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
//            }
//            tv.setTextSize(18);
//            tr.addView(tv);
//        }
//        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//    }
}
