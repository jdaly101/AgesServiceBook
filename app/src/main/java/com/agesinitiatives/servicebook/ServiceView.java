package com.agesinitiatives.servicebook;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ServiceView extends AppCompatActivity {

    private static final String TAG = "ServiceView";
    private static final String BASE_AGES_URL = "http://www.agesinitiatives.com/dcs/public/dcs/";
    private WebView webView;
    private String serviceUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_view);

        webView = findViewById(R.id.serviceWebView);

        Intent intent = getIntent();
        serviceUrl = intent.getStringExtra("SERVICE_URL");
        Log.i(TAG, "SERVICE URL: " + serviceUrl);

        DocLoader docLoader = new DocLoader();
        docLoader.execute();
    }

    private class DocLoader extends AsyncTask<Void, Void, Void> {
        Document doc;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                doc = Jsoup.connect(BASE_AGES_URL + serviceUrl).get();
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
            String html = doc.select(".content").first().outerHtml();
            webView.loadDataWithBaseURL("file:///android_asset/.", html, "text/html", "UTF-8", null);
        }
    }
}