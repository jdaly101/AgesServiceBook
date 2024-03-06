package com.agesinitiatives.servicebook;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class AboutActivity extends AppCompatActivity {
    private static final String ABOUT_URL = "https://dcs.goarch.org/goa/dcs/and/about_app.html";
    private static final String TAG = "AboutView";
    private WebView webView;

    private DocLoader docLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        WebView.setWebContentsDebuggingEnabled(true);
        webView = findViewById(R.id.about_inner_webview);
        webView.loadUrl("https://dcs.goarch.org/goa/dcs/and/about_app.html");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        docLoader = new DocLoader();
        docLoader.execute();
    }

    private class DocLoader extends AsyncTask<Void, Void, Void> {
        Document doc;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Connection.Response response = Jsoup.connect(AboutActivity.ABOUT_URL).timeout(10000).execute();
                if (response.statusCode() == 200) {
                    doc = response.parse();
                    doc.head().getElementsByTag("script").remove();
                }

            } catch (IOException e) {
//                Log.e(TAG, e.toString());
            }
            return null;
        }

        @SuppressLint("JavascriptInterface")
        @Override
        protected void onPostExecute(Void result) {
            Element content = doc.select("html").first();
            Element innerContent = doc.select(".content").first();
            innerContent.appendElement("script")
                    .attr("src", "file:///android_asset/about.js");

            String html = content.outerHtml();
            webView.addJavascriptInterface(this, "android");
            webView.loadDataWithBaseURL("https://dcs.goarch.org/goa/dcs/", html, "text/html", "UTF-8", null);
        }

        @JavascriptInterface
        public void onData(String posValue) {
            Log.i(TAG, "foo");
        }
    }
}
