package com.example.shiftly360;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PartnerBusinessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setText("COMING SOON!");
        textView.setTextSize(24);
        textView.setGravity(android.view.Gravity.CENTER);

        setContentView(textView);
    }
}
