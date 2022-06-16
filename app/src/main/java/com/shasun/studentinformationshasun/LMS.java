package com.shasun.studentinformationshasun;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
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

import java.io.File;
import java.net.URI;

import im.delight.android.webview.AdvancedWebView;

public class LMS extends AppCompatActivity{
    long lngStudentId = 0;
    TextView tvPageTitle;
    private AdvancedWebView wvLMS;
    private static final int FILECHOOSER_RESULTCODE   = 2888;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lms);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngStudentId = loginsession.getLong("userid", 1);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("LMS");
        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
      onBackPressed();      }
        });
        Button button_refresh=(Button) findViewById(R.id.button_refresh);
        button_refresh.setVisibility(View.GONE);

        wvLMS = (AdvancedWebView ) findViewById(R.id.wvLMS);
        wvLMS.getSettings().setJavaScriptEnabled(true);
        wvLMS.setWebViewClient(new WebViewClient());
        wvLMS.setWebChromeClient(new WebChromeClient());
//        WebSettings webSettings = wvLMS.getSettings();
//        webSettings.setJavaScriptEnabled(true);
        wvLMS.getSettings().setBuiltInZoomControls(true);
        wvLMS.getSettings().setSupportMultipleWindows(true);
        wvLMS.getSettings().setPluginState(WebSettings.PluginState.ON);

        wvLMS.getSettings().setAllowFileAccess(true);
        wvLMS.getSettings().setSupportZoom(true);
        wvLMS.getSettings().setUseWideViewPort(true);
        wvLMS.getSettings().setLoadWithOverviewMode(true);
//        wvLMS.getSettings().setSupportMultipleWindows(true);
//        wvLMS.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvLMS.loadUrl("http://erp.shasuncollege.edu.in/shasunonline/students/transaction/LMSClassWorkforMobile.jsp?StudentId="+lngStudentId);

        wvLMS.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                AdvancedWebView newWV = new AdvancedWebView(LMS.this);
                //            WebSettings webSettings = newWebView.getSettings();

