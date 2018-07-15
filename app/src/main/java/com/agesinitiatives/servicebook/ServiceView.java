package com.agesinitiatives.servicebook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ServiceView extends AppCompatActivity {

    private static final String TAG = "ServiceView";
    private static final String BASE_AGES_URL = "http://www.agesinitiatives.com/dcs/public/dcs/";
    private WebView webView;
    private String serviceUrl;
    private String serviceTitle;

    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private DocLoader docLoader;

    private String displayLang;
    private String fontSize;
    private boolean nightMode;
    private boolean tapLangSwap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_view);
        progressBar = findViewById(R.id.progressBarServices);
        progressBar.setVisibility(View.VISIBLE);

        webView = findViewById(R.id.serviceWebView);
        webView.getSettings().setJavaScriptEnabled(true);

        refreshPreferences();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        serviceUrl = extras.getString("SERVICE_URL");
        serviceTitle = extras.getString("SERVICE_TITLE");
        setTitle(serviceTitle);

//        docLoader = new DocLoader();
//        docLoader.execute();
    }

    @Override
    public void onResume() {
        refreshPreferences();

        docLoader = new DocLoader();
        docLoader.execute();

        super.onResume();
    }

    private void refreshPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        displayLang = sharedPreferences.getString("service_lang_preference", "");
        fontSize = sharedPreferences.getString("service_font_size", "");
        nightMode = sharedPreferences.getBoolean("night_mode", false);
        tapLangSwap = sharedPreferences.getBoolean("tap_swap_langs", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final Context context = getApplicationContext();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(context, SettingsActivity.class);
            intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.AllPrefsFragment.class.getName());
            intent.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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
//                Log.e(TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            Element content = doc.select(".content").first();
            content.appendElement("link")
                    .attr("rel", "stylesheet")
                    .attr("type", "text/css")
                    .attr("href", "services.css");
            if (nightMode) {
                content.appendElement("link")
                        .attr("rel", "stylesheet")
                        .attr("type", "text/css")
                        .attr("href", "services-night.css");
            } else {
                content.appendElement("link")
                        .attr("rel", "stylesheet")
                        .attr("type", "text/css")
                        .attr("href", "services-day.css");
            }
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
