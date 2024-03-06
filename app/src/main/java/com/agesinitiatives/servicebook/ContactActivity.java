package com.agesinitiatives.servicebook;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.agesinitiatives.servicebook.databinding.ActivityContactBinding;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ContactActivity extends AppCompatActivity {

    private static final String CONTACT_URL = "https://dcs.goarch.org/goa/dcs/and/contact_app.html";
    private static final String TAG = "ContactView";
    private WebView webView;

    private DocLoader docLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        WebView.setWebContentsDebuggingEnabled(true);
        webView = findViewById(R.id.contact_webview);
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
                Connection.Response response = Jsoup.connect(ContactActivity.CONTACT_URL).timeout(10000).execute();
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