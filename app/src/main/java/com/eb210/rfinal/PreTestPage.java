package com.eb210.rfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageButton;





public class PreTestPage extends AppCompatActivity {

    GifImageButton gif_btn,img_btn1,img_btn2,img_btn3;
    Button btn_l,btn_n;
    TextView title,btv2,tv0,btnResult;

    private int letterNo=0;
    ArrayList<String> letterChinese = new ArrayList<String>();
    ArrayList<String>  letterEnglish = new ArrayList<String>();
    ArrayList<String>  letterNum = new ArrayList<String>();
    String[] letterAll = null;
    int cnt = 0, btn_frq = 100, flag_frq = 0, temp_frq = 0;
    boolean findFrq = false;

    // 撥放器
    SoundPool soundPool;
    int soundID;
//    boolean pressed = false;
    boolean[] getButton = new boolean[]{false, false, false};


    //+-+-+-+-+-+--+-+-+-+
//    ArrayList<String>  FruitEnglishOrigin= new ArrayList<String>();
//    ArrayList<String>  FruitEnglish= new ArrayList<String>();
//    SoundPool soundPool;
//    int soundID;
//    int recordStatus = 0;
//    //private int fruitNo = 0;
//    String nowwav;
//    FirebaseDatabase database = FirebaseDatabase.getInstance("https://finalt01-default-rtdb.asia-southeast1.firebasedatabase.app/");
//    String firebase_nowwavname = "start_test";
//    String firebase_nowwavname_ch;
    //+-+-+-+-+-+--+-+-+-+




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_test_page);

        gif_btn = findViewById(R.id.gifImageButton);
        img_btn1 = findViewById(R.id.img_b1);
        img_btn2 = findViewById(R.id.img_b2);
        img_btn3 = findViewById(R.id.img_b3);
        btn_l = findViewById(R.id.btn_last);
        btn_n = findViewById(R.id.btn_next);
        title = findViewById(R.id.TV_title);
        btv2 = findViewById(R.id.bTV2);
        tv0 = findViewById(R.id.TV0);
        btnResult = findViewById(R.id.resultBTN);
        gif_btn.setImageResource(getResources().getIdentifier("@drawable/play", null, getPackageName()));
        //*****************************************
        img_btn1.setImageResource(getResources().getIdentifier("@drawable/orange", null, getPackageName()));
        img_btn2.setImageResource(getResources().getIdentifier("@drawable/orange", null, getPackageName()));
        img_btn3.setImageResource(getResources().getIdentifier("@drawable/orange", null, getPackageName()));
        //*****************************************



        AssetManager assetManager = getAssets();
        InputStream inputStream;
        String text = null;
        try {
            inputStream = assetManager.open("letter.txt");
            text = loadTextFile(inputStream);
            letterAll = text.split("\n| ");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        int times=0;
        letterChinese = new ArrayList<String>();
        letterEnglish = new ArrayList<String>();
        letterNum = new ArrayList<String>();
        for(int i = 0; i < letterAll.length; i++){
            if(i % 3 == 0){
                letterChinese.add(letterAll[i].trim());
            }
            else if(i % 3 == 1){
                letterEnglish.add(letterAll[i].trim());//trim是去掉空格
            }
            else{
                letterNum.add(letterAll[i].trim());
            }
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        gif_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cnt++;

//                int temp = 1000;
                if(cnt % 2 == 1){
                    gif_btn.setImageResource(getResources().getIdentifier("@drawable/stop", null, getPackageName()));
                    frequencyAudio(String.valueOf(btn_frq));//play the audio of frequency
                    tv0.setText(String.valueOf(btn_frq) + " Hz");
                }else{
                    soundPool.stop(soundID);//stop the audio of frequency
//                    frequencyAudio(String.valueOf(temp));
                    gif_btn.setImageResource(getResources().getIdentifier("@drawable/play", null, getPackageName()));

                }

//                soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
//                soundID = soundPool.load("raw/sinusoid1000.wav", 1);
//                soundPool.play(soundID, 1, 1, 1, 0, 1);




            }
        });




        btn_l.setVisibility(View.INVISIBLE);
        letterNo = -1;

        btn_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                pressed = false;
                letterNo--;
                btn_l.setVisibility(View.VISIBLE);
                if(letterNo == 1){
                    btn_l.setVisibility(View.INVISIBLE);
                }
                changedPage();
                soundPool.stop(soundID);
                //+-+-+-+-+-+--++
