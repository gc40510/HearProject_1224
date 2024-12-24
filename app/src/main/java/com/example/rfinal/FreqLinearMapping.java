package com.example.rfinal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FreqLinearMapping extends AppCompatActivity {
//    public boolean activityFlag = false;

    private AudioTrack audioTrack;
    private boolean isPlaying = false;
    private double currentDb; // 用于记录当前分贝数
    private Runnable playbackRunnable;
    private Runnable playSoundRunnable;

    private Gson gson = new Gson();


    private int sampleRate = 44100;
    public static int project_sr = 16000;
    private int bufferSize;
    private double currentFrequency = 500.0; // 初始频率为500Hz
    private double amplitude = 0.0;
    public static double[] gain_value;
    private boolean[] freqSelected = new boolean[8001];

    //private double[] frequencies = {64.0, 125.0, 250.0, 500.0, 1000.0, 2000.0, 4000.0, 8000.0};
    //private double[] amplitudes = new double[8];

    private Handler handler = new Handler();



    int upTemp, upProgress, low, up, lowResult, upResult;
    int Freq = 1024, db;
    public static Map<Integer,Integer> Freq_db = new HashMap<>();
    //########################################################

    SeekBar seekBar2;
    TextView seekText2;
    Button check_db, db_btn, btnFinish;
    Button hz_60, hz_125, hz_250, hz_500, hz_1k, hz_2k, hz_4k, hz_8k;
    TextView setUpTV, hz_TV;
    Spinner freq_selector;
    //########################################################

    // 撥放器
    SoundPool soundPool;
    int soundID;
    boolean AudioFreqIsPlay = false, stop = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freq_linear_mapping);
        //########################################################

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //seekBar2 = findViewById(R.id.seekBar2);
        //seekText2 = findViewById(R.id.seekText2);
        db_btn = findViewById(R.id.dbBtn);
