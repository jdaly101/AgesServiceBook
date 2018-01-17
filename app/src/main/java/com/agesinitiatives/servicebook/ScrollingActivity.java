package com.agesinitiatives.servicebook;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ScrollingActivity extends AppCompatActivity {
    final static String TAG = "ScrollingActivity";
    private final static String AGES_URL = "http://agesinitiatives.com/dcs/public/dcs/";
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String serviceUrl = intent.getStringExtra("service_url");

        webView = findViewById(R.id.serviceWebView);

        DocLoader docLoader = new DocLoader(serviceUrl);
        docLoader.execute();
//        webView.loadData("<div><h1>Hello world!</h1><p>More content to follow</p></div>", "text/html", null);

//        webView.getSettings().setBlockNetworkImage(true);
//        webView.getSettings().setJavaScriptEnabled(false);
//        webView.loadUrl(AGES_URL + serviceUrl);


//        getWindow().requestFeature(Window.FEATURE_PROGRESS);
    }

    private class DocLoader extends AsyncTask<Void, Void, Void> {
        Document doc;
        private String url;

        private DocLoader(String myUrl) {
            super();
            url = myUrl;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String myUrl = "http://www.agesinitiatives.com/dcs/public/dcs/h/s/2018/01/07/li8/gr-en/index.html";
                doc = Jsoup.connect(myUrl).get();
                doc.head().getElementsByTag("link").remove();
                doc.head().getElementsByTag("script").remove();
                doc.body().getElementsByTag("image").remove();
                doc.body().getElementsByTag("a").remove();
                doc.body().getElementsByTag("i").remove();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            doc.select("tr:gt(70)").remove();
            String html = doc.select(".content").first().outerHtml();
            Log.i(TAG, Integer.toString(html.length()));
            webView.loadDataWithBaseURL("file:///android_asset/.", html, "text/html", "UTF-8", null);
        }
    }
}
