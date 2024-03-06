package com.agesinitiatives.servicebook;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy);

        TextView textView = findViewById(R.id.privacyTextView);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

}