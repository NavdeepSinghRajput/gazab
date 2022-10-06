package com.gazabapp.webview;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gazabapp.R;

public class WebviewActivity extends AppCompatActivity {

    WebView wv;
    ProgressBar Pbar;
    ProgressDialog progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        wv = (WebView) findViewById(R.id.webView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Pbar = (ProgressBar) findViewById(R.id.pB1);

        WebSettings ws = wv.getSettings();
        //Add all plugins
        ws.setJavaScriptEnabled(true);
        ws.setLoadWithOverviewMode(true);
        ws.setAllowFileAccess(true);
        ws.setDomStorageEnabled(true);
        ws.setDatabaseEnabled(true);
        ws.setMinimumFontSize(1);
        ws.setMinimumLogicalFontSize(1);
        String url = getIntent().getStringExtra("url");
        wv.loadUrl(url);

        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            //Show loader on url load
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Then show progress  Dialog
                // in standard case YourActivity.this
                if (progressbar == null) {
                    progressbar = new ProgressDialog(WebviewActivity.this);
                    progressbar.setCancelable(false);
                    progressbar.setMessage("Loading...");
                    progressbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressbar.show();
                }
                Log.e("PageStartUrl", url);
            }

            // Called when all page resources loaded
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressbar.isShowing()) {
                        progressbar.dismiss();
                        progressbar = null;
                    }
                } catch (Exception exception) {
                    Log.e("Exception", exception.getMessage());
                    exception.printStackTrace();
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
                Log.e("onLoadResource", url);

            }
        });

        wv.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            // For Android 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
                // Double check that we don't have any existing callbacks
                return true;
            }

            // openFileChooser for Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            }

            // openFileChooser for Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            //openFileChooser for other Android versions
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100 && Pbar.getVisibility() == ProgressBar.GONE) {
                    Pbar.setVisibility(ProgressBar.VISIBLE);
                }
                Pbar.setProgress(newProgress);
                if (newProgress == 100) {
                    Pbar.setVisibility(ProgressBar.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        CookieSyncManager.createInstance(this);
        CookieManager.getInstance();
    }
}