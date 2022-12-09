package com.amirta.studentinformation;

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
import android.os.Message;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import im.delight.android.webview.AdvancedWebView;

public class HelpDesk extends AppCompatActivity  {
    long lngStudentId = 0;
    TextView tvPageTitle;
    private AdvancedWebView wvHelpDesk;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helpdesk);
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Help Desk");
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));

        Button btnBack=(Button) findViewById(R.id.button_back);
        Button button_refresh=(Button) findViewById(R.id.button_refresh);
        button_refresh.setVisibility(View.GONE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
       onBackPressed();     }
        });
        wvHelpDesk = (AdvancedWebView ) findViewById(R.id.wvHelpDesk);
        WebSettings webSettings = wvHelpDesk.getSettings();
        webSettings.setJavaScriptEnabled(true);

//        wvHelpDesk.setListener(this, this);
        wvHelpDesk.setMixedContentAllowed(false);

        wvHelpDesk.setWebViewClient(new WebViewClient());
        wvHelpDesk.setWebChromeClient(new WebChromeClient());
        wvHelpDesk.getSettings().setBuiltInZoomControls(true);
        wvHelpDesk.getSettings().setSupportMultipleWindows(true);
        wvHelpDesk.getSettings().setPluginState(WebSettings.PluginState.ON);
        wvHelpDesk.getSettings().setAllowFileAccess(true);
        wvHelpDesk.getSettings().setSupportZoom(true);
        wvHelpDesk.getSettings().setUseWideViewPort(true);
        wvHelpDesk.getSettings().setLoadWithOverviewMode(true);
        wvHelpDesk.loadUrl("http://erp.shasuncollege.edu.in/shasunonline/students/transaction/HelpDeskforMobile.jsp?StudentId="+lngStudentId);

        wvHelpDesk.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            AdvancedWebView newWebView = new AdvancedWebView(HelpDesk.this);
            // myParentLayout.addView(newWebView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();
            return true;
            }
        });
    }

    @SuppressLint("NewApi")
    @Override 
    public void onResume() {
        super.onResume();
        wvHelpDesk.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        wvHelpDesk.onPause();
        // ...
        super.onPause();
    }

    @Override
    public void onDestroy() {
        wvHelpDesk.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        wvHelpDesk.onActivityResult(requestCode, resultCode, intent);
        // ...
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
        ProgressDialog dialog2 = new ProgressDialog(HelpDesk.this);

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            dialog2.setMessage("Loading......");
            dialog2.show();
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
            if (dialog2 != null && dialog2.isShowing()) {
                dialog2.dismiss();
            }
        }
    }
}
