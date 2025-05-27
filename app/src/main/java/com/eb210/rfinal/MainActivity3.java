package com.eb210.rfinal;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.maple.recorder.recording.AudioChunk;
import com.maple.recorder.recording.AudioRecordConfig;
import com.maple.recorder.recording.MsRecorder;
import com.maple.recorder.recording.PullTransport;

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


    // firebase
    String nowwav;
    String firebase_nowwavname = "start_test";
    String firebase_nowwavname_ch;
    String standard_wav = "";
    String user_wav = "";
    String server_DTW = "";
    JSONObject diff;
    Button show_result_btn;

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
        show_result_btn = (Button) findViewById(R.id.btn_result);
        
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
                String url = "https://140.125.45.129:414/call4";
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

                        Log.d("HTTP", "nowwavname is "+firebase_nowwavname);

                        progressDialog = new ProgressDialog(MainActivity3.this);
                        progressDialog.setTitle("處理中");
                        progressDialog.setMessage("正在處理音檔中");
                        progressDialog.show();


                        NetworkRequestTask networkRequestTask = new NetworkRequestTask(url,
                                firebase_nowwavname_ch, firebase_nowwavname, audio_file);
                        networkRequestTask.execute();

                        //Log.d("HTTP Connection", response);


                    } catch (Exception e) {
                        Log.e("TTT1", "error 3");
                        Log.e("TTT1", e.toString());
                    }

                    recordStatus++;
                    recordStatus %= 2;
                }
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
                    String tailo = json.getString("tailo");
                    String audioUrl = json.getString("fast_audio_url");
                    Log.d("TTSRequestTask", "✅ 台羅文字：" + tailo);
                    Log.d("TTSRequestTask", "🎧 音檔網址：" + audioUrl);

                    return new String[]{tailo, audioUrl};
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
