package com.maqsood007.gvrwebview;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.gearvrf.GVRActivity;
import org.gearvrf.scene_objects.view.GVRFrameLayout;
import org.gearvrf.scene_objects.view.GVRWebView;

public class GVRWebViewActivity extends GVRActivity implements View.OnTouchListener, View.OnClickListener,
        View.OnFocusChangeListener {


    private GVRWebViewMain main;
    private GVRWebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFrameLayout();
        main = new GVRWebViewMain(this);
        setMain(main, "gvr.xml");
    }


    private void setFrameLayout() {
        webView = new GVRWebView(this);

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setSupportZoom(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setWebChromeClient(new WebChromeClient() {
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(final WebView view, String url) {
                Log.v("onPageStarted URL", url);
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.v("onPageStarted URL", url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.v("shouldOverride URL", url);
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                Log.v("onPageFinished URL", url);
            }
        });
        String linkURLType = "https://www.youtube.com";
        webView.loadUrl(linkURLType);
    }

    public GVRWebView getFrameLayout() {
        return webView;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Log.d("Main onTouchEvent", "Tapped at: ");
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        } else {
            if (main != null) {
                main.onTouchEvent(event);
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }
}
