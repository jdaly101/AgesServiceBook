package com.agesinitiatives.servicebook;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void goToDonatePage(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.agesinitiatives.org/support"));
        startActivity(browserIntent);
    }
}
