package com.agesinitiatives.servicebook;

import android.annotation.SuppressLint;
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
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ServiceView extends AppCompatActivity {

    private static final String TAG = "ServiceView";
    private static final String BASE_AGES_URL = "https://agesinitiatives.com/dcs/public/dcs/";
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
    private boolean showMusicLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_view);
        progressBar = findViewById(R.id.progressBarServices);
        progressBar.setVisibility(View.VISIBLE);

        WebView.setWebContentsDebuggingEnabled(true);
        webView = findViewById(R.id.serviceWebView);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setDomStorageEnabled(true);
//        webView.setWebChromeClient(new WebChromeClient());

        refreshPreferences();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        serviceUrl = extras.getString("SERVICE_URL");
        serviceTitle = extras.getString("SERVICE_TITLE");
        setTitle(serviceTitle);

        docLoader = new DocLoader();
        docLoader.execute();

    }

    @Override
    public void onResume() {
        refreshPreferences();

//        docLoader = new DocLoader();
//        docLoader.execute();

        super.onResume();
    }

    private void refreshPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        displayLang = sharedPreferences.getString("service_lang_preference", "");
        fontSize = sharedPreferences.getString("service_font_size", "");
        nightMode = sharedPreferences.getBoolean("night_mode", false);
        tapLangSwap = sharedPreferences.getBoolean("tap_swap_langs", false);
        showMusicLinks = sharedPreferences.getBoolean("show_music_links", false);
        Log.i(TAG, "Show music links: " + showMusicLinks);
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

        private void appendStylesheet(Element content, String cssFileName) {
            content.appendElement("link")
                    .attr("rel", "stylesheet")
                    .attr("type", "text/css")
                    .attr("href", cssFileName);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Connection.Response response = Jsoup.connect(BASE_AGES_URL + serviceUrl).timeout(10000).execute();
                if (response.statusCode() == 200) {
                    doc = response.parse();
                    doc.head().getElementsByTag("link").remove();
                    doc.head().getElementsByTag("script").remove();
                    doc.body().getElementsByTag("image").remove();
                }

            } catch (IOException e) {
//                Log.e(TAG, e.toString());
            }
            return null;
        }

        @SuppressLint("JavascriptInterface")
        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            Element content = doc.select(".content").first();
            appendStylesheet(content, "file:///android_asset/services.css");
            if (nightMode) {
                appendStylesheet(content, "file:///android_asset/services-night.css");
            } else {
                appendStylesheet(content, "file:///android_asset/services-day.css");
            }
            if (displayLang.equals("EN")) {
                appendStylesheet(content, "file:///android_asset/services-en.css");
            } else if (displayLang.equals("GR")) {
                appendStylesheet(content, "file:///android_asset/services-gr.css");
            }

            if (fontSize.equals("Small")) {
                appendStylesheet(content, "file:///android_asset/font-small.css");
            } else if (fontSize.equals("Medium")) {
                appendStylesheet(content, "file:///android_asset/font-medium.css");
            } else if (fontSize.equals("Large")) {
                appendStylesheet(content, "file:///android_asset/font-large.css");
            }

//            content.appendElement("script")
//                    .attr("src", "file:///android_asset/alwb.js");
            content.appendElement("script")
                    .attr("src", "file:///android_asset/jquery-2.0.3.js");
            content.appendElement("script")
                    .attr("src", "file:///android_asset/services.js");

            if (tapLangSwap) {
                content.appendElement("script")
                        .attr("src", "file:///android_asset/langSwap.js");
            }

            if (showMusicLinks) {
                appendStylesheet(content, "file:///android_asset/fontawesome.min.css");
                appendStylesheet(content, "file:///android_asset/jquery.dropdown.css" );
                appendStylesheet(content, "file:///android_asset/media.css");

                content.appendElement("script")
                        .attr("src", "file:///android_asset/jquery.dropdown.js");

            } else {
                appendStylesheet(content, "file:///android_asset/media-hidden.css");
            }

            String html = content.outerHtml();
            // webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
            webView.addJavascriptInterface(this, "android");
            webView.loadDataWithBaseURL("https://agesinitiatives.com/dcs/public/dcs/", html, "text/html", "UTF-8", null);
        }

        @JavascriptInterface
        public void onData(String posValue) {
            Log.i(TAG, "JS value: " + posValue + " - " + serviceUrl + " - " + serviceTitle);
        }
    }
}
