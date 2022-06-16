package com.shasun.studentinformationshasun;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ENoticeView extends AppCompatActivity {
    long lngStudentId = 0;
    String strEmployeeName = "";
    String strHTML = "";
    TextView tvPageTitle;
    private WebView wvENoticeView;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enoticeview);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));

        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
//        strEmployeeName = loginsession.getString("employeename","");
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("E-Notice Notification");
        Button button_refresh=(Button) findViewById(R.id.button_refresh);
        button_refresh.setVisibility(View.GONE);

        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
        onBackPressed();
            }
        });
        wvENoticeView = (WebView) findViewById(R.id.wvENoticeView);
        WebSettings webSettings = wvENoticeView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvENoticeView.setWebViewClient(new WebViewClient());
        wvENoticeView.getSettings().setBuiltInZoomControls(true);
        wvENoticeView.getSettings().setSupportZoom(true);
        wvENoticeView.getSettings().setUseWideViewPort(true);
        wvENoticeView.getSettings().setLoadWithOverviewMode(true);
        wvENoticeView.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/general/transaction/EnoticeNotificationViewforMobile.jsp?StudentId="+lngStudentId);
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        wvENoticeView.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        wvENoticeView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    private String getMimeType(String url){
        String parts[]=url.split("\\.");
        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return extension;
    }

    private boolean handleUri(WebView view,final Uri uri,String strMimeType) {
        // Based on some condition you need to determine if you are going to load the url
        // in your web view itself or in a browser.
        // You can use `host` or `scheme` or any part of the `uri` to decide.
        final Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        if (strMimeType.contains("pdf")){
            intent.setDataAndType(uri, "application/pdf");
        }
        try {
            view.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //user does not have a pdf viewer installed
        }
        return true;
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        ProgressDialog dialog3 = new ProgressDialog(ENoticeView.this);

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            dialog3.setMessage("Loading......");
            dialog3.show();
            Runnable progressRunnable = new Runnable() {
                @Override
                public void run() {
                    dialog3.dismiss();
//                    Toast.makeText(ENoticeView.this, "Slow Internet Connection", Toast.LENGTH_LONG).show();
                }
            };
            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 500);
            super.onPageStarted(view, url, favicon);
        }
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String strMimeType = getMimeType(url);

            String strExtension = url.substring(url.lastIndexOf(".") + 1);
            String tempurl = url.substring(0,(url.length() - (strExtension.length()+1)));
//            String[] strExtension=url.split(".");
            final Uri uri = Uri.parse(tempurl);
            return handleUri(view, uri,strMimeType);
        }
        @RequiresApi(Build.VERSION_CODES.N)
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            String strMimeType = getMimeType(uri.toString());
            return handleUri(view, uri,strMimeType);
        }
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return true;
//        }
        @Override
        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
            if (dialog3 != null && dialog3.isShowing()) {
                dialog3.dismiss();
            }
        }
    }
}
