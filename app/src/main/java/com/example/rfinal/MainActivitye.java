package com.example.rfinal;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.maple.recorder.recording.AudioChunk;
import com.maple.recorder.recording.AudioRecordConfig;
import com.maple.recorder.recording.MsRecorder;
import com.maple.recorder.recording.PullTransport;
import com.maple.recorder.recording.Recorder;

import java.io.File;
import java.io.IOException;

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
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://finalt01.appspot.com");
    StorageReference storageRef = storage.getReference();
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
        recordBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordBt.isChecked()) {
                    // 錄音流程
                    try {

                        recorder = MsRecorder.wav(
                            new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/a01.wav"),
                            new AudioRecordConfig(),
                            new PullTransport.Default()
                                .setOnAudioChunkPulledListener(new PullTransport.OnAudioChunkPulledListener() {
                                    @Override
                                    public void onAudioChunkPulled(AudioChunk audioChunk) {
                                        Log.e("TTT1", "amplitude: " + audioChunk.maxAmplitude());
                                    }
                                })

                        );

                        recorder.startRecording(); // 开始

                        }catch (Exception e){
                            Log.e("TTT1", "G1");
                        }

                } else {
                    // 停止錄音

                        try {
                            recorder.pauseRecording(); // 暂停
                            //recorder.resumeRecording(); // 重新開始
                            recorder.stopRecording(); // 結束

                            //StorageReference as = storageRef.child("aa.amr");
                            Uri file = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/a01.wav"));
                            StorageReference riversRef = storageRef.child(file.getLastPathSegment());
                            UploadTask uploadTask = riversRef.putFile(file);

                            // Register observers to listen for when the download is done or if it fails
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    T1.setText(T1.getText()+"F4");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    // ...
                                    T1.setText(T1.getText()+"onSuccess");
                                }
                            });

                        }catch (Exception e){
                            Log.e("TTT1", "G2");
                        }

                    }
                }

        });
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
