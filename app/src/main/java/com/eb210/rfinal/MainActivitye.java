package com.eb210.rfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
import com.maple.recorder.recording.AudioChunk;
import com.maple.recorder.recording.AudioRecordConfig;
import com.maple.recorder.recording.MsRecorder;
import com.maple.recorder.recording.PullTransport;
import com.maple.recorder.recording.Recorder;

import java.io.File;

public class MainActivitye extends AppCompatActivity {

    Context context;
    Recorder recorder;
    // 錄音按鈕
    private ToggleButton recordBt;
    // 撥放按鈕
    private Button playBt;
    // 音檔站存資料夾
    File RF;
    // 錄音器
    MediaRecorder MR = null;
    // 撥放器
    MediaPlayer MP = null;
    Uri uri;
    TextView T1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activitye);

        T1=findViewById(R.id.T1);

        // 抓取錄音按鈕
        recordBt = (ToggleButton) findViewById(R.id.toggleButton);
        // 抓取撥放按鈕
        playBt = (Button) findViewById(R.id.Button2);

        // 設定一開始不能按的按鈕
        playBt.setEnabled(false);

        // 設定監聽

        playBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 播放流程
                MP = MediaPlayer.create(context, uri);
                MP.start();
                MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer MP) {
                        MP.release();
                    }
                });
            }
        });
    }



    @Override
    protected void onPause() {
        Toast.makeText(this, "Pause", Toast.LENGTH_LONG).show();
        super.onPause();
    }

}