//                newWebView.getSettings().setJavaScriptEnabled(true);
//                newWebView.getSettings().setSupportMultipleWindows(true);
//                newWebView.getSettings().setAllowFileAccessFromFileURLs(true);
//                newWebView.getSettings().getLoadsImagesAutomatically();
//                newWebView.getSettings().setAllowContentAccess(true);
//                newWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//
//                final Dialog dialog = new Dialog(LMS.this);
//                dialog.setContentView(newWebView);
//                dialog.show();
//                Window window = dialog.getWindow(); window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//
//                newWebView.setWebChromeClient(new WebChromeClient() {
//                    @Override
//                    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                        return super.onJsAlert(view, url, message, result);
//                    }
//
//                    @Override
//                    public void onCloseWindow(WebView window) {
//                        dialog.dismiss();
//                    }
//
//                });   // End setWebChromeClient
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWV);
                resultMsg.sendToTarget();
                return true;
            }
            });
    }

    private boolean handleUri(WebView view,final Uri uri,String strMimeType) {
//        Log.i(TAG, "Uri =" + uri);
//        final String host = uri.getHost();
//        final String scheme = uri.getScheme();
        // Based on some condition you need to determine if you are going to load the url
        // in your web view itself or in a browser.
        // You can use `host` or `scheme` or any part of the `uri` to decide.
//        if (/* any condition */) {
//            // Returning false means that you are going to load this url in the webView itself
//            return false;
//        } else {
            // Returning true means that you need to handle what to do with the url
            // e.g. open web page in a Browser
//            String[] strTempUrl=uri.toString().split(".");
//            Uri tempUri = Uri.parse(strTempUrl[0]);
            final Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//        if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
//            // Word document
//            intent.setDataAndType(uri, "application/msword");
//        } else if (uri.toString().contains(".pdf")) {
//            // PDF file
//            intent.setDataAndType(uri, "application/pdf");
//        } else if (uri.toString().contains(".ppt") || uri.toString().contains(".pptx")) {
//            // Powerpoint file
//            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//        } else if (uri.toString().contains(".xls") || uri.toString().contains(".xlsx")) {
//            // Excel file
//            intent.setDataAndType(uri, "application/vnd.ms-excel");
//        } else if (uri.toString().contains(".zip") || uri.toString().contains(".rar")) {
//            // WAV audio file
//            intent.setDataAndType(uri, "application/x-wav");
//        } else if (uri.toString().contains(".rtf")) {
//            // RTF file
//            intent.setDataAndType(uri, "application/rtf");
//        } else if (uri.toString().contains(".wav") || uri.toString().contains(".mp3")) {
//            // WAV audio file
//            intent.setDataAndType(uri, "audio/x-wav");
//        } else if (uri.toString().contains(".gif")) {
//            // GIF file
//            intent.setDataAndType(uri, "image/gif");
//        } else if (uri.toString().contains(".jpg") || uri.toString().contains(".jpeg")) {
//            // JPG file
//            intent.setDataAndType(uri, "image/jpeg");
//        } else if (uri.toString().contains(".png")) {
//            // JPG file
//            intent.setDataAndType(uri, "image/png");
//        }else if (uri.toString().contains(".txt")) {
//            // Text file
//            intent.setDataAndType(uri, "text/plain");
//        } else if (uri.toString().contains(".3gp") || uri.toString().contains(".mpg") || uri.toString().contains(".mpeg") || uri.toString().contains(".mpe") || uri.toString().contains(".mp4") || uri.toString().contains(".avi")) {
//            // Video files
//            intent.setDataAndType(uri, "video/*");
//        } else {
//            //if you want you can also define the intent type for any other file
//            //additionally use else clause below, to manage other unknown extensions
//            //in this case, Android will show all applications installed on the device
//            //so you can choose which application to use
//            intent.setDataAndType(uri, "*/*");
//        }
//        String strMimeType = getMimeType(uri.toString());
        if (strMimeType.contains("pdf")){
            intent.setDataAndType(uri, "application/pdf");
        }
//        else if (strMimeType.contains("doc") || strMimeType.contains("docx")) {
////            // Word document
//            intent.setDataAndType(uri, "application/msword");
//        }else if (strMimeType.contains("txt")) {
////            // Text file
//            intent.setDataAndType(uri, "text/plain");
//        }else if (strMimeType.contains("xls") || strMimeType.contains("xlsx")) {
////            // Excel file
//            intent.setDataAndType(uri, "application/vnd.ms-excel");
//        }else if (strMimeType.contains("ppt") || strMimeType.contains("pptx")) {
////            // Powerpoint file
//            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//        }else if (strMimeType.contains("rtf")) {
////            // RTF file
//            intent.setDataAndType(uri, "application/rtf");
//        }
//        else if (strMimeType.contains("png")){
//            intent.setDataAndType(uri, "image/png");
//        }
//        intent.setDataAndType(uri, strMimeType);
        try {
            view.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //user does not have a pdf viewer installed
        }
//            startActivity(intent);
            return true;
//        }
    }

    private String getMimeType(String url)
    {
        String parts[]=url.split("\\.");
        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return extension;
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        ProgressDialog dialog5 = new ProgressDialog(LMS.this);

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            dialog.setMessage("Loading......");
//            dialog.show();
            dialog5.setMessage("Loading.........");
            dialog5.show();
            Runnable progressRunnable = new Runnable() {
                @Override
                public void run() {
                    dialog5.dismiss();
//                    Toast.makeText(LMS.this, "Slow Internet Connection", Toast.LENGTH_LONG).show();
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
            super.onPageFinished(view, url);
            if (dialog5 != null && dialog5.isShowing()) {
                dialog5.dismiss();
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        wvLMS.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        wvLMS.onPause();
        // ...
        super.onPause();
    }

    @Override
    public void onDestroy() {
//        dialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        wvLMS.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

//    @Override
//    public void onPageStarted(String url, Bitmap favicon) { }
//
//    @Override
//    public void onPageFinished(String url) { }
//
//    @Override
//    public void onPageError(int errorCode, String description, String failingUrl) { }
//
//    @Override
//    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }
//
//    @Override
//    public void onExternalPageRequest(String url) { }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
       }
}
