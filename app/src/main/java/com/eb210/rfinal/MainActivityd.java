package com.eb210.rfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivityd extends AppCompatActivity {

    TextView T1;
    ImageView p1,p2,p3,p4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activityd);
        getSupportActionBar().hide();

        p1 = findViewById(R.id.puzzle01);
        p2 = findViewById(R.id.puzzle02);
        p3 = findViewById(R.id.puzzle03);
        p4 = findViewById(R.id.puzzle04);

//        p1.setImageResource(getResources().getIdentifier("@drawable/streetbro_4", null, getPackageName()));
//        p2.setImageResource(getResources().getIdentifier("@drawable/bag_2", null, getPackageName()));
//        p3.setImageResource(getResources().getIdentifier("@drawable/raccoon_3", null, getPackageName()));
//        p4.setImageResource(getResources().getIdentifier("@drawable/sleep_1", null, getPackageName()));
    }
}