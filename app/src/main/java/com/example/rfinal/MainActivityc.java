package com.example.rfinal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.maple.recorder.recording.AudioChunk;
import com.maple.recorder.recording.AudioRecordConfig;
import com.maple.recorder.recording.MsRecorder;
import com.maple.recorder.recording.PullTransport;
import com.maple.recorder.recording.Recorder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class MainActivityc extends AppCompatActivity {

    private Button btnRecord, btnSave, btnCompare,btnStop;
    private TextView txtResult2;
    private MediaRecorder mediaRecorder;
    private Recorder recorder;
    private List<File> audioFiles = new ArrayList<>(); // 儲存錄製的四個音檔
    private int currentRecordingIndex = 0; // 當前錄製的音檔索引
    private int currentRecordingIndex2 = 0; //紀錄顯示音調到幾聲
    private ProgressDialog progressDialog;

    HostnameVerifier hostnameVerifier = (hostname, session) -> true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activityc);

        // 綁定按鈕
        btnRecord = findViewById(R.id.btn_record);
        btnSave = findViewById(R.id.btn_save);
        btnStop = findViewById(R.id.btn_stop);
        btnCompare = findViewById(R.id.btn_compare);

        // 綁定 TextView
        txtResult2 = findViewById(R.id.txt_result2);

        // 設置錄音按鈕點擊事件
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRecordingIndex < 4) {
                    startRecording();
                    btnRecord.setEnabled(false);  // 錄音中禁止點擊
                    btnStop.setEnabled(true);     // 啟用停止按鈕
                } else {
                    Toast.makeText(MainActivityc.this, "已經錄製了四個音檔", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // 設置停止錄音按鈕
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRecordingIndex2 < 3) {
                    // 根據索引顯示要錄製的音調
                    String[] tones = {"ㄅㄚ (第二聲)", "ㄅㄚ (第三聲)", "ㄅㄚ (第四聲)"};
                    txtResult2.setText("錄製的音調: " + tones[currentRecordingIndex2]);
                } else {
                    Toast.makeText(MainActivityc.this, "erro", Toast.LENGTH_SHORT).show();
                }
                currentRecordingIndex2++;
                stopRecording();

                btnRecord.setEnabled(true);  // 允許再次錄音
                btnStop.setEnabled(false);   // 停止錄音後禁用按鈕
            }
        });

        // 設置保存按鈕點擊事件
        // 設置保存按鈕點擊事件
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioFiles.size() == 4) {
                    // 提供上傳的 API URL
                    new UploadTask("https://140.125.45.132:414/call2").execute(audioFiles);

                    // 將圖片載入 ImageView
                    ImageView imgPitchContour = findViewById(R.id.img_pitch_contour);
                    String imageUrl = "https://140.125.45.132:414/get_image2";
                    Glide.with(MainActivityc.this)
                            .load(imageUrl)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(imgPitchContour);

                    Toast.makeText(MainActivityc.this, "圖片已載入", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivityc.this, "請先錄製四個音檔", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // 設置比較按鈕點擊事件（可選）
        // 設置比較按鈕點擊事件（可選）
        btnCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioFiles.size() == 4) {
                    // 使用新的 URL 上傳音檔
                    new UploadTask("https://140.125.45.132:414/call3").execute(audioFiles);

                    // 獲取 ImageView
                    ImageView imgPitchContour2 = findViewById(R.id.img_pitch_contour2);
                    ImageView imgPitchContour = findViewById(R.id.img_pitch_contour);
                    // 使用 Glide 載入圖片
                    String imageUrl = "https://140.125.45.132:414/get_image2";
                    Glide.with(MainActivityc.this)
                            .load(imageUrl)
                            .skipMemoryCache(true)  // 避免載入快取
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁碟快取
                            .into(imgPitchContour);
                    imageUrl = "https://140.125.45.132:414/get_image3";
                    Glide.with(MainActivityc.this)
                            .load(imageUrl)
                            .skipMemoryCache(true)  // 避免載入快取
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁碟快取
                            .into(imgPitchContour2);

                    Toast.makeText(MainActivityc.this, "圖片已載入", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivityc.this, "請先錄製四個音檔", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 初始時停用停止錄音按鈕
        btnStop.setEnabled(false);
    }

    // 開始錄音
    private void startRecording() {
        try {
            // 創建音檔
            File audioFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "recording_" + currentRecordingIndex + ".wav");
            if (audioFile.exists()) {
                audioFile.delete();
            }
            audioFile.createNewFile();

            // 使用 MsRecorder 進行錄音
            recorder = MsRecorder.wav(
                    audioFile,
                    new AudioRecordConfig(),
                    new PullTransport.Default()
                            .setOnAudioChunkPulledListener(new PullTransport.OnAudioChunkPulledListener() {
                                @Override
                                public void onAudioChunkPulled(AudioChunk audioChunk) {
                                    Log.e("數據", "amplitude: " + audioChunk.maxAmplitude());
                                }
                            })
            );

            recorder.startRecording(); // 開始錄音

            Toast.makeText(this, "開始錄音: " + audioFile.getName(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("MainActivityc", "錄音失敗", e);
            Toast.makeText(this, "錄音失敗", Toast.LENGTH_SHORT).show();
        }
    }

    // 停止錄音
    private void stopRecording() {
        if (recorder != null) {
            try {
                recorder.pauseRecording(); // 暫停錄音
                recorder.stopRecording(); // 停止錄音

                // 將錄製的音檔加入列表
                File audioFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "recording_" + currentRecordingIndex + ".wav");
                audioFiles.add(audioFile);
                currentRecordingIndex++;

                Toast.makeText(this, "錄音完成: " + audioFile.getName(), Toast.LENGTH_SHORT).show();
            } catch (RuntimeException e) {
                Log.e("MainActivityc", "錄音停止失敗", e);
                Toast.makeText(this, "錄音停止失敗，請重試", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 上傳音檔到 Flask 伺服器



    // 上傳任務
    // 上傳任務
    private class UploadTask extends AsyncTask<List<File>, Void, String> {
        private String apiUrl;

        // 構造函數，接受自定義的 URL
        public UploadTask(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        @Override
        protected String doInBackground(List<File>... files) {
            try {
                // 創建 HTTPS 連接
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCertificates, null);

                URL url = new URL(apiUrl); // 使用傳入的 URL
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                // 设置请求方法为POST
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + generateBoundary());

                // 启用输入输出流
                connection.setDoOutput(true);

                // 寫入音檔
                try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                    for (int i = 0; i < files[0].size(); i++) {
                        writeFileParameter(os, "file" + (i + 1), files[0].get(i));
                    }
                    os.writeBytes("--" + generateBoundary() + "--");
                }

                // 獲取伺服器回應
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "上傳成功";
                } else {
                    return "上傳失敗，錯誤碼: " + responseCode;
                }
            } catch (Exception e) {
                Log.e("UploadTask", "上傳失敗", e);
                return "上傳失敗: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(MainActivityc.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    // 生成 boundary
    private String generateBoundary() {
        return "-------------pekopeko";
    }

    // 寫入檔案參數
    private void writeFileParameter(DataOutputStream os, String name, File file) throws IOException {
        os.writeBytes("--" + generateBoundary() + "\r\n");
        os.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + file.getName() + "\"\r\n");
        os.writeBytes("Content-Type: audio/wav\r\n\r\n");

        // 寫入檔案內容
        byte[] buffer = new byte[4096];
        int bytesRead;
        try (FileInputStream fis = new FileInputStream(file)) {
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
        os.writeBytes("\r\n");
    }

    // 信任所有證書（僅用於測試）
    private TrustManager[] trustAllCertificates = new TrustManager[]{
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
    @Override
    protected void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.stopRecording();
            recorder = null;
        }
    }
}
