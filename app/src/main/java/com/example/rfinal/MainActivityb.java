package com.example.rfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivityb extends AppCompatActivity {

    SoundPool soundPool;
    TextView TV1,TV2, testv;
    ImageView IV1, IV2, IV3;
    Button BT1, BT2;
    ArrayList<String>  letterChinese = new ArrayList<String>();
    ArrayList<String>  letterEnglish = new ArrayList<String>();
    ArrayList<String>  letterNum = new ArrayList<String>();
    String[] letterAll = null;
    private int letterNo=0;


    int soundID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activityb);

        int times=0;
        TV1=findViewById(R.id.bTV1);
        TV2=findViewById(R.id.bTV2);
        IV1 = findViewById(R.id.bIV1);
        IV2 = findViewById(R.id.bIV2);
        IV3 = findViewById(R.id.bIV3);
        BT1 = findViewById(R.id.bBT1);
        BT2 = findViewById(R.id.bBT2);
        testv = findViewById(R.id.bTest);
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

        BT1.setVisibility(View.INVISIBLE);
        letterNo = -1;




        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 聲音來源, 左聲道音量, 右聲道音量, 優先級, 循環次數, 速率...速率最低0.5, 最高為2, 1代表正常速度
        TV1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        BT1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    letterNo--;
                    BT1.setVisibility(View.VISIBLE);
                    if(letterNo == 0) {
                        BT1.setVisibility(View.INVISIBLE);
                    }
                    GetLetter();
            }
        });
        BT2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(letterNo>=letterChinese.size()){
                    letterNo=0; //超過題目位址,回到初始值}
                }
                else{
                    letterNo++;
                    GetLetter();
                    BT1.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void GetLetter(){
        String[] letterlen = new String[letterNo]; //喧告字串陣列大小
        String uri = null; //圖片路徑和名稱
        String uri2 = null;
        String uri3 = null;
        IV1.setImageDrawable(null);
        IV2.setImageDrawable(null);
        IV3.setImageDrawable(null);
        if(Integer.parseInt(letterNum.get(letterNo))  % 2 == 1){
            uri = "@drawable/" + letterEnglish.get(letterNo) + Integer.parseInt(letterNum.get(letterNo));
            int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
            IV2.setImageResource(imageResource);
        }
        else{
            uri = "@drawable/" + letterEnglish.get(letterNo) + (Integer.parseInt(letterNum.get(letterNo)) - 1);
            uri2 = "@drawable/" + letterEnglish.get(letterNo) + Integer.parseInt(letterNum.get(letterNo));
            uri3 = "@drawable/arrowright";
            int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子
            int imageResource2 = getResources().getIdentifier(uri2, null, getPackageName()); //取得圖片Resource位子
            int imageResource3 = getResources().getIdentifier(uri3, null, getPackageName()); //取得圖片Resource位子
            IV1.setImageResource(imageResource);
            IV2.setImageResource(imageResource3);
            IV3.setImageResource(imageResource2);
        }
        letterlen = letterChinese.toArray(letterlen); //將List放到字串陣列裡來
        TV1.setText(letterlen[letterNo]);
        testv.setText(letterEnglish.get(letterNo));
        //TV2.setText(letterEnglish.get(letterNo));
        if( (letterEnglish.get(letterNo) == "zh") || (letterEnglish.get(letterNo) == "ch") || (letterEnglish.get(letterNo) == "sh") || (letterEnglish.get(letterNo) == "r")){
            TV2.setText("捲舌音");
        }

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
}