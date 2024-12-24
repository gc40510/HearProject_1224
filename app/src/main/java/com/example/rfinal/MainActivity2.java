package com.example.rfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.maple.recorder.recording.AudioChunk;
import com.maple.recorder.recording.AudioRecordConfig;
import com.maple.recorder.recording.MsRecorder;
import com.maple.recorder.recording.PullTransport;
import com.maple.recorder.recording.Recorder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity2 extends AppCompatActivity {
    int recordStatus = 0;
    // recordStatus
    // 0 -> 開始錄音
    // 1 -> 錄音結束

    private int fruitNo = 0;
    // 儲存現在的題目編號


    // 題目
    String[] FruitAll = null;
    ArrayList<String> FruitChineseOrigin= new ArrayList<String>();
    ArrayList<String>  FruitEnglishOrigin= new ArrayList<String>();
    ArrayList<String>  FruitChinese= new ArrayList<String>();
    ArrayList<String>  FruitEnglish= new ArrayList<String>();

    // 各個元件
    ImageView IV1, IV3;

    Button BT1, BT2, BT3,bb;

    Button repeat_audio_btn;
    // repeat_audio_btn
    // -> repeat record voice when button be clicked

    Button show_result_btn;
    // show_result_btn
    // -> 顯示測試結果

    TextView TV1;
    TextView T1;
    TextView atestV;

    // 特效 (還沒搞懂)
    Animation circle_anim;

    // firebase
    String nowwav;
    String firebase_nowwavname = "start_test";
    String firebase_nowwavname_ch;
    String myref = "";
    String serious_word = "";
    String standard_wav = "";
    String user_wav = "";
    boolean standard_state = false;
    boolean user_state = false;
    /*
    移除firebase支援
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://finalt01.appspot.com");
    StorageReference storageRef = storage.getReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://finalt01-default-rtdb.asia-southeast1.firebasedatabase.app/");
    */

    // 錄音按鍵/撥放按鍵
    GifImageView IV2;
    // 音檔站存資料夾
    File audio_file;
    // 錄音器
    Recorder recorder;
    // 撥放器
    SoundPool soundPool;
    int soundID;

    // 其他
    final Context context = MainActivity2.this;
    String server_state = "0";
    Handler handler = new Handler();
    Handler handler1 = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().hide();

        // 讀取MainActivity傳入的值並讀檔
        Bundle bundle = getIntent().getExtras();
        String theme = bundle.getString("theme");
        Map<String, String> thememap = new HashMap<>();
        thememap.put("水果", "fruit.txt");
        thememap.put("動物", "animal.txt");
        thememap.put("日常用品", "daily.txt");
        IV1 = findViewById(R.id.aIV1);
        IV2 = findViewById(R.id.aIV2);
        IV3 = findViewById(R.id.aIV3);
        BT1 = findViewById(R.id.aBT1);
        BT2 = findViewById(R.id.aBT2);
        BT3 = findViewById(R.id.aBT3);
        repeat_audio_btn = (Button) findViewById(R.id.repeat);
        show_result_btn = (Button) findViewById(R.id.show_result_btn);
        T1 = findViewById(R.id.Ta1);
        TV1 = findViewById(R.id.aTV1);
        atestV = findViewById(R.id.atestv);
//        IV2.setImageResource(getResources().getIdentifier("@drawable/microphone", null, getPackageName()));
//        IV3.setImageResource(getResources().getIdentifier("@drawable/clockbackground", null, getPackageName()));
        IV2.setImageDrawable(getResources().getDrawable(R.drawable.microphone));
        IV3.setImageDrawable(getResources().getDrawable(R.drawable.clockbackground));
        T1 = findViewById(R.id.Ta1);
        bb = findViewById(R.id.bbb);
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent9 = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent9);
            }
        });


        // 取得題目
        AssetManager assetManager = getAssets();
        InputStream inputStream;
        try {
            inputStream = assetManager.open(thememap.get(theme));
            String text = loadTextFile(inputStream);
            FruitAll = text.split("\n| ");
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 打亂題目順序
        FruitChineseOrigin = new ArrayList<String>();
        FruitEnglishOrigin = new ArrayList<String>();
        for(int i = 0; i < FruitAll.length; i++){
            if(i % 2 == 0){
                FruitChineseOrigin.add(FruitAll[i].trim());
            }
            else{
                FruitEnglishOrigin.add(FruitAll[i].trim());//trim是去掉空格
            }
        }
        // 設定firebase
        // users
        // 0 -> 測試資料還沒準備好
        // 1 -> 測試資料已經準備好
        try {
            /*
            取消firebase的使用
            DatabaseReference myRef = database.getReference("users");
            myRef.setValue(0);
             */
            nowwav = "start_test";
        } catch (Exception e) {
            Log.e("TTT1", "error0 real date base users value set");
        }

        try {
            ArrayRandom();
        } catch (Exception e) {
            Log.e("TTT1", "ArrayRandom Error");
        }

        // 確保題目音檔已經仔入完成
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        IV1.setImageResource(getResources().getIdentifier("@drawable/start", null, getPackageName()));
        IV1.setImageDrawable(getResources().getDrawable(R.drawable.start));

        //遊戲說明
        // custom dialog
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View alert = layoutInflater.inflate(R.layout.gameillu, null);
        AlertDialog.Builder scn = new AlertDialog.Builder(context);
        scn.setView(alert);
        AlertDialog SCN = scn.show();
        Button btn_OK = (Button)alert.findViewById(R.id.gBT1);
        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SCN.dismiss();
            }
        });

        //黑板上的圖片
        IV1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 播放題目音檔
                GetFirstFruit();
                recordStatus = 0;
            }
        });
        //錄音按鈕
        IV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordStatus == 0) {
                    // 錄音流程
                    try {
                        recordStatus++;
                        recordStatus %= 2;
//                        IV2.setImageResource(getResources().getIdentifier("@drawable/pause", null, getPackageName()));
                        IV2.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                        recorder = MsRecorder.wav(
                                new File(getFilesDir() + "/abc.wav"),
                                new AudioRecordConfig(),
                                new PullTransport.Default()
                                        .setOnAudioChunkPulledListener(new PullTransport.OnAudioChunkPulledListener() {
                                            @Override
                                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                                Log.e("數據", "amplitude: " + audioChunk.maxAmplitude());
                                            }
                                        })
                        );
                        recorder.startRecording(); // 開始

                    } catch (Exception e) {
                        Log.e("TTT1", "error1 開始錄音");
                    }
                }
                else if(recordStatus == 1) {
                    // 停止錄音
                    try {
                        recorder.pauseRecording(); // 暂停
                        //recorder.resumeRecording(); // 重新開始
                        recorder.stopRecording(); // 結束
//                        IV2.setImageResource(getResources().getIdentifier("@drawable/loading", null, getPackageName()));
                        IV2.setImageDrawable(getResources().getDrawable(R.drawable.loading));
                    } catch (Exception e) {
                        //Toast.makeText(context,"FileIOException", Toast.LENGTH_SHORT).show();
                        //e.printStackTrace();
                        Log.e("TTT1", "error2 停止錄音");
                    }

                    try {
                        audio_file = new File(getFilesDir() + "/abc.wav");
                        Uri file = Uri.fromFile(audio_file);

                        //add new code here


                        // Log.v("test", audio_file.getAbsolutePath());
                        /*
                        取消Firebase使用

                        StorageReference riversRef = storageRef.child(file.getLastPathSegment());
                        UploadTask uploadTask = riversRef.putFile(file);

                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...
                                try {
                                    DatabaseReference myRef = database.getReference("users");
                                    myRef.setValue(1);
                                    DatabaseReference myRef2 = database.getReference("nowwav");
                                    myRef2.setValue(firebase_nowwavname);
                                    DatabaseReference myRef4 = database.getReference("nowwav_ch");
                                    myRef4.setValue(firebase_nowwavname_ch);
                                    DatabaseReference rr = database.getReference("check_server");
                                    rr.setValue(1);
                                    server_state = "1";
                                } catch (Exception e) {
                                    Log.e("TTT1", "error myRef.setValue(1)");
                                }
                            }
                        });
                        */

                    } catch (Exception e) {
                        Log.e("TTT1", "error 3");
                    }

                    recordStatus++;
                    recordStatus %= 2;
                    BT3.setVisibility(View.VISIBLE);
                    repeat_audio_btn.setVisibility(View.VISIBLE);
                    show_result_btn.setVisibility(View.VISIBLE);
                    IV2.setVisibility(View.INVISIBLE);
//                    IV2.setImageResource(getResources().getIdentifier("@drawable/microphone", null, getPackageName()));
                    IV2.setImageDrawable(getResources().getDrawable(R.drawable.microphone));
                }
            }
        });

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

    // 撥放音檔
    private void GetFirstFruit(){
        String uri = "@drawable/" + FruitEnglish.get(fruitNo); //圖片路徑和名稱
        String uri2 = "@raw/" + FruitEnglish.get(fruitNo) + "0"; //音檔路徑和名稱
        nowwav = FruitEnglish.get(fruitNo) + "0.wav";
        firebase_nowwavname = FruitEnglish.get(fruitNo);
        firebase_nowwavname_ch = FruitChinese.get(fruitNo);
        T1.setText(" ");
        int imageResource = getResources().getIdentifier(uri, null, getPackageName()); //取得圖片Resource位子

        int soundIDResource = getResources().getIdentifier(uri2, null, getPackageName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        try{
            soundID = soundPool.load(this, soundIDResource, 1);
            atestV.setText("有聲音");
        } catch (Exception e) {
            atestV.setText("沒聲音");
            e.printStackTrace();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        soundPool.play(soundID, 1, 1, 1, 0, 1);
        IV1.setImageResource(imageResource);
        TV1.setText(FruitChinese.get(fruitNo));
        atestV.setText("第"+ (fruitNo + 1) + "題");
//        IV2.setImageResource(getResources().getIdentifier("@drawable/microphone", null, getPackageName()));
        IV2.setImageDrawable(getResources().getDrawable(R.drawable.microphone));

        recordStatus = 0;
    }

    // repeat record voice
    private void repeatAudio(String url){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        try{
            soundID = soundPool.load(url, 1);
            atestV.setText("有聲音");
        } catch (Exception e) {
            atestV.setText("沒聲音");
            e.printStackTrace();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        soundPool.play(soundID, 1, 1, 1, 0, 1);

    }
    // 圓角轉換函式，帶入Bitmap圖片及圓角數值則回傳圓角圖，回傳Bitmap再置入ImageView
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx)
    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    // 將播放的順序打亂
    private void ArrayRandom(){
        int n = FruitChineseOrigin.size();
        ArrayList<Integer> RandomIndex = new ArrayList<Integer>(n);
        for(int i = 0; i < n; i++){
            RandomIndex.add(i);
        }
        Collections.shuffle(RandomIndex);
        for(int i = 1; i < n; i++){
            FruitChinese.add(FruitChineseOrigin.get(RandomIndex.get(i)));
            FruitEnglish.add(FruitEnglishOrigin.get(RandomIndex.get(i)));
        }
    }
    //等待旋轉
    private void startRot(){
        circle_anim = AnimationUtils.loadAnimation(this, R.anim.anim_round_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        circle_anim.setInterpolator(interpolator);
        if (circle_anim != null) {
            IV2.startAnimation(circle_anim);  //开始动画
        }
    }
}