//        setUpTV = findViewById(R.id.setUpBtn);
        check_db = findViewById(R.id.check_db);
        btnFinish = findViewById(R.id.btnFinish);
        //########################################################
        hz_60 = findViewById(R.id.hz60);
        hz_125 = findViewById(R.id.hz125);
        hz_250 = findViewById(R.id.hz250);
        hz_500 = findViewById(R.id.hz500);
        hz_1k = findViewById(R.id.hz1k);
        hz_2k = findViewById(R.id.hz2k);
        hz_4k = findViewById(R.id.hz4k);
        hz_8k = findViewById(R.id.hz8k);
        hz_TV = findViewById(R.id.hzTV);
        freq_selector = findViewById(R.id.freqselector);
        //########################################################

        bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        bufferSize = Math.max(bufferSize, sampleRate);  // 例如，至少设置为与采样率相等

        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
        );

        hz_250.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freq_selector.setSelection(1);
            }
        });

        hz_500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freq_selector.setSelection(3);
            }
        });

        hz_1k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freq_selector.setSelection(7);
            }
        });

        hz_2k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freq_selector.setSelection(15);
            }
        });

        hz_4k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freq_selector.setSelection(18);
            }
        });

        hz_8k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                freq_selector.setSelection(20);
            }
        });

        freq_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = parent.getItemAtPosition(position).toString();
                Freq = Integer.parseInt(selectedValue);
                if (!freqSelected[Freq]) {
                    if (AudioFreqIsPlay) {
                        soundPool.stop(soundID);
                    }
                    AudioFreqIsPlay = false;
                    hz_TV.setText("目前頻率值：" + Freq + "Hz");
                } else {
                    Toast.makeText(FreqLinearMapping.this, Freq + "Hz已經測過了，請選擇其他沒有測過的頻率。", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /*seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress2, boolean b) {
                if(AudioFreqIsPlay == true){
                    soundPool.stop(soundID);
                }
                AudioFreqIsPlay = false;
                upTemp = progress2 / 10;
                upProgress = upTemp * 10;
                frequencyAudio(String.valueOf(upProgress));
                seekText2.setText("拖移植 / 最大值 = " + upProgress + "Hz" +" / " + seekBar.getMax() + "Hz");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(FreqLinearMapping.this, "觸碰SeekBar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(FreqLinearMapping.this, "放開SeekBar", Toast.LENGTH_SHORT).show();
            }
        });*/
        //Handler handler = new Handler();


        /*db_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlaying();
                Log.d("AudioPlay", "After startPlaying");
            }
        });

         */

        db_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaying();
            }
        });

        check_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lowResult = low;
                upResult = up;
                gain_value = Interpolation(1024);
                String json = gson.toJson(gain_value);
                editor.putString("gainvalue", json);
                editor.apply();
                AlertDialog.Builder builder = new AlertDialog.Builder(FreqLinearMapping.this);
                builder.setTitle("確認測試結果");
                builder.setMessage("下限頻率 = " + Freq_db + " Hz"+ "\n上限頻率 = " + Freq_db + " Hz");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        activityFlag = true;
                        Intent intentM = new Intent(FreqLinearMapping.this, MainActivity.class);
                        startActivity(intentM);
                    }
                });
                builder.show();
            }
        });
    }

        private void startPlaying() {
            if (isPlaying) {
                return;
            }
            isPlaying = true;
            currentDb = 0;
            playSound();
        }

        private void stopPlaying() {
            if (!isPlaying) {
                return;
            }
            isPlaying = false;
            freqSelected[Freq] = true;
            Freq_db.put(Freq, (int) currentDb);
            handler.removeCallbacks(playSoundRunnable);

            //currentDbTextView.setText("Stopped at: " + currentDb + "dB");
            if (audioTrack != null) {
                audioTrack.release(); // 釋放audioTrack對象
                audioTrack = null;    // 將audioTrack設置為null
            }
        }

        private void playSound() {
            if (currentDb > 130 || !isPlaying) {
                isPlaying = false;
                if (audioTrack != null) {
                    audioTrack.release();
                    audioTrack = null;
                }
                return;
            }

            final int durationMs = 2000;
            final int numSamples = (int) ((durationMs / 1000.0) * sampleRate);
            final double[] samples = new double[numSamples];
            final byte[] generatedSound = new byte[2 * numSamples];

            // 生成浮点数表示的音频样本
            for (int i = 0; i < numSamples; ++i) {
                samples[i] = Math.sin(2 * Math.PI * i / (sampleRate / Freq));
            }

            // 调整振幅并转换为 short 类型
            int idx = 0;
            double referenceAmplitude = 0.0001; // 假设的参考振幅值，需要通过实验来确定
            double amplitude = referenceAmplitude * Math.pow(10, currentDb / 20.0);

            //用以確認有無超出SHORT範圍，限制在-1~1之間。
            boolean isClipped = false;
            for (double sample : samples) {
                double amplifiedSample = sample * amplitude;
                if (amplifiedSample > 1.0) {
                    amplifiedSample = 1.0;
                    isClipped = true;
                } else if (amplifiedSample < -1.0) {
                    amplifiedSample = -1.0;
                    isClipped = true;
                }
                short shortSample = (short) (amplifiedSample * 32767);
                generatedSound[idx++] = (byte) (shortSample & 0x00ff);
                generatedSound[idx++] = (byte) ((shortSample & 0xff00) >>> 8);
            }

            if (isClipped) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FreqLinearMapping.this, "Amplitude clipped at " + currentDb + "dB", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FreqLinearMapping.this, "Playing at " + currentDb + "dB", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, generatedSound.length,
                    AudioTrack.MODE_STATIC);
            audioTrack.write(generatedSound, 0, generatedSound.length);
            audioTrack.play();

            playSoundRunnable = new Runnable() {
                @Override
                public void run() {
                    audioTrack.release();
                    audioTrack = null;
                    currentDb += 5;
                    playSound();
                }
            };

            handler.postDelayed(playSoundRunnable, durationMs);
        }





        /*db_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                class DBRunnable implements Runnable{
                    int db = 0;
                    @Override
                    public void run() {
                        String temp = Freq + "_" + db;
                        frequencyAudio(temp);
                        Log.v("check_db", String.valueOf(db));
                        Toast.makeText(FreqLinearMapping.this, "目前播放："+temp, Toast.LENGTH_SHORT).show();
                        if(db <= 130){
                            handler.postDelayed(this, 2000);
                        }
                        db += 10;

                    }
                }
                DBRunnable dbRunnable = new DBRunnable();
                handler.post(dbRunnable);
                class StopRunnable implements Runnable{
                    @Override
                    public void run() {
                        Log.v("check_db", "stop");
                        Freq_db.put(Freq, dbRunnable.db);
                        handler.removeCallbacks(dbRunnable);
//                        soundPool.stop(soundID);
                    }
                }
                StopRunnable stopRunnable = new StopRunnable();
                check_db.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handler.post(stopRunnable);
                    }
                });
            }
        });

         */




        /*check_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
            }
        });

         */


        //****************************

    /*private void startPlaying() {
        isPlaying = true;
        //playButton.setText("停止播放");

        audioTrack.play();

        Log.d("AudioPlay", "After audioTrack play");

        playbackRunnable = new Runnable() {
            @Override
            public void run() {
                generateTone();
                Log.d("AudioPlay", "Generate Tone");
                if (isPlaying) {
                    handler.postDelayed(this, 1000); // 每秒更新一次音量
                }
            }
        };

        handler.post(playbackRunnable);
    }
    private void stopPlaying() {
        isPlaying = false;
        //playButton.setText("播放");

        audioTrack.stop();
        //audioTrack.release();
        //audioTrack = null;

        handler.removeCallbacks(playbackRunnable);

        // 记录当前分贝数
        currentDb = amplitudeToDb(amplitude);
        Toast.makeText(FreqLinearMapping.this, "当前分贝数：" + currentDb, Toast.LENGTH_SHORT).show();
    }

    private void generateTone() {
        short[] buffer = new short[bufferSize];

        double angularFrequency = 2.0 * Math.PI * Freq / sampleRate;

        for (int i = 0; i < bufferSize; i++) {
            double time = i / (double) sampleRate;  // 时间变量
            buffer[i] = (short) (amplitude * Math.sin(angularFrequency * time));
        }

        audioTrack.write(buffer, 0, bufferSize);

        // 更新音量，每次递增10dB
        amplitude *= Math.pow(10, 1.0 / 20.0); // 1dB的增量

        Log.d("AudioPlay", "Now amplitude is " + amplitude);
        //if (amplitude > Math.pow(10, 10.0 / 20.0)) { // 100dB上限
        if (amplitudeToDb(amplitude) >= 100) {
            Log.d("AudioPlay", "Limit exceed!!!");
        }
    }

    // 将振幅值转换为分贝值
    private double amplitudeToDb(double amplitude) {
        // 20 * log10(amplitude) 是将振幅值转换为分贝值的公式
        return 20.0 * Math.log10(amplitude);
    }

     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlaying();
    }

    private static int Hz2Index(double hz, int frequencyRate, int frameSize) {
        // 將頻率轉換為相應的索引
        //System.out.println("傳入的頻率 = " + hz);
        hz /= (frequencyRate / (double) frameSize);

        // 將索引轉換為整數並返回
        return (int) hz;
    }

    private double[] Interpolation(int frame_size){
        LinearInterpolator interpolator = new LinearInterpolator();
        int fft_count = frame_size /2 +1;
        double[] x = new double[Freq_db.size()+1], y=new double[Freq_db.size()+1];
        double[] x_Smooth = new double[fft_count], y_Smooth = new double[fft_count];
        int index = 1;
        x[0] = 0;
        y[0] = 0;
        List<Map.Entry<Integer, Integer>> Freq_db_list = new ArrayList<>(Freq_db.entrySet());
        Collections.sort(Freq_db_list, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> entry1, Map.Entry<Integer, Integer> entry2) {
                return entry1.getKey().compareTo(entry2.getKey());
            }
        });
        for (Map.Entry<Integer, Integer> entry : Freq_db_list) {
            x[index] = Hz2Index(entry.getKey(),project_sr,frame_size);
            y[index] = entry.getValue();
            Log.d("FreqAdjust", "x[index]:" + x[index] + " , y[index]:" + y[index]);
            index++;
        }
        PolynomialSplineFunction function = interpolator.interpolate(x, y);
        for (int z=0;z<fft_count;z++){
            x_Smooth[z] = z;
            y_Smooth[z] = function.value(x_Smooth[z]);
        }
        return y_Smooth;
    }

    private void frequencyAudio(String audioNum){
        AudioFreqIsPlay = true;

        String uri = "@raw/" + "pure_" + audioNum + "db"; //音檔路徑和名稱 (原本是sin開頭 改pure)

        int soundIDResource = getResources().getIdentifier(uri, null, getPackageName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(10).build();
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