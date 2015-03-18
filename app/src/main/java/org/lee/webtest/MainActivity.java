package org.lee.webtest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.lee.android.util.Log;
import org.lee.android.util.Toast;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private TextView mMessageText;
    private Button mButtonGo;
    private EditText mUrlEdit;
    private EditText mJsEdit;
    private TextView mProgressNum;
    private ProgressBar mProgressBar;
    private WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.init(this);
        setContentView(R.layout.activity_main);
        init();
    }


    private void init() {
        mMessageText = (TextView) findViewById(R.id.MessageView);
        mButtonGo = (Button) findViewById(R.id.Go);
        mButtonGo.setOnClickListener(this);
        mUrlEdit = (EditText) findViewById(R.id.Url);
        mJsEdit = (EditText) findViewById(R.id.Js);
        findViewById(R.id.JsInject).setOnClickListener(this);

        mProgressNum = (TextView) findViewById(R.id.ProgressNum);
        mProgressBar = (ProgressBar) findViewById(android.R.id.progress);
        mWebView = (WebView) findViewById(R.id.WebView);
        mWebView = (WebView) findViewById(R.id.WebView);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    AppFunction.hideInputMethod(getBaseContext(), v);
                    return false;
                }
                return false;
            }
        });
        buildWebView(mWebView);
        mWebView.loadUrl("http://www.woolom.com");
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mMessageText.setText(null);
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mUrlEdit.setText(url);
            setTitle(view.getTitle());

            mProgressNum.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            setTitle(view.getTitle());
            mProgressNum.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }
    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            setTitle(title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressNum.setText(newProgress + "%");
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.anchor(consoleMessage.message());
            mMessageText.setText("Log:"
                    + "\n" + consoleMessage.message()
                    + "\n" + consoleMessage.lineNumber()
                    + "\n" + consoleMessage.sourceId());
            return super.onConsoleMessage(consoleMessage);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.Refresh:
                mWebView.reload();
                return true;
            case R.id.About:
                AboutActivity.launch(this);
                return true;
            case R.id.ClearCache:
                mWebView.clearCache(true);
                Toast.show("缓存已清除!");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String js = "var newscript = document.createElement(\"script\");\n" +
            "        newscript.src=\"js-url\";\n" +
            "        newscript.onload=function(){\n" +
            "                alert('JavaScript已注入');\n" +
            "              };\n" +
            "        document.body.appendChild(newscript);";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Go:
                String url = mUrlEdit.getText().toString().trim();
                if (TextUtils.isEmpty(url)) {
                    Toast.show(mUrlEdit.getHint().toString());
                    return;
                }
                AppFunction.hideInputMethod(getBaseContext(), v);
                mMessageText.setText(null);
                url = UrlUtils.smartUrlFilter(url);
                Log.anchor(url);
                mWebView.loadUrl(url);
                return;
            case R.id.JsInject:
                String jsAdress = mJsEdit.getText().toString().trim();
                if (TextUtils.isEmpty(jsAdress)) {
                    Toast.show(mJsEdit.getHint().toString());
                    return;
                }
                AppFunction.hideInputMethod(getBaseContext(), v);
                jsAdress = UrlUtils.smartUrlFilter(jsAdress, false);
                js = js.replace("js-url", jsAdress);
                mWebView.loadUrl("javascript:" + js);
                Log.anchor(js);
//                mWebView.loadUrl("javascript:alert('hahh')");
                Toast.show("JavaScript注入中...");
                return;
        }

    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    private void buildWebView(WebView webView) {
        webView.setHorizontalScrollBarEnabled(false);//水平不显示
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);//(见@1)WebView加载某些js代码时，WebView会找不到js中的localStorage属性，例如localStorage.index
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webSettings.setLoadsImagesAutomatically(true);
    }

}
