package com.example.rfinal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.maple.recorder.recording.AudioChunk;
import com.maple.recorder.recording.AudioRecordConfig;
import com.maple.recorder.recording.MsRecorder;
import com.maple.recorder.recording.PullTransport;
import com.maple.recorder.recording.Recorder;

import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import pl.droidsonroids.gif.GifImageView;

import java.io.FileNotFoundException;


public class MainActivitya extends AppCompatActivity {

    int recordStatus = 0;
    // recordStatus
    // 0 -> 開始錄音
    // 1 -> 錄音結束

    private int fruitNo = 0;
    // 儲存現在的題目編號


    // 題目
    String[] FruitAll = null;
    ArrayList<String>  FruitChineseOrigin= new ArrayList<String>();
    ArrayList<String>  FruitEnglishOrigin= new ArrayList<String>();
    ArrayList<String>  FruitChinese= new ArrayList<String>();
    ArrayList<String>  FruitEnglish= new ArrayList<String>();

    // 各個元件
    ImageView IV1, IV3;
    ImageView img_pad;
    Button BT1, BT2, BT3,bb;

    RadioGroup soundbtngroup;

    ProgressDialog progressDialog;

    Button repeat_audio_btn;
    // repeat_audio_btn
    // -> repeat record voice when button be clicked

    Button show_result_btn;
    // show_result_btn
    // -> 顯示測試結果
    Button show_mouth_tech_btn;
    // -> 顯示嘴型技巧



    TextView TV1;
    TextView T1;
    TextView atestV;

    // 特效 (還沒搞懂)
    Animation circle_anim;

    // firebase
    String nowwav;
    String firebase_nowwavname = "start_test";
    String firebase_nowwavname_ch;
    String standard_wav = "";
    String user_wav = "";
    String server_DTW = "";
    JSONObject diff;

    FrequencyAdjuster frequencyAdjuster = new FrequencyAdjuster();

