package com.bw.views.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bw.views.chooseview.ChooseItem;
import com.bw.views.chooseview.ChooseView;
import com.bw.views.roundseekbar.RoundSeekBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.tvMain);
        RoundSeekBar seekBar = findViewById(R.id.rsbMain);
        seekBar.setOnSeekBarChangeListener((progress, fromUser) -> textView.setText("Hello World! " + progress));

        ChooseView chooseView = findViewById(R.id.cvMain);
        List<ChooseItem> chooseItems=new ArrayList<>();
        chooseItems.add(new ChooseItem(0,R.mipmap.icon_80_seat_heating_left_n));
        chooseItems.add(new ChooseItem(1,R.mipmap.icon_80_seat_heating_left_s1));
        chooseItems.add(new ChooseItem(2,R.mipmap.icon_80_seat_heating_left_s2));
        chooseItems.add(new ChooseItem(3,R.mipmap.icon_80_seat_heating_left_s3));
        chooseView.setChooseItems(chooseItems);
        chooseView.setOnItemChooseListener(chooseItem ->
                Toast.makeText(MainActivity.this, "选择了"+chooseItem.getId(), Toast.LENGTH_SHORT).show());


        ImageView ivMain = findViewById(R.id.ivMain);
        ivMain
                .setOnClickListener(view -> {
            Toast.makeText(this, "Hello World!", Toast.LENGTH_SHORT).show();
        });


    }
}