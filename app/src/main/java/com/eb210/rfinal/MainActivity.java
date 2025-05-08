package com.eb210.rfinal;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity<fontType> extends AppCompatActivity {
    TabHost TH1;
    ImageButton TB11, TB12, TB21, TB22;
    Button btn_agreement,resetbutton; // show agreement
    int consentAgree = 0; // 0 -> user not agree using agreement, 1 -> user agree using agreement
    boolean activityFlag = false;
    //中文,台語 語言選擇
    String selectedLanguage = "chinese"; // 默認中文

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int save_consentAgree = consentAgree;
        outState.putInt("ConsentAgree", save_consentAgree);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("state", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("state", "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("state", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("state", "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("state", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("state", "onDestroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        Log.v("state", "onCreate");
        // 要求錄音和儲存裝置的存取權限
        onRecordBtnClicked();

        // 狀態回復
//        if(savedInstanceState!=null){
//            int save_consentAgree = savedInstanceState.getInt("ConsentAgree");
//            consentAgree = save_consentAgree;
//            Log.v("test",String.valueOf(consentAgree));
//        }
//*********************************************
        pref = getSharedPreferences("person", MODE_PRIVATE);
        editor = pref.edit();
        boolean bool = pref.contains("person_agree");
        Log.v("test", String.valueOf(bool));
        //*--reset the AgreeConsent--*
//        editor.putInt("person_agree", 0);//putInt(label , value)
        //*----------------------*
        editor.commit();

        if(bool == true){//if pref have the label.
            consentAgree = pref.getInt("person_agree", 0);//putInt(label , address[0])-->get the preson_agree-label in the pref.
            Log.v("test", String.valueOf(consentAgree));
        }else{
            //nothing can be get.
            Log.v("test", String.valueOf(consentAgree));
        }
//*********************************************





        // 設定轉場動畫
        Explode explode = new Explode();
        explode.setDuration(3000);
        explode.setMode(Visibility.MODE_IN);
        getWindow().setReenterTransition(explode);

        // TabHost
        TH1 = findViewById(R.id.TH1);

        // ImageButtom
        TB11 = findViewById(R.id.IB11);
        TB12 = findViewById(R.id.IB12);
        TB21 = findViewById(R.id.IB21);
        TB22 = findViewById(R.id.IB22);







        // TabHost setup
        TH1.setup();
        TabHost .TabSpec TS;
        TS = TH1.newTabSpec("");
        TS.setContent(R.id.tab1);
        TS.setIndicator("課程選擇");
        TH1.addTab(TS);
        TS= TH1.newTabSpec("");
        TS.setContent(R.id.tab2);
        TS.setIndicator("其他功能");
        TH1.addTab(TS);

        // 同意書
        btn_agreement = (Button) findViewById(R.id.btn_agreement);
        btn_agreement.setVisibility(View.INVISIBLE);
        if(consentAgree == 1){//if the consentAgree is true,jump to the pre-test-Page.
//            Intent intent = new Intent(MainActivity.this, PreTestPage.class);
            activityFlag = true;
//            Intent intent = new Intent(MainActivity.this, FreqLinearMapping.class);
//            startActivity(intent);
        }else{//In Else situation,jump out a Dialog-consentAgree, before jump to the pre-test-Page.
            agreement();
        }

        btn_agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agreement();
            }
        });

        TB11.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("請選擇您的課程");
                String items[] = {"中文","台語", "動物", "日常用品"}; //-1代表沒有條目被選中 綜合改george
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 1.把選中的條目取出來
                        String item = items[which];
                        // 根據選擇設定語言
                        if (item.equals("中文")) {
                            selectedLanguage = "chinese";
                        } else if (item.equals("台語")) {
                            selectedLanguage = "tailo";
                        }

                        Toast.makeText(getApplicationContext(),item.toString(),Toast.LENGTH_LONG).show();
                        // 2.然後把對話框關閉
                        dialog.dismiss();
                        Intent intent =new Intent(MainActivity.this  ,  MainActivitya.class);
//                        Intent intent =new Intent(MainActivity.this  ,  MainActivity2.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("theme", item);
                        bundle.putString("language", selectedLanguage); // 傳遞語言選擇
                        intent.putExtras(bundle);
//
                        startActivity(intent);
//                        finish();
                    } }); //一樣要show
                builder.show();
            }
        });
        TB12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this  ,  MainActivityb.class);
                startActivity(intent);
            }
        });
        TB22.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Intent intent =new Intent(MainActivity.this  ,  MainActivityd.class);
                Intent intent =new Intent(MainActivity.this  ,  FreqLinearMapping.class);
                startActivity(intent);
            }
        });

        TB21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this  ,  MainActivityc.class);
                startActivity(intent);
            }
        });



        resetbutton = findViewById(R.id.ResetBt);
        resetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt("person_agree", 0);
                editor.commit();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String json = sharedPreferences.getString("gainvalue", null);
        if (json != null) {
            Gson gson = new Gson();
            FreqLinearMapping.gain_value = gson.fromJson(json, double[].class);
        }

    }
    // 文檔讀入
    private String loadTextFile(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[4096];
        int len = 0;
        while ((len = inputStream.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, len);
        }
        return new String(byteArrayOutputStream.toByteArray(), "UTF8");
    }

    // 同意書
    private void agreement(){
        AssetManager assetManager = getAssets();
        InputStream inputStream;
        String text = null;
        try {
            inputStream = assetManager.open("consent.txt");
            text = loadTextFile(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //builder.setIcon(R.drawable.bear)
        builder.setTitle("語音蒐集同意書")
                .setMessage(text)
                .setPositiveButton("我了解並同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //*********************************************
                        //when Agree-Button be pressed,change the person_agree-label to value-true.
                        editor.putInt("person_agree", 1);//if setPositiveButton enable,then put the value-1 into person_agree
                        editor.commit();
                        //*********************************************
//                        consentAgree = 1;
                        btn_agreement.setVisibility(View.INVISIBLE);

                        //**----------**
                        consentAgree = pref.getInt("person_agree", 0);//get the value of address[0] of person_agree-label
                        if(consentAgree == 1){//if the consentAgree is true,jump to the pre-test-Page.
//                            Intent intent = new Intent(MainActivity.this, PreTestPage.class);
                            activityFlag = true;
//                            Intent intent = new Intent(MainActivity.this, FreqLinearMapping.class);
//                            startActivity(intent);
                        }
                        //**----------**
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //*********************************************
                        editor.putInt("person_agree", 0);
                        editor.commit();
                        //*********************************************
//                        consentAgree = 0;
                        btn_agreement.setVisibility(View.VISIBLE);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Get the alert dialog buttons reference
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        //Change the alert dialog buttons text and background color
        positiveButton.setTextColor(Color.parseColor("#FF000000"));
        positiveButton.setBackgroundColor(Color.parseColor("#ffdbdbdb"));
        negativeButton.setTextColor(Color.parseColor("#FF000000"));
        negativeButton.setBackgroundColor(Color.parseColor("#ffdbdbdb"));

        // [排版] positiveButton negativeButton 之間加上間隔
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20,0,0,0);
        positiveButton.setLayoutParams(params);
    }

    //要求權限
    public void onRecordBtnClicked() {
        boolean audioHasGone = checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;

        boolean storageHasGone = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions;
            if (!audioHasGone && !storageHasGone) {//如果兩個權限都未取得
                permissions = new String[2];
                permissions[0] = Manifest.permission.RECORD_AUDIO;
                permissions[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            } else if (!audioHasGone) {//如果只有相機權限未取得
                permissions = new String[1];
                permissions[0] = Manifest.permission.RECORD_AUDIO;
            } else if (!storageHasGone) {//如果只有存取權限未取得
                permissions = new String[1];
                permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            } else {
                Toast.makeText(MainActivity.this, "錄音權限已取得\n儲存權限已取得", Toast.LENGTH_LONG).show();
                return;
            }
            requestPermissions(permissions, 100);
        }
    }
}