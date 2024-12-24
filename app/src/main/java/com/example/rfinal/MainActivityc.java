package com.example.rfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class  MainActivityc extends AppCompatActivity {

    TextView fa,fb,fc, aa,ab,ac, da,db,dc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activityc);
        getSupportActionBar().hide();

        fa = findViewById(R.id.score_fa);
        fb = findViewById(R.id.score_fb);
        fc = findViewById(R.id.score_fc);
        aa = findViewById(R.id.score_aa);
        ab = findViewById(R.id.score_ab);
        ac = findViewById(R.id.score_ac);
        da = findViewById(R.id.score_da);
        db = findViewById(R.id.score_db);
        dc = findViewById(R.id.score_dc);



    }


}