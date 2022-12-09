package com.amirta.studentinformation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import im.delight.android.webview.AdvancedWebView;

public class LeaveEntry extends AppCompatActivity{
    long lngStudentId = 0;
    TextView tvPageTitle;
//    private ProgressDialog dialog6;
    private AdvancedWebView wvLeaveEntry;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaveentry);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));

        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Leave Entry");
        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
           onBackPressed(); }
        });
        wvLeaveEntry = (AdvancedWebView) findViewById(R.id.wvLeaveEntry);
        wvLeaveEntry.setWebViewClient(new WebViewClient());
        wvLeaveEntry.getSettings().setJavaScriptEnabled(true);
        wvLeaveEntry.getSettings().setBuiltInZoomControls(true);
        wvLeaveEntry.getSettings().setSupportMultipleWindows(true);
        wvLeaveEntry.getSettings().setPluginState(WebSettings.PluginState.ON);
        wvLeaveEntry.getSettings().setAllowFileAccess(true);
        wvLeaveEntry.getSettings().setSupportZoom(true);
        wvLeaveEntry.getSettings().setUseWideViewPort(true);
        wvLeaveEntry.getSettings().setLoadWithOverviewMode(true);
        wvLeaveEntry.loadUrl("http://erp.shasuncollege.edu.in/shasunonline/students/report/StudentLeaveApplicationforMobile.jsp?StudentId="+lngStudentId);

        wvLeaveEntry.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                AdvancedWebView newWebView = new AdvancedWebView(LeaveEntry.this);
                //            WebSettings webSettings = newWebView.getSettings();

                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.getSettings().setSupportMultipleWindows(true);
                newWebView.getSettings().setAllowFileAccessFromFileURLs(true);
                newWebView.getSettings().getLoadsImagesAutomatically();
                newWebView.getSettings().setAllowContentAccess(true);
                newWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

                final Dialog dialog = new Dialog(LeaveEntry.this);
                dialog.setContentView(newWebView);
                dialog.show();
                Window window = dialog.getWindow(); window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                newWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                        return super.onJsAlert(view, url, message, result);
                    }

                    @Override
                    public void onCloseWindow(WebView window) {
                        dialog.dismiss();
                    }

                });   // End setWebChromeClient
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
        wvLeaveEntry.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        wvLeaveEntry.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        wvLeaveEntry.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
      }

    public class WebViewClient extends android.webkit.WebViewClient {
        ProgressDialog dialog6 = new ProgressDialog(LeaveEntry.this);
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            dialog6.setMessage("Loading......");
            dialog6.show();
            Runnable progressRunnable = new Runnable() {
                @Override
                public void run() {
                    dialog6.dismiss();
//                    Toast.makeText(LMS.this, "Slow Internet Connection", Toast.LENGTH_LONG).show();
                }
            };
            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 500);
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
            if (dialog6 != null && dialog6.isShowing()) {
                dialog6.dismiss();
            }
        }

        @Override
        public void onReceivedError(WebView view,int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            dialog6.dismiss();
            Toast.makeText(getApplicationContext(),
                    "Error: The Requested Page Does Not Exist", Toast.LENGTH_SHORT).show();
        }
    }
}
