package com.eb210.rfinal;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import java.io.OutputStream;
import java.net.URLEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;
import com.maple.recorder.recording.AudioChunk;
import com.maple.recorder.recording.AudioRecordConfig;
import com.maple.recorder.recording.MsRecorder;
import com.maple.recorder.recording.PullTransport;
import com.maple.recorder.recording.Recorder;
import android.app.ProgressDialog;
import android.content.Context;




public class MainActivity3 extends AppCompatActivity {
    int recordStatus = 0;
    private EditText inputChinese;
    private TextView outputTailo;
    private Button btnConvert;
    private GifImageView btnmic;

    private Button btnTestPost;
    Recorder recorder;
    File audio_file;
    ProgressDialog progressDialog;
    Button show_mouth_tech_btn;

    // firebase
    String nowwav;
    String firebase_nowwavname = "start_test";
    String firebase_nowwavname_ch;
    String standard_wav = "";
    String user_wav = "";
    String server_DTW = "";
    String tts_tailo;
    JSONObject diff;
    Button show_result_btn;
    final Context context = MainActivity3.this;

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
        final Context context = MainActivity3.this;
        public NetworkRequestTask(String url, String wavcontent,File file) {
            this.url = url;
            this.wavcontent = wavcontent;
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
            if (result == null) {
                Log.e("HTTP Request", "Response is null (server error or network issue)");
                progressDialog.dismiss();
                Toast.makeText(context, "伺服器請求失敗，請檢查網路", Toast.LENGTH_SHORT).show();
                return; // 提前返回，避免後續操作
            }
            try {
                // 处理响应数据，例如更新UI
                Log.d("HTTP Request", result);
                JSONObject resultjson = new JSONObject(result);
                user_wav = resultjson.getString("kaldi");
                standard_wav = resultjson.getString("standard");
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
    private SpannableString diffTailoProcess(String std_tailo, String asr_tailo) {
        std_tailo = std_tailo.toLowerCase(); // 忽略大小寫
        asr_tailo = asr_tailo.toLowerCase(); // 忽略大小寫

        String[] stdSyllables = std_tailo.split("-");
        String[] asrSyllables = asr_tailo.split("-");

        StringBuilder resultBuilder = new StringBuilder();
        int minLength = Math.min(stdSyllables.length, asrSyllables.length);

        boolean isPerfectMatch = true; // 假設兩者發音完全一致
        int currentOffset = 0;

        // 拼接標準發音和實際發音的比較
        for (int i = 0; i < minLength; i++) {
            String[][] stdParts = splitTaiLoPinyin(stdSyllables[i]);  // 返回的是二維陣列
            String[][] asrParts = splitTaiLoPinyin(asrSyllables[i]);

            // 如果標準發音和 ASR 發音不相同，則顯示 "唸成"
            if (!stdSyllables[i].equals(asrSyllables[i])) {
                resultBuilder.append(stdSyllables[i]).append("唸成").append(asrSyllables[i]).append("\n");
                isPerfectMatch = false; // 標記為不完全匹配
            }

            currentOffset += stdSyllables[i].length() + 2 + asrSyllables[i].length(); // 加上 " 唸成 " 和換行符號
        }

        // 如果沒有錯誤，顯示 "唸得非常標準"
        if (isPerfectMatch) {
            resultBuilder.append("唸得非常標準");
        }

        SpannableString resultText = new SpannableString(resultBuilder.toString());

        currentOffset = 0; // 重設 offset 用於標註顏色

        // 比較每個拼音並標註不同部分
        for (int i = 0; i < minLength; i++) {
            String[][] stdParts = splitTaiLoPinyin(stdSyllables[i]);
            String[][] asrParts = splitTaiLoPinyin(asrSyllables[i]);

            // 計算聲母和韻母的起始和結束位置
            int stdStart = currentOffset;
            int stdEnd = stdStart + stdParts[0][0].length() + stdParts[0][1].length();
            int asrStart = stdEnd + 2; // "唸成" 的長度是 3
            int asrEnd = asrStart + asrParts[0][0].length() + asrParts[0][1].length();

            // 如果聲母不同，標註紅色
            if (!stdParts[0][0].equals(asrParts[0][0])) {
                resultText.setSpan(new ForegroundColorSpan(Color.RED), stdStart, stdStart + stdParts[0][0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                resultText.setSpan(new ForegroundColorSpan(Color.RED), asrStart, asrStart + asrParts[0][0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // 如果韻母不同，標註藍色
            if (!stdParts[0][1].equals(asrParts[0][1])) {
                resultText.setSpan(new ForegroundColorSpan(Color.BLUE), stdStart + stdParts[0][0].length(), stdEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                resultText.setSpan(new ForegroundColorSpan(Color.BLUE), asrStart + asrParts[0][0].length(), asrEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // 更新 offset
            currentOffset += stdSyllables[i].length() + 3 + asrSyllables[i].length(); // 加上 " 唸成 " 和換行符號
        }

        return resultText;
    }
    private static final String[] CONSONANTS = {
            "tsh", "chh", "ph", "th", "kh", "ts", "ch", "ng",
            "b", "p", "t", "k", "m", "n", "l", "s", "g", "h", "j"
    };

    private static String[][] splitTaiLoPinyin(String phrase) {
        String[] words = phrase.split("-");
        String[][] result = new String[words.length][2];

        for (int i = 0; i < words.length; i++) {
            String word = removeTone(words[i].toLowerCase());
            result[i] = splitInitialFinal(word);
        }
        return result;
    }

    private static String removeTone(String input) {
        if (input == null || input.isEmpty()) return input;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    private static String[] splitInitialFinal(String syllable) {
        for (String consonant : CONSONANTS) {
            if (syllable.startsWith(consonant)) {
                return new String[]{consonant, syllable.substring(consonant.length())};
            }
        }
        return new String[]{"", syllable};
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        inputChinese = findViewById(R.id.inputChinese);
        outputTailo = findViewById(R.id.outputTailo);
        btnConvert = findViewById(R.id.btnConvert);
        btnmic = findViewById(R.id.record_btn);
        btnmic.setImageDrawable(getResources().getDrawable(R.drawable.microphone));
        String firebase_nowwavname = "start_test";
        //test botton
        btnTestPost = findViewById(R.id.btnTestPost);
        show_result_btn = (Button) findViewById(R.id.show_result_btn);
        show_mouth_tech_btn = (Button) findViewById(R.id.show_mouth_tech_btn);

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
                Button btn_OK = alert.findViewById(R.id.OK2);
                TextView tv_result2 = alert.findViewById(R.id.tv_result2);

                // 為按鈕設定點擊事件
                btn_OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss(); // 關閉對話框
                    }
                });
                processTaiwanesePronunciation(tv_result2);

            }
        });
        btnTestPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testInput = inputChinese.getText().toString().trim();
                if (testInput.isEmpty()) {
                    Toast.makeText(MainActivity3.this, "請先輸入中文內容", Toast.LENGTH_SHORT).show();
                } else {
                    sendTestPost(testInput);
                }
            }
        });


        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = inputChinese.getText().toString().trim();
                if (inputText.isEmpty()) {
                    Toast.makeText(MainActivity3.this, "請輸入中文句子", Toast.LENGTH_SHORT).show();
                } else {
                    new TTSRequestTask().execute(inputText);
                }
            }
        });
        btnmic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //語言選擇不同的api
                String url = "https://140.125.45.129:414/call5";
                if (recordStatus == 0) {
                    // 錄音流程
                    try {
                        recordStatus++;
                        recordStatus %= 2;
//                        IV2.setImageResource(getResources().getIdentifier("@drawable/pause", null, getPackageName()));
                        btnmic.setImageDrawable(getResources().getDrawable(R.drawable.pause));
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
                        btnmic.setImageDrawable(getResources().getDrawable(R.drawable.loading));
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


                        progressDialog = new ProgressDialog(MainActivity3.this);
                        progressDialog.setTitle("處理中");
                        progressDialog.setMessage("正在處理音檔中");
                        progressDialog.show();

                        firebase_nowwavname_ch = "si-kue";
                        NetworkRequestTask networkRequestTask = new NetworkRequestTask(url,
                                tts_tailo,audio_file);
                        networkRequestTask.execute();

                        //Log.d("HTTP Connection", response);


                    } catch (Exception e) {
                        Log.e("TTT1", "error 3");
                        Log.e("TTT1", e.toString());
                    }

                    recordStatus++;
                    recordStatus %= 2;
                    btnmic.setImageDrawable(getResources().getDrawable(R.drawable.microphone));
                }
            }
        });
        show_result_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ✅ 檢查 diff 是否為 null
                if (diff == null) {
                    Toast.makeText(context, "請先完成錄音與伺服器回傳，再顯示結果", Toast.LENGTH_LONG).show();
                    return; // 中止後續處理
                }
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(MainActivity3.this);
                    progressDialog.setTitle("處理中");
                }
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
                String feedback = "";
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
                            String std_bpmf, asr_bpmf;
                            std_bpmf = diffIncorrect.getString("expected_tailo");
                            asr_bpmf = diffIncorrect.getString("actual_tailo");
                            feedbackcolor.append(diffTailoProcess(std_bpmf, asr_bpmf));
                        }
                        tv_result.setText(feedbackcolor);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


                String imageUrl = "https://140.125.45.129:414/get_image";
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
    }


    private class TTSRequestTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... strings) {
            String chineseText = strings[0];
            String boundary = "----TTSBoundary";
            String lineEnd = "\r\n";
            String twoHyphens = "--";

            try {
                // ✅ 內部處理信任所有自簽憑證（適用於開發測試）
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        }
                };

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

                // ✅ 建立連線
                URL url = new URL("https://140.125.45.129:414/tts_android");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                Log.d("TTSRequestTask", "🚀 正在傳送中文到 Flask: " + chineseText);

                try (DataOutputStream request = new DataOutputStream(connection.getOutputStream())) {
                    request.writeBytes(twoHyphens + boundary + lineEnd);
                    request.writeBytes("Content-Disposition: form-data; name=\"chinese_text\"" + lineEnd);
                    request.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                    request.writeBytes(lineEnd);
                    request.write(chineseText.getBytes("UTF-8"));
                    request.writeBytes(lineEnd);
                    request.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                }

                int responseCode = connection.getResponseCode();
                Log.d("TTSRequestTask", "📡 Flask 回應碼: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d("TTSRequestTask", "📨 伺服器回應內容: " + result.toString());

                    JSONObject json = new JSONObject(result.toString());
                    tts_tailo = json.getString("tailo");
                    String audioUrl = json.getString("fast_audio_url");
                    Log.d("TTSRequestTask", "✅ 台羅文字：" + tts_tailo);
                    Log.d("TTSRequestTask", "🎧 音檔網址：" + audioUrl);

                    return new String[]{tts_tailo, audioUrl};
                } else {
                    Log.e("TTSRequestTask", "❌ 伺服器錯誤: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("TTSRequestTask", "❗ 傳送失敗", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                String tailo = result[0];
                String audioUrl = result[1];
                outputTailo.setText("台羅：" + tailo);
                Toast.makeText(MainActivity3.this, "🟢 成功取得音檔，準備播放...", Toast.LENGTH_SHORT).show();
                // ✅ 建議用下載後播放，避免串流斷線
                playAudioAfterDownload(audioUrl);
            } else {
                Toast.makeText(MainActivity3.this, "🔴 語音請求失敗，請檢查連線或伺服器", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void playAudioFromUrl(String audioUrl) {
        Log.d("MediaPlayer", "🔊 正在嘗試播放音檔：" + audioUrl);

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d("MediaPlayer", "▶️ 音檔已準備完成，開始播放！");
                mediaPlayer.start();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayer", "❌ 播放錯誤: what=" + what + ", extra=" + extra);
                Toast.makeText(MainActivity3.this, "播放音檔時發生錯誤", Toast.LENGTH_LONG).show();
                return true;
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("MediaPlayer", "❗ 音檔來源錯誤或無法播放", e);
            Toast.makeText(MainActivity3.this, "無法播放音檔，請確認網址", Toast.LENGTH_LONG).show();
        }
    }
    private void playAudioAfterDownload(String audioUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(audioUrl);
                InputStream inputStream = url.openStream();
                File audioFile = File.createTempFile("tts_audio", ".wav", getCacheDir());
                FileOutputStream outputStream = new FileOutputStream(audioFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                runOnUiThread(() -> {
                    Log.d("MediaPlayer", "📥 音檔已下載：" + audioFile.getAbsolutePath());
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                        mediaPlayer.setOnPreparedListener(mp -> {
                            Log.d("MediaPlayer", "▶️ 本地音檔播放開始！");
                            mediaPlayer.start();
                        });
                        mediaPlayer.setOnCompletionListener(mp -> {
                            Log.d("MediaPlayer", "✅ 播放完成！");
                            mediaPlayer.release();
                        });
                        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                            Log.e("MediaPlayer", "❌ 播放失敗 (本地)", null);
                            return true;
                        });
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        Log.e("MediaPlayer", "❗ 播放失敗", e);
                    }
                });

            } catch (Exception e) {
                Log.e("AudioDownload", "❗ 下載音檔失敗", e);
            }
        }).start();
    }
    private void processTaiwanesePronunciation(TextView tv_result2) {
        try {
            String feedback = "";
            String diffStatus = diff.getString("status");
            if (diffStatus.equals("all_correct")) {
                feedback = "每個字都很標準！";
                tv_result2.setText(feedback);
            } else if (diffStatus.equals("incorrect_count")) {
                feedback = "字數不對喔";
                tv_result2.setText(feedback);
            } else {
                JSONArray diffIncorrects = diff.getJSONArray("incorrect_pinyin");
                SpannableStringBuilder tech_result = new SpannableStringBuilder();
                for (int i = 0; i < diffIncorrects.length(); i++) {
                    JSONObject diffIncorrect = diffIncorrects.getJSONObject(i);
                    String std_tailo = diffIncorrect.getString("expected_tailo");
                    String asr_tailo = diffIncorrect.getString("actual_tailo");

                    String targetPinyin = Arrays.toString(diffTailoProcess2(std_tailo, asr_tailo))
                            .replace("[", "").replace("]", "").trim();

                    String consonantDiff = targetPinyin.split(",")[0].trim();
                    String vowelDiff = targetPinyin.split(",")[1].trim();
                    String toneDiff = targetPinyin.split(",")[2].trim();

                    // 逐個錯誤符號處理：聲母
                    if (!consonantDiff.isEmpty()) {
                        String[] consonants = consonantDiff.split(" ");
                        for (String consonant : consonants) {
                            String[] consonantDetails = tailoInfo.getTaiLoDetails(consonant);
                            SpannableString spannableString = new SpannableString(
                                    "\n" + consonant + "發音錯誤\n發音部位: " + consonantDetails[0] + "\n舌頭和嘴型: " + consonantDetails[1]);

                            // 標紅
                            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, consonant.length() + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new AbsoluteSizeSpan(30, true), 0, consonant.length() + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tech_result.append(spannableString);
                        }
                    }

                    // 逐個錯誤符號處理：韻母
                    if (!vowelDiff.isEmpty()) {
                        String[] vowels = vowelDiff.split(" ");
                        for (String vowel : vowels) {
                            String[] vowelDetails = tailoInfo.getTaiLoDetails(vowel);
                            SpannableString spannableString = new SpannableString(
                                    "\n" + vowel + "發音錯誤\n發音部位: " + vowelDetails[0] + "\n舌頭和嘴型: " + vowelDetails[1]);

                            // 標紅
                            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, vowel.length() + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new AbsoluteSizeSpan(30, true), 0, vowel.length() + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tech_result.append(spannableString);
                        }
                    }

                    // 逐個錯誤符號處理：聲調
                    if (!toneDiff.isEmpty()) {
                        String[] tones = toneDiff.split(" ");
                        for (String tone : tones) {
                            String[] toneDetails = tailoInfo.getTaiLoDetails(tone);
                            SpannableString spannableString = new SpannableString(
                                    "\n" + tone + "聲 發音錯誤\n漢語調類: " + toneDetails[0] + "\n發音技巧-: " + toneDetails[1]);

                            // 標紅
                            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, tone.length() + 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new AbsoluteSizeSpan(30, true), 0, tone.length() + 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tech_result.append(spannableString);
                        }
                    }
                }
                tv_result2.setText(tech_result);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    // 台語發音技巧功能，比較字串並回傳不同部分
    private static String[] diffTailoProcess2(String std_tailo, String asr_tailo) {
        std_tailo = std_tailo.toLowerCase(); // 忽略大小寫
        asr_tailo = asr_tailo.toLowerCase(); // 忽略大小寫

        // 使用更新的 splitTaiLoPinyin 函數
        String[][] stdParts = splitTaiLoPinyin(std_tailo); // 標準發音
        String[][] asrParts = splitTaiLoPinyin(asr_tailo); // 實際發音

        String consonantDiff = "";
        String vowelDiff = "";
        String toneDiff = "";

        // 比較聲母、韻母和聲調，對每個單詞分別處理
        for (int i = 0; i < stdParts.length; i++) {
            // 比較聲母
            if (!stdParts[i][0].equals(asrParts[i][0])) {
                consonantDiff += stdParts[i][0] + " "; // 若不同，記錄標準發音的聲母
            }

            // 比較韻母（不含聲調）
            String stdVowel = stdParts[i][1].replaceAll("[0-9]", "");
            String asrVowel = asrParts[i][1].replaceAll("[0-9]", "");
            if (!stdVowel.equals(asrVowel)) {
                vowelDiff += stdVowel + " ";   // 若不同，記錄標準發音的韻母（不含聲調）
                // vowelDiff += stdParts[i][1] + " "; // 若不同，記錄標準發音的韻母（含聲調）
            }

            // 比較聲調
            String stdTone = stdParts[i][1].replaceAll("[^0-9]", "");
            String asrTone = asrParts[i][1].replaceAll("[^0-9]", "");
            if (!stdTone.equals(asrTone)) {
                toneDiff += stdTone + " "; // 若不同，記錄標準發音的聲調
            }
        }

        return new String[]{consonantDiff.trim(), vowelDiff.trim(), toneDiff.trim()};
    }

    private void sendTestPost(String text) {
        new Thread(() -> {
            try {
                URL url = new URL("https://140.125.45.129:414/test_post"); // ✅ 替換為你實際的 server IP 和 port
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "chinese_text=" + URLEncoder.encode(text, "UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes("UTF-8"));
                os.flush();
                os.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject json = new JSONObject(response.toString());
                String result = json.getString("received_text");

                runOnUiThread(() -> {
                    Toast.makeText(MainActivity3.this, "伺服器回應：" + result, Toast.LENGTH_LONG).show();
                    outputTailo.setText("回應內容：\n" + result);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity3.this, "錯誤：" + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    };



}
