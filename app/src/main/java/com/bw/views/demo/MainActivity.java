package com.bw.views.demo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bw.views.roundseekbar.RoundSeekBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.tvMain);
        RoundSeekBar seekBar = findViewById(R.id.rsbMain);
        seekBar.setOnSeekBarChangeListener((progress, fromUser) -> textView.setText("Hello World! " + progress));
    }
}