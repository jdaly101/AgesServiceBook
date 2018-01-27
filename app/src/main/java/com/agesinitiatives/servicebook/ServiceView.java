package com.agesinitiatives.servicebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ServiceView extends AppCompatActivity {

    private static final String TAG = "ServiceView";
    private static final String BASE_AGES_URL = "http://www.agesinitiatives.com/dcs/public/dcs/";
    private WebView webView;
    private String serviceUrl;

    private String displayLang;
    private String fontSize;
    private boolean tapLangSwap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_view);

        webView = findViewById(R.id.serviceWebView);
        webView.getSettings().setJavaScriptEnabled(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        displayLang = sharedPreferences.getString("service_lang_preference", "");
        fontSize = sharedPreferences.getString("service_font_size", "");
        tapLangSwap = sharedPreferences.getBoolean("tap_swap_langs", false);

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
            Element content = doc.select(".content").first();
            content.appendElement("link")
                    .attr("rel", "stylesheet")
                    .attr("type", "text/css")
                    .attr("href", "services.css");
            if (displayLang.equals("EN")) {
                content.appendElement("link")
                        .attr("rel", "stylesheet")
                        .attr("type", "text/css")
                        .attr("href", "services-en.css");
            } else if (displayLang.equals("GR")) {
                content.appendElement("link")
                        .attr("rel", "stylesheet")
                        .attr("type", "text/css")
                        .attr("href", "services-gr.css");
            }

            if (fontSize.equals("Small")) {
                content.appendElement("link")
                        .attr("rel", "stylesheet")
                        .attr("type", "text/css")
                        .attr("href", "font-small.css");
            } else if (fontSize.equals("Medium")) {
                content.appendElement("link")
                        .attr("rel", "stylesheet")
                        .attr("type", "text/css")
                        .attr("href", "font-medium.css");
            } else if (fontSize.equals("Large")) {
                content.appendElement("link")
                        .attr("rel", "stylesheet")
                        .attr("type", "text/css")
                        .attr("href", "font-large.css");
            }

            content.appendElement("script")
                    .attr("src", "jquery-3.2.1.min.js");
            content.appendElement("script")
                    .attr("src", "services.js");

            if (tapLangSwap) {
                content.appendElement("script")
                        .attr("src", "langSwap.js");
            }

            String html = content.outerHtml();
            webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
        }
    }
}