    TrustManager[] trustAllCertificates = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
    };

    HostnameVerifier hostnameVerifier = (hostname, session) -> true;

    private class NetworkRequestTask extends AsyncTask<Void, Void, String> {
        private String url;
        private String wavcontent;
        private String novwav;
        private File file;

        public NetworkRequestTask(String url, String wavcontent, String novwav, File file) {
            this.url = url;
            this.wavcontent = wavcontent;
            this.novwav = novwav;
            this.file = file;
        }
        @Override
        protected String doInBackground(Void... voids) {
            try {

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCertificates, null);

                // 创建URL对象
                URL apiUrl = new URL(url);
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
                HttpsURLConnection connection = (HttpsURLConnection) apiUrl.openConnection();

                // 设置请求方法为POST
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + generateBoundary());
                //connection.setRequestProperty("Content-Type", "multipart/form-data");

                // 启用输入输出流
                connection.setDoOutput(true);

                //把要POST的內容先LOG到LOGCAT
                Log.d("HTTP","wavcontent="+wavcontent);
                Log.d("HTTP","nowwav="+novwav);

                // 获取输出流
                try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                    writeTextParameter(os, "wavcontent", wavcontent);
                    writeTextParameter(os, "nowwav", novwav);
                    writeFileParameter(os, "file", file);
                    //os.writeBytes("--" + generateBoundary() + "\r\n");
                    os.writeBytes("--" + generateBoundary() + "--");
                }

                // 获取响应
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        return response.toString();
                    }
                } else {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        //return "HTTP Error: " + responseCode + "\n" + response.toString();
                    }
                    return "HTTP Error: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null; // 或者返回错误信息
            }
        }
        private String generateBoundary() {
            // 生成一个随机的boundary字符串
            //return "---------------------------" + System.currentTimeMillis();
            return "-------------pekopeko";
        }
        private void writeTextParameter(DataOutputStream os, String name, String value) throws IOException {
            String boundary = generateBoundary();
            String lineEnd = "\r\n";
            StringBuilder parameter = new StringBuilder();
            parameter.append("--").append(boundary).append(lineEnd);
            parameter.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(lineEnd);
            parameter.append(lineEnd);
            parameter.append(value).append(lineEnd);
            Log.d("HTTP", "Request: " + parameter);
            os.write(parameter.toString().getBytes("UTF-8"));
        }

        private void writeFileParameter(DataOutputStream os, String name, File file) throws IOException {
            os.writeBytes("--" + generateBoundary() + "\r\n");
            os.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + file.getName() + "\"\r\n");
            os.writeBytes("Content-Type: application/octet-stream\r\n\r\n");

            // 写入文件内容
            byte[] buffer = new byte[4096];
            int bytesRead;
            try (FileInputStream fis = new FileInputStream(file)) {
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            os.writeBytes("\r\n");
        }
        @Override
        protected void onPostExecute(String result) {
            // 处理响应数据，例如更新UI
            Log.d("HTTP Request", result);
            try {
                JSONObject resultjson = new JSONObject(result);
                user_wav = resultjson.getString("kaldi");
                standard_wav = resultjson.getString("standard");
                server_DTW = resultjson.getString("dist");
                diff = resultjson.getJSONObject("diff"); //注音部分的jsonobject

                // 將 diff 儲存到檔案
                File directory = new File(context.getFilesDir(), "custom_folder");
                if (!directory.exists()) {
                    directory.mkdirs(); // 創建資料夾
                }
                File file = new File(directory, "diff.json");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(diff.toString().getBytes());
                fos.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace(); // 或者處理異常的邏輯
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
            //String[] result_words = result.split("<br>");
            //user_wav = result_words[0].split(":")[1].trim();
            //standard_wav = result_words[1].split(":")[1].trim();
            //server_DTW = result_words[2].split(":")[1].trim();
            progressDialog.dismiss();
            show_result_btn.callOnClick();
        }
    }

    /*
    刪除firebase部分的code
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
    AudioTrack audioTrack;
    int soundID;

    // 其他
    final Context context = MainActivitya.this;
    String server_state = "0";
    Handler handler = new Handler();
    Handler handler1 = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activitya);
        getSupportActionBar().hide();

        // 讀取MainActivity傳入的值並讀檔
        Bundle bundle = getIntent().getExtras();
        String theme = bundle.getString("theme");
        Map<String, String> thememap = new HashMap<>();
        thememap.put("綜合", "george.txt"); //改成自己錄的音檔
        thememap.put("水果", "fruit.txt");
        thememap.put("動物", "animal.txt");
        thememap.put("日常用品", "daily.txt");

        int loadingImage = getResources().getIdentifier("@drawable/loading", null, getPackageName());
        IV1 = findViewById(R.id.aIV1);
        IV2 = findViewById(R.id.aIV2);
        IV3 = findViewById(R.id.aIV3);
        BT1 = findViewById(R.id.aBT1);
        BT2 = findViewById(R.id.aBT2);
        BT3 = findViewById(R.id.aBT3);
        soundbtngroup =  findViewById(R.id.soundbtngroup);
        img_pad = findViewById(R.id.img_pad);
        repeat_audio_btn = (Button) findViewById(R.id.repeat);
        show_mouth_tech_btn = (Button) findViewById(R.id.show_mouth_tech_btn);
        show_result_btn = (Button) findViewById(R.id.show_result_btn);
        T1 = findViewById(R.id.Ta1);
        TV1 = findViewById(R.id.aTV1);
        atestV = findViewById(R.id.atestv);
//        IV2.setImageResource(getResources().getIdentifier("@drawable/microphone", null, getPackageName()));
        IV2.setImageDrawable(getResources().getDrawable(R.drawable.microphone));
//        IV3.setImageResource(getResources().getIdentifier("@drawable/clockbackground", null, getPackageName()));
        IV3.setImageDrawable(getResources().getDrawable(R.drawable.clockbackground));
        img_pad.setImageDrawable(getResources().getDrawable(R.drawable.pad));
        T1 = findViewById(R.id.Ta1);


        bb = findViewById(R.id.bbb);
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent9 = new Intent(MainActivitya.this, MainActivity.class);
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
            刪除firebase部分
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
                        // Log.v("test", audio_file.getAbsolutePath());

                        //String response = sendPostRequest(firebase_nowwavname_ch, firebase_nowwavname, audio_file);

                        Log.d("HTTP", "nowwavname is "+firebase_nowwavname);

                        progressDialog = new ProgressDialog(MainActivitya.this);
                        progressDialog.setTitle("處理中");
                        progressDialog.setMessage("正在處理音檔中");
                        progressDialog.show();


                        NetworkRequestTask networkRequestTask = new NetworkRequestTask("https://140.125.45.132:414/call",
                                firebase_nowwavname_ch, firebase_nowwavname, audio_file);
                        networkRequestTask.execute();
                        
                        //Log.d("HTTP Connection", response);


                    } catch (Exception e) {
                        Log.e("TTT1", "error 3");
                        Log.e("TTT1", e.toString());
                    }

                    recordStatus++;
                    recordStatus %= 2;
                    BT3.setVisibility(View.VISIBLE);
                    repeat_audio_btn.setVisibility(View.VISIBLE);
                    show_result_btn.setVisibility(View.VISIBLE);
                    show_mouth_tech_btn.setVisibility(View.VISIBLE);
                    IV2.setVisibility(View.INVISIBLE);
//                    IV2.setImageResource(getResources().getIdentifier("@drawable/microphone", null, getPackageName()));
                    IV2.setImageDrawable(getResources().getDrawable(R.drawable.microphone));
                }
            }
        });

        //上一題
        BT1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BT3.setVisibility(View.INVISIBLE);
                IV2.setVisibility(View.VISIBLE);
                repeat_audio_btn.setVisibility(View.INVISIBLE);
                show_result_btn.setVisibility(View.INVISIBLE);
                show_mouth_tech_btn.setVisibility(View.INVISIBLE);
                if(fruitNo == 1) {
                    BT1.setVisibility(View.INVISIBLE);
                }
                fruitNo--;
                GetFirstFruit();
            }
        });
        //下一題
        BT2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BT3.setVisibility(View.INVISIBLE);
                IV2.setVisibility(View.VISIBLE);
                repeat_audio_btn.setVisibility(View.INVISIBLE);
                show_result_btn.setVisibility(View.INVISIBLE);
                show_mouth_tech_btn.setVisibility(View.INVISIBLE);
                if(fruitNo >= FruitChineseOrigin.size()-1){
                    fruitNo = 0; //超過題目位址,回到初始值}
                }
                else{
                    IV1.startAnimation(AnimationUtils.loadAnimation(MainActivitya.this, R.anim.fade_in_out));
                    fruitNo++;

                    GetFirstFruit();
                    BT1.setVisibility(View.VISIBLE);
                }

            }
        });

        BT3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordStatus = 0;
                IV2.setVisibility(View.VISIBLE);
            }
        });

        repeat_audio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    repeatAudio(audio_file.getAbsolutePath());
                } catch (Exception e) {
                    Log.e("TTT1", "repeat audio error: " + e);
                }
            }
        });

        show_result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("正在處理圖檔中");
                progressDialog.show();


                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View alert = layoutInflater.inflate(R.layout.show_result, null);
                AlertDialog.Builder scn = new AlertDialog.Builder(context);
                scn.setView(alert);
                AlertDialog SCN = scn.show();
                Button btn_OK = (Button)alert.findViewById(R.id.OK);
                TextView tv_result = (TextView)alert.findViewById(R.id.tv_result);
                TextView tv_title = (TextView)alert.findViewById(R.id.tv_title);
                ImageView img_view_result = (ImageView)alert.findViewById(R.id.imageView);

                String[] target = standard_wav.replace('\n', '\0').split(" ");
                String[] refrence = user_wav.replace('\n', '\0').split(" ");

                DTW dtw = new DTW(context);
                Map<String, List<String>> ans = dtw.dtw(target, refrence);
                Map<String, Integer> count = new HashMap<String, Integer>();
                String[] state = new String[target.length/2];
                for(int i=0;i<state.length;i++){
                    state[i] = "ok";
                }
                for(int i=0;i<target.length;i++){
                    count.put(target[i], ans.get(target[i]).size()-1);
                }
                for(int i=0;i<target.length;i+=2){
//                                                process initial
                    Log.v("dtw", "index"+(int)i/2);
                    String phoneme = target[i];
                    String usr_phoneme;
                    if(count.get(phoneme)>=0){
                        usr_phoneme  = ans.get(phoneme).get(count.get(phoneme));
                    }else {
                        usr_phoneme = "";
                    }
                    count.put(phoneme, count.get(phoneme)-1);
                    if(!phoneme.equals(usr_phoneme)){
                        state[i/2] = "initial";
                    }
//                                                process vowels
                    phoneme = target[i+1];
                    if(count.get(phoneme)>=0){
                        usr_phoneme  = ans.get(phoneme).get(count.get(phoneme));
                    }else {
                        usr_phoneme = "";
                    }
                    count.put(phoneme, count.get(phoneme)-1);
                    if(!phoneme.equals(usr_phoneme)){
                        if(state[i/2]=="initial"){
                            state[i/2] = "both";
                        }else {
                            state[i/2] = "vowels";
                        }

                    }

                }
//                                            for(String str:count.keySet()){
//                                                Log.v("dtw", String.valueOf(count.get(str)));Z
//                                            }
                for(int i=0;i<state.length;i++){
                    Log.v("dtw", state[i]);
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(FruitChinese.get(fruitNo));
                String[] word = FruitChinese.get(fruitNo).split("");
                for(String str:word){
                    Log.v("word", str);
                }
                String feedback = "";
                for(int i=0;i<state.length;i++){
                    if(state[i].equals("initial")){
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.rgb(0,255,0)),i,i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        feedback += "\""+word[i]+"\"" + "的聲母念錯了\n";
                    }else if(state[i].equals("vowels")){
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.rgb(0,0,255)),i,i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        feedback += "\""+word[i]+"\"" + "的韻母念錯了\n";

                    }else if(state[i].equals("both")){
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.rgb(255,0,0)),i,i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        feedback += "\""+word[i]+"\"" + "整個都念錯了\n";
                    }else {
                        feedback += "\""+word[i]+"\"" + "發的很標準\n";
                    }
                }

                //處理回傳的json中的diff
                feedback = "";
                try {
                    String diffStatus = diff.getString("status");
                    if (diffStatus.equals("all_correct")){
                        feedback = "每個字都很標準！";
                        tv_result.setText(feedback);
                    } else if (diffStatus.equals("incorrect_count")) {
                        feedback = "字數不對喔";
                        tv_result.setText(feedback);
                    } else {
                        JSONArray diffIncorrects = diff.getJSONArray("incorrect_pinyin");
                        SpannableStringBuilder feedbackcolor = new SpannableStringBuilder();
                        for (int i = 0; i < diffIncorrects.length(); i++) {
                            JSONObject diffIncorrect = diffIncorrects.getJSONObject(i);
                            String std_bpmf = diffIncorrect.getString("expected_bopomofo");
                            String asr_bpmf = diffIncorrect.getString("actual_bopomofo");
                            feedbackcolor.append(diffPinyinProcess(std_bpmf,asr_bpmf));
                            //feedback += std_bpmf + "唸成" + asr_bpmf + "了\n";
                        }
                        tv_result.setText(feedbackcolor);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


                tv_title.setText(spannableStringBuilder);
                //tv_result.setText(feedback);


//              tv_result.setText("準確度很棒，請繼續保持");
//              SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(FruitChinese.get(fruitNo));
//              tv_title.setText(spannableStringBuilder);
                //Log.e("firebase myref", myref);
                //Log.e("firebase serious_word", serious_word);
//              if (Integer.parseInt(myref) == 1) {
//                  tv_result.setText("準確度很棒，請繼續保持");
//                  tv_title.setText(FruitChinese.get(fruitNo));
//              }
//              if (Integer.parseInt(myref) >= 2){
//                  tv_result.setText("準確度要加油，紅色的字有點念錯");
////                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(FruitChinese.get(fruitNo));
////            if(Integer.parseInt(serious_word) == 3){
////                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.rgb(255,0,0)),3,4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////            }
////            else if (Integer.parseInt(serious_word) == 2){
////                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.rgb(255,0,0)),2,3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////            }
////            else if (Integer.parseInt(serious_word) == 1){
////                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.rgb(255,0,0)),1,2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////            }
////            else if (Integer.parseInt(serious_word) == 0){
////                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.rgb(255,0,0)),0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////            }
////
////            tv_title.setText(spannableStringBuilder);

                String imageUrl = "https://140.125.45.132:414/get_image";
                //Bitmap bmp = BitmapFactory.decodeByteArray(byte, 0, byte.length);
                Glide.with(v.getContext())
                        .asBitmap()
                        .skipMemoryCache(true) // 禁用内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 禁用磁盘缓存
                        .load(imageUrl)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                img_view_result.setImageBitmap(bitmap);
                            }
                        });


                progressDialog.dismiss();

                //img_view_result.setImageBitmap(Bitmap.createScaledBitmap());

                btn_OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SCN.dismiss();
                    }
                });

            }
        });
        //發音技巧

        //發音技巧按鈕
        show_mouth_tech_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 加載佈局文件
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View alert = layoutInflater.inflate(R.layout.show_mouth_tech, null);

                // 創建並顯示對話框
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(alert);
                AlertDialog dialog = builder.create();
                dialog.show();

                // 綁定佈局中的按鈕
                Button btn_OK = (Button) alert.findViewById(R.id.OK2);
                //發音技巧 result
                TextView tv_result2 = (TextView)alert.findViewById(R.id.tv_result2);

                // 為按鈕設定點擊事件
                btn_OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss(); // 關閉對話框
                    }
                });

                String targetPinyin = "";
                try {
                    String feedback = "";
                    String diffStatus = diff.getString("status");
                    if (diffStatus.equals("all_correct")){
                        feedback = "每個字都很標準！";
                        tv_result2.setText(feedback);
                    } else if (diffStatus.equals("incorrect_count")) {
                        feedback = "字數不對喔";
                        tv_result2.setText(feedback);
                    } else {
                        JSONArray diffIncorrects = diff.getJSONArray("incorrect_pinyin");
                        SpannableStringBuilder feedbackcolor = new SpannableStringBuilder();
                        for (int i = 0; i < diffIncorrects.length(); i++) {
                            JSONObject diffIncorrect = diffIncorrects.getJSONObject(i);
                            String std_bpmf = diffIncorrect.getString("expected_bopomofo");
                            String asr_bpmf = diffIncorrect.getString("actual_bopomofo");
                            //feedbackcolor.append(diffPinyinProcess2(std_bpmf,asr_bpmf));
                            targetPinyin = diffPinyinProcess2(std_bpmf,asr_bpmf);
                            //feedback += std_bpmf + "唸成" + asr_bpmf + "了\n";
                        }
                        //tv_result2.setText(targetPinyin);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                for(int i = 0;i < targetPinyin.length();i++) {
                    char target = targetPinyin.charAt(i);
                    String[] details = com.example.myapp.utils.PinyinInfo.getPinyinDetails(target);
                    tv_result2.append("\n"+target+"發音錯誤\n"+"\n發音部位: " + details[0] + "\n舌頭和嘴型: " + details[1]);
                }
                //查詢正確發音技巧功能
                //char targetPinyin = 'ㄅ'; // 測試的拼音符號
                //String[] details = com.example.myapp.utils.PinyinInfo.getPinyinDetails(targetPinyin);

                // 顯示拼音細節到介面
                //tv_result2.append("發音錯誤"+"\n發音部位: " + details[0] + "\n舌頭和嘴型: " + details[1]);
            }
        });






    }
    //比較字串並回傳
    private String diffPinyinProcess2(String std_bpmf, String asr_bpmf) {
        int lenstd = std_bpmf.length();
        int lenasr = asr_bpmf.length();

        // 若長度不同，回傳空字串
        if (lenstd != lenasr) {
            return std_bpmf;
        } else {
            StringBuilder diffChars = new StringBuilder();
            for (int i = 0; i < lenstd; i++) {
                char stdChar = std_bpmf.charAt(i);
                char asrChar = asr_bpmf.charAt(i);
                if (stdChar != asrChar) {
                    diffChars.append(stdChar); // 儲存不同的字元
                }
            }
            return diffChars.toString(); // 回傳儲存的不同字元
        }
    }




    private SpannableString diffPinyinProcess(String std_bpmf, String asr_bpmf){
        int lenstd = std_bpmf.length();
        int lenasr = asr_bpmf.length();
        SpannableString resultText = new SpannableString(std_bpmf + "唸成" + asr_bpmf + "了\n");
        if (lenstd != lenasr){
            return resultText;
        } else {
            List<ForegroundColorSpan> colorSpanList = new ArrayList<>();
            colorSpanList.add(new ForegroundColorSpan(Color.RED));    // 顏色1
            colorSpanList.add(new ForegroundColorSpan(Color.GREEN));  // 顏色2
            colorSpanList.add(new ForegroundColorSpan(Color.BLUE));   // 顏色3
            colorSpanList.add(new ForegroundColorSpan(Color.MAGENTA)); // 顏色4
            colorSpanList.add(new ForegroundColorSpan(Color.RED));    // 顏色1
            colorSpanList.add(new ForegroundColorSpan(Color.GREEN));  // 顏色2
            colorSpanList.add(new ForegroundColorSpan(Color.BLUE));   // 顏色3
            colorSpanList.add(new ForegroundColorSpan(Color.MAGENTA)); // 顏色4
            for (int i = 0;i < lenstd;i++){
                char stdChar = std_bpmf.charAt(i);
                char asrChar = asr_bpmf.charAt(i);
                int stdCharPosition = resultText.toString().indexOf(stdChar);
                int asrCharPosition = resultText.toString().lastIndexOf(asrChar);
                Log.d("Coloring", "Position of stdChar " + stdChar + " is " + resultText.toString().indexOf(stdChar));
                Log.d("Coloring", "Position of asrChar " + asrChar + " is " + resultText.toString().lastIndexOf(asrChar));
                if (stdChar != asrChar){
                    //resultText.setSpan(colorSpanList.get(i),resultText.toString().indexOf(stdChar),resultText.toString().indexOf(stdChar)+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //resultText.setSpan(colorSpanList.get(i),resultText.toString().lastIndexOf(asrChar),resultText.toString().lastIndexOf(asrChar)+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    resultText.setSpan(colorSpanList.get(i),stdCharPosition,stdCharPosition+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    resultText.setSpan(colorSpanList.get(i+4),asrCharPosition,asrCharPosition+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            return resultText;
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

        //frequencyAdjuster.loadAudio("raw/" + FruitEnglish.get(fruitNo) + "0");
        frequencyAdjuster.loadAudio(soundIDResource, this);
        frequencyAdjuster.doSTFT();
        frequencyAdjuster.doAdjust();
        frequencyAdjuster.doistft();
        String filepath = frequencyAdjuster.outputToFile("adjust_audio.wav");
        //byte[] audioData = frequencyAdjuster.getAdjusted_audio();

        int selectedSoundbtn = soundbtngroup.getCheckedRadioButtonId();
        RadioButton selectedBtn = findViewById(selectedSoundbtn);
        String btnText = selectedBtn.getText().toString();
        //Toast.makeText(MainActivitya.this, btnText, Toast.LENGTH_SHORT).show();

        boolean playAdjusted = btnText.equals("個人化調整");

        try{
            if (playAdjusted){
                soundID = soundPool.load(filepath, 1);
            } else {
                soundID = soundPool.load(this, soundIDResource, 1);
            }
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
//        IV1.setImageResource(imageResource);
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
            //soundID = soundPool.load("/data/user/0/com.example.rfinal/files/abc.wav_adjust", 1);
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

class FrequencyAdjuster extends AppCompatActivity{
    private int frequency_rate;
    private int frame_size;
    private int frame_hop_size;
    private int valid_db;
    private byte[] original_audio;
    private byte[] adjusted_audio;
    private double[] orig_audio_float;
    private Complex[][] orig_audio_stft;
    private double[] adjusted_audio_float;
    public Complex[][] adjusted_audio_stft;
    private double[] user_hearing_threshold;
    public int fft_count;



    public FrequencyAdjuster(){
        this.frequency_rate = 16000;
        this.frame_size = 1024;
        this.frame_hop_size = 256;
        this.valid_db = 20;
        //this.user_hearing_threshold = new double[]{0, 50, 40, 10, 10, 10, 10};
    }

    public FrequencyAdjuster(int rate, int frame_size, int hop_size, int threshold_db){
        this.frequency_rate = rate;
        this.frame_size = frame_size;
        this.frame_hop_size = hop_size;
        this.valid_db = threshold_db;
        this.fft_count = (frame_size/2)+1;
    }

    public void loadAudio(int resId, Context context){

        AssetFileDescriptor afd = context.getResources().openRawResourceFd(resId);

        int filesize = (int) afd.getLength();
        Log.d("FreqAdjust", "filesize is " + filesize);
        original_audio = new byte[filesize];

        try {
            FileInputStream FileinputStream = afd.createInputStream();;
            FileinputStream.read(original_audio);

            FileinputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int shortArraySize = filesize / 2;

        Log.d("FreqAdjust", "shortArraySize = " + shortArraySize);

        orig_audio_float = new double[shortArraySize];

        for (int i = 0; i < shortArraySize; i++) {
            // Convert each pair of bytes to a short value
            short sample = (short) ((original_audio[i * 2] & 0xFF) | (original_audio[i * 2 + 1] << 8));
            // Normalize to range [-1.0, 1.0]
            double normalizedSample = sample / 32768.0;
            orig_audio_float[i] = normalizedSample;
        }
        Log.d("FreqAdjust", "Complete to loadAudio");

    }

    public void doSTFT(){

        Log.d("FreqAdjust", "Start to do STFT");

        AudioTransformer audioTransformer = new AudioTransformer(this.frequency_rate, this.frame_size, this.frame_hop_size);

        Log.d("FreqAdjust", "Complete init AudioTransformer");
        Log.d("FreqAdjust", "Start to do STFT (call function)");
        Log.d("FreqAdjust", "orig_audio_float的長度是" + orig_audio_float.length);
        Complex[][] stftResult = audioTransformer.STFT(orig_audio_float);
        Log.d("FreqAdjust", "STFT completed (call function)");

        orig_audio_stft = stftResult;
    }

    public void doAdjust(){
        double[] dbs = FreqLinearMapping.gain_value;
        adjusted_audio_stft = new Complex[orig_audio_stft.length][orig_audio_stft[0].length];
        double[] plus = new double[513];

        System.out.println("125hz的dbs是：" + dbs[Hz2Index(125, frequency_rate, 1024)]);
        for (int i = 0; i < 513; i++) {
            if (dbs[i] > valid_db) {
                //plus[i] = dbs[i] - valid_db - (valid_db - dbs[Hz2Index(125, frequency_rate, 1024)]);
                plus[i] = dbs[i] - valid_db;
            }
        }

        for (int i = 0; i < orig_audio_stft.length; i++) {
            Complex prefPa = orig_audio_stft[i][Hz2Index(125, frequency_rate, 1024)];

            if (i == 0) {
                System.out.println("pref_pa = " + prefPa);
            }

            if (prefPa.getReal() != 0) {
                for (int j = 0; j < orig_audio_stft[0].length; j++) {
                    Complex newPa = signalPlus(plus[j], orig_audio_stft[i][j], prefPa);
                    adjusted_audio_stft[i][j] = newPa;
                }
            } else {
                for (int j = 0; j < orig_audio_stft[0].length; j++) {
                    Complex newPa = signalPlus(plus[j], orig_audio_stft[i][j], prefPa);
                    adjusted_audio_stft[i][j] = newPa;
                }
            }
        }
    }

    private static double[] setDbsF2(double[] x, double[] y, double[] xSmooth) {
        org.apache.commons.math3.analysis.interpolation.LinearInterpolator interpolator = new org.apache.commons.math3.analysis.interpolation.LinearInterpolator();
        PolynomialSplineFunction function = interpolator.interpolate(x, y);

        for (int i = 0; i < xSmooth.length; i++) {
            xSmooth[i] = i;
        }

        double[] ySmooth = new double[xSmooth.length];
        for (int i = 0; i < xSmooth.length; i++) {
            ySmooth[i] = function.value(xSmooth[i]);
        }

        return ySmooth;
    }

    private static int Hz2Index(double hz, int frequencyRate, int frameSize) {
        // 將頻率轉換為相應的索引
        //System.out.println("傳入的頻率 = " + hz);
        hz /= (frequencyRate / (double) frameSize);

        // 將索引轉換為整數並返回
        return (int) hz;
    }

    public static double ItoR(Complex val) {
        double R_signal;

        R_signal = Math.sqrt(Math.pow(val.getReal(), 2) + Math.pow(val.getImaginary(), 2));

        return R_signal;
    }

    private static Complex signalPlus(double plus, Complex originalSignal, Complex prefSignal) {
        // 將實部和虛部分開
        double[] signal = {ItoR(originalSignal), ItoR(prefSignal)};

        // 計算每個元素的dB
        double db = signal[0] / signal[1];
        db = Math.log10(db);
        db *= 20;

        // 對每個元素應用指定的增益
        double newDb = db + plus;

        // 將每個元素轉換回線性刻度
        double newPa = Math.pow(10, newDb / 20);
        newPa *= signal[1];

        // 計算每個元素的增益因子
        double gain = newPa / signal[0];

        // 對每個元素應用增益到原始信號
        originalSignal = originalSignal.multiply(gain);
        //Complex adjustedComplex = new Complex(originalSignal.getReal() * gain, originalSignal.getImaginary());

        return originalSignal;
    }

    private static Complex[] createFullSpectrum(Complex[] positiveSpectrum) {
        int n = positiveSpectrum.length;

        // 補滿雙邊頻譜，包括正頻率和鏡像的負頻率
        Complex[] fullSpectrum = new Complex[2 * n - 2];

        // 將正頻率複製到完整頻譜的左半邊
        System.arraycopy(positiveSpectrum, 0, fullSpectrum, 0, n);

        // 將正頻率鏡像反轉到完整頻譜的右半邊
        for (int i = n; i < (2*n)-2; i++) {
            if (positiveSpectrum[2 * n - 1 - i] == null){
                positiveSpectrum[2 * n - 1 - i] = new Complex(0, 0);
            }
            fullSpectrum[i] = positiveSpectrum[2 * n - 1 - i].conjugate(); // 鏡像反轉
        }

        return fullSpectrum;
    }

    public void doistft() {
        int numFrames = adjusted_audio_stft.length;
        System.out.println("numFrames = " + numFrames);
        //int frameSize = adjusted_audio_stft[0].length;
        int frameSize = (adjusted_audio_stft[0].length-1)*2;
        System.out.println("frameSize = " + frameSize);
        int hopSize = 256;
        int overlap = frameSize - hopSize; // 重疊部分

        // 計算信號長度，考慮重疊
        int signalLength = numFrames * hopSize + frameSize;
        System.out.println("signalLength(ISTFT) = " + signalLength);

        // Initialize the ISTFT result array
        double[] resultSignal = new double[signalLength];

        for (int i = 0; i < numFrames; i++) {
            // Perform inverse FFT on each frequency bin
            Complex[] fullComplexs = createFullSpectrum(adjusted_audio_stft[i]);

            //double[] ifftResult = performInverseFFT(adjusted_audio_stft[i]);
            double[] ifftResult = performInverseFFT(fullComplexs);

            // Add the result to the correct position
            int startPos = i * hopSize;
            System.out.print("startPos = " + startPos + ", ");
            System.out.print("frame index = " + i);
            System.out.println();
            for (int j = 0; j < frameSize; j++) {
                resultSignal[startPos + j] += ifftResult[j];
            }
        }
        adjusted_audio_float = resultSignal;

        System.out.println("陣列長度：" + adjusted_audio_float.length);
    }

    private static double[] performInverseFFT(Complex[] complexData) {
        if (complexData == null) {
            // 處理 complexData 為 null 的情況，例如拋出 IllegalArgumentException 或返回一個預設的陣列
            throw new IllegalArgumentException("Input complexData is null");
        }

        // Perform inverse FFT
        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] ifftResult = transformer.transform(complexData, TransformType.INVERSE);

        // Convert the complex result to real part
        double[] realPart = new double[ifftResult.length];
        for (int i = 0; i < ifftResult.length; i++) {
            realPart[i] = ifftResult[i].getReal();
        }

        return realPart;
    }

    public String outputToFile(String filePath){
        //writeNormalizedAudio(adjusted_audio_float, new AudioFormat(frequency_rate, 16, 1, true, false), filePath);
        return writeNormalizedAudio(adjusted_audio_float, new AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .setSampleRate(frequency_rate)
                .build(), filePath);
    }

    private static String writeNormalizedAudio(double[] normalizedAudio, AudioFormat format, String filePath) {
        byte[] audioBytes = convertTo16BitPCM(normalizedAudio);

        return (writeWavFile(audioBytes, format.getSampleRate(), filePath));

    }

    public static String writeWavFile(byte[] audioData, int sampleRate, String filePath) {
        //String filepath = context.getFilesDir() + "/" + fileName;

        File outputFile;

        try {
            outputFile = File.createTempFile("adjusted", "wav");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Log.d("FreqAdjust", "輸出檔案位置：" + outputFile.getAbsolutePath());

        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            // Write WAV header
            fileOutputStream.write(generateWavHeader(audioData.length, sampleRate));

            // Write audio data
            fileOutputStream.write(audioData);

            Log.d("FreqAdjust", "Finished write to wav");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile.getAbsolutePath();
    }

    private static byte[] generateWavHeader(int audioDataSize, int sampleRate) {
        int totalDataLen = audioDataSize + 36;
        int byteRate = sampleRate * 2;

        ByteBuffer headerBuffer = ByteBuffer.allocate(44);
        headerBuffer.order(ByteOrder.LITTLE_ENDIAN);

        // ChunkID "RIFF"
        headerBuffer.put("RIFF".getBytes());
        // ChunkSize
        headerBuffer.putInt(totalDataLen);
        // Format "WAVE"
        headerBuffer.put("WAVE".getBytes());
        // Subchunk1ID "fmt "
        headerBuffer.put("fmt ".getBytes());
        // Subchunk1Size (16 for PCM)
        headerBuffer.putInt(16);
        // AudioFormat (PCM = 1)
        headerBuffer.putShort((short) 1);
        // NumChannels
        headerBuffer.putShort((short) 1); // Mono
        // SampleRate
        headerBuffer.putInt(sampleRate);
        // ByteRate
        headerBuffer.putInt(byteRate);
        // BlockAlign
        headerBuffer.putShort((short) 2);
        // BitsPerSample
        headerBuffer.putShort((short) 16);
        // Subchunk2ID "data"
        headerBuffer.put("data".getBytes());
        // Subchunk2Size
        headerBuffer.putInt(audioDataSize);

        return headerBuffer.array();
    }

    private static byte[] convertTo16BitPCM(double[] normalizedAudio) {
        byte[] audioBytes = new byte[normalizedAudio.length * 2];
        for (int i = 0; i < normalizedAudio.length; i++) {
            // 將浮點數值映射到16位元PCM的範圍內
            double scaledValue = normalizedAudio[i] * Short.MAX_VALUE;
            short sample = (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, scaledValue));
            audioBytes[i * 2] = (byte) (sample & 0xFF);
            audioBytes[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
        }
        return audioBytes;
    }
}

class AudioTransformer {
    private int frequency_rate;
    private int frame_size;
    private int frame_hop_size;

    public AudioTransformer(int frequency_rate, int frame_size, int frame_hop_size){
        this.frequency_rate = frequency_rate;
        this.frame_size = frame_size;
        this.frame_hop_size = frame_hop_size;
    }

    public Complex[][] STFT(double[] audioSignal){
        return calculateSTFT(audioSignal, frame_size, frame_hop_size, frame_size);
    }

    private static Complex[][] calculateSTFT(double[] signal, int windowSize, int hopSize, int fftSize) {
        Log.d("FreqAdjust", "Go into calculateSTFT function");
        int signalLength = signal.length;
        Log.d("FreqAdjust", "SignalLength is "+signalLength);
        int numFrames = (int) Math.ceil((double) (signalLength - windowSize) / hopSize) + 1;
        numFrames = (int) Math.ceil((double) signalLength / hopSize);
        Log.d("FreqAdjust", "numFrames is "+numFrames);
        System.out.println("numFrames = " + numFrames);
        System.out.println("signalLength = " + signalLength);
        System.out.println("windowSize = " + windowSize);
        System.out.println("hopSize = " + hopSize);
        System.out.println("fftSize = " + fftSize);

        // Initialize the output STFT matrix
        Complex[][] stftMatrix = new Complex[numFrames][fftSize/2+1];

        // Apply zero padding to the signal
        int paddedSignalLength = numFrames * hopSize + windowSize;
        //paddedSignalLength = numFrames * hopSize;
        System.out.println("paddedSignalLength = " + paddedSignalLength);
        double[] paddedSignal = Arrays.copyOf(signal, paddedSignalLength);

        // Initialize Hann window
        double[] hannWindow = new double[windowSize];
        for (int i = 0; i < windowSize; i++) {
            hannWindow[i] = 0.5 * (1 - Math.cos(2 * Math.PI * i / (windowSize - 1)));
        }

        // Perform STFT
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        for (int i = 0; i < numFrames; i++) {
            // Extract the current frame
            double[] frame = Arrays.copyOfRange(paddedSignal, i * hopSize, i * hopSize + windowSize);

            // Apply Hann window
            for (int j = 0; j < windowSize; j++) {
                frame[j] *= hannWindow[j];
            }

            // Compute FFT
            Complex[] fftResult = fft.transform(frame, TransformType.FORWARD);

            // Keep only the positive frequencies
            int positiveFrequencies = fftSize / 2 + 1;
            stftMatrix[i] = Arrays.copyOfRange(fftResult, 0, positiveFrequencies);
        }

        return stftMatrix;
    }
}