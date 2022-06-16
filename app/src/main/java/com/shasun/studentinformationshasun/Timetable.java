package com.shasun.studentinformationshasun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class Timetable extends AppCompatActivity {
    long lngStudentId = 0;
    TextView tvPageTitle;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mytimetable);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Timetable");
        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
       onBackPressed();
            }
        });
        Button button_refresh=(Button) findViewById(R.id.button_refresh);
        button_refresh.setVisibility(View.GONE);

        WebView wvTimetable = (WebView) findViewById(R.id.wvTimetable);
        WebSettings webSettings = wvTimetable.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvTimetable.setWebViewClient(new WebViewClient());
        wvTimetable.getSettings().setBuiltInZoomControls(true);
        wvTimetable.getSettings().setSupportZoom(true);
        wvTimetable.getSettings().setUseWideViewPort(true);
        wvTimetable.getSettings().setLoadWithOverviewMode(true);
        wvTimetable.loadUrl("http://erp.shasuncollege.edu.in/shasunonline/students/report/TReportSubFormforMobile.jsp?StudentId="+lngStudentId);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
       }

    public class WebViewClient extends android.webkit.WebViewClient {
        ProgressDialog dialog = new ProgressDialog(Timetable.this);

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            dialog.setMessage("Loading......");
            dialog.show();
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