//                GetFirstFruit();
                //+-+-+-+-+-+--++
                btn_frq -= temp_frq;

            }
        });


        btn_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                pressed = false;
                //*****************************
                //Use the boolean-Array to save the which img_btn be selected.
                //*****************************
                gif_btn.setImageResource(getResources().getIdentifier("@drawable/play", null, getPackageName()));

                if(letterNo>=letterChinese.size()){
                    letterNo=0; //超過題目位址,回到初始值}
                }
                else{
                    letterNo++;
                    changedPage();
                    //+-+-+-+-+-+--++
//                    GetFirstFruit();
                    //+-+-+-+-+-+--++
                    btn_l.setVisibility(View.VISIBLE);
                }
                soundPool.stop(soundID);

                //****----****
                if(findFrq == false && flag_frq == 0){
                    btn_frq += 100;//clear
                    temp_frq = 100;
                }else if (findFrq == false && flag_frq == 1){
                    btn_frq += 50;//little
                    temp_frq = 50;
                }else if(findFrq = true || flag_frq == 0){
                    btn_frq += 0;//None
                    temp_frq = 0;
                    findFrq = true;
                }else if(findFrq == false){
                    btn_frq += 100;//other
                    temp_frq = 100;
                }
                tv0.setText(String.valueOf(btn_frq) + " Hz");


            }
        });

        img_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_frq = 0;
                //reduce the frequency range
                img_btn1.setImageResource(getResources().getIdentifier("@drawable/green", null, getPackageName()));
                img_btn2.setImageResource(getResources().getIdentifier("@drawable/orange", null, getPackageName()));
                img_btn3.setImageResource(getResources().getIdentifier("@drawable/orange", null, getPackageName()));
                getButton[0] = true;
                getButton[1] = false;
                getButton[2] = false;

//                if(pressed == false){
//                    img_btn1.setImageResource(getResources().getIdentifier("@drawable/green", null, getPackageName()));
//                    pressed = true;
//                }


            }
        });

        img_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_frq = 1;
                //expand the less frequency range
                img_btn1.setImageResource(getResources().getIdentifier("@drawable/orange", null, getPackageName()));
                img_btn2.setImageResource(getResources().getIdentifier("@drawable/green", null, getPackageName()));
                img_btn3.setImageResource(getResources().getIdentifier("@drawable/orange", null, getPackageName()));
                getButton[0] = false;
                getButton[1] = true;
                getButton[2] = false;
//                if(pressed == false){
//                    img_btn2.setImageResource(getResources().getIdentifier("@drawable/green", null, getPackageName()));
//                    pressed = true;
//                }

            }
        });

        img_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_frq = 2;
                //expand the more frequency range
                img_btn1.setImageResource(getResources().getIdentifier("@drawable/orange", null, getPackageName()));
                img_btn2.setImageResource(getResources().getIdentifier("@drawable/orange", null, getPackageName()));
                img_btn3.setImageResource(getResources().getIdentifier("@drawable/green", null, getPackageName()));
                getButton[0] = false;
                getButton[1] = false;
                getButton[2] = true;

                //******-----------************
                btnResult.setText("Find the Upper-Bound.");
//                if(pressed == false){
//                    img_btn3.setImageResource(getResources().getIdentifier("@drawable/green", null, getPackageName()));
//                    pressed = true;
//                }

            }
        });


        //+-+-+-+-++--+-+-+-+-+-+--+-+-+---+

//        try {
//            DatabaseReference myRef = database.getReference("users");
//            myRef.setValue(0);
//            nowwav = "start_test";
//        } catch (Exception e) {
//            Log.e("TTT1", "error0 real date base users value set");
//        }
//
//
//        gif_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GetFirstFruit();
//                recordStatus = 0;
//            }
//        });





    }

    private void changedPage(){
        img_btn1.setImageResource(getResources().getIdentifier("@drawable/orange", null, getPackageName()));
        img_btn2.setImageResource(getResources().getIdentifier("@drawable/orange", null, getPackageName()));
        img_btn3.setImageResource(getResources().getIdentifier("@drawable/orange", null, getPackageName()));

    }


    private String loadTextFile(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[4096];
        int len = 0;
        while ((len = inputStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, len);
        }
        return new String(byteArrayOutputStream.toByteArray(), "UTF8");
    }

//    // 撥放音檔
//private void repeatAudio(String url){
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        soundPool = new SoundPool.Builder()
//                .setMaxStreams(10)
//                .build();
//    } else {
//        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
//    }
//
//    try{
//        soundID = soundPool.load(url, 1);
////        atestV.setText("有聲音");
//    } catch (Exception e) {
////        atestV.setText("沒聲音");
//        e.printStackTrace();
//    }
//    try {
//        Thread.sleep(500);
//    } catch (InterruptedException e) {
//        e.printStackTrace();
//    }
//    soundPool.play(soundID, 1, 1, 1, 0, 1);
//
//}




    private void frequencyAudio(String audioNum){

        String uri = "@raw/" + "sinusoid" + audioNum; //音檔路徑和名稱


        int soundIDResource = getResources().getIdentifier(uri, null, getPackageName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        try{
            soundID = soundPool.load(this, soundIDResource, 1);

        } catch (Exception e) {

            e.printStackTrace();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        soundPool.play(soundID, 1, 1, 1, 0, 1);


    }






}