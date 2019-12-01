package com.example.file_picker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.file_picker.Utils.Utils;
import com.example.file_picker.databinding.ActivityMainBinding;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    String filee11, filee22;
    Intent chooseFile, chooseFile1;
    String pathh, pathh1;
    RxPermissions rxPermissions = null;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    ActivityMainBinding binding;
    private ProgressDialog mProgressDialog;
    private long startTime;
    private long endTime;
    private Uri audioFile1Uri = null;
    private Uri audioFile2Uri = null;
    private MediaPlayer audioPlayer = null;
    String rp1, rp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        rxPermissions = new RxPermissions(this);

        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("audio/*");
                startActivityForResult(chooseFile, 10);

            }
        });

        binding.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile1 = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile1.setType("audio/*");
                startActivityForResult(chooseFile1, 20);
            }
        });

        binding.btn2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
//                file1 = binding.tv.getText().toString();
//                file2 = binding.tv1.getText().toString();

//                Log.d("First_File", file1);
//                Log.d("Second_File", file2);

                runFFmpegRxJava();
            }
        });


        binding.play1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String showw = binding.tv.getText().toString().trim();

                if (!TextUtils.isEmpty(showw)) {

                    initAudioPlayer();

                    audioPlayer.start();

                    binding.play1.setEnabled(false);
                    binding.stop1.setEnabled(true);
                }

            }
        });

        binding.play2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String showww = binding.tv1.getText().toString().trim();
                if (!TextUtils.isEmpty(showww)) {

                    initAudioPlayer2();

                    audioPlayer.start();
                }
            }
        });


        binding.stop1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioPlayer.isPlaying()) {
                    audioPlayer.stop();
                }

                audioPlayer.release();
                audioPlayer = null;

            }
        });


        binding.stop2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void initAudioPlayer() {

        try {
            if (audioPlayer == null) {
                audioPlayer = new MediaPlayer();

                String showw = binding.tv.getText().toString().trim();

                if (audioFile1Uri != null) {

                    // Play audio from selected local file.
                    audioPlayer.setDataSource(getApplicationContext(), audioFile1Uri);
                }

                audioPlayer.prepare();
            }
        } catch (IOException ex) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void initAudioPlayer2() {

        try {
            if (audioPlayer == null) {
                audioPlayer = new MediaPlayer();
                if (audioFile2Uri != null) {

                    // Play audio from selected local file.
                    audioPlayer.setDataSource(getApplicationContext(), audioFile2Uri);
                }

                audioPlayer.prepare();
            }
        } catch (IOException ex) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    audioFile1Uri = data.getData();
                    filee11 = audioFile1Uri.getPath();
                    binding.tv.setText(filee11);

                    rp1 = binding.tv.getText().toString();
                    rp1 = rp1.replace("/root_path", "");
                    Log.d("abcd", rp1);

//                    filee11 = filee11.replace("/root_path","");
//                    Log.d("pp1",filee11);

                }

                break;

            case 20:
                if (resultCode == RESULT_OK) {
                    audioFile2Uri = data.getData();
                    filee22 = audioFile2Uri.getPath();
                    binding.tv1.setText(filee22);

                    rp2 = binding.tv1.getText().toString();
                    rp2 = rp2.replace("/root_path", "");
                    Log.d("efgh", rp2);


//                    filee22 = filee22.replace("/root_path","");
//                    Log.d("pq1",filee22);


                }

                break;
        }

    }

    private void runFFmpegRxJava() {
        openProgressDialog();

//        String File11 = "/root_path/storage/emulated/0/v22.mp3";
//        String File12 = "/root_path/storage/emulated/0/bkg.mp3";
//        String File13 = "/root_path/storage/emulated/0/o265.mp3";

        String[] command = {"ffmpeg", "-i", rp1, "-i", rp2, "-filter_complex", "amix=inputs=2:duration=first:dropout_transition=20 /storage/emulated/0/o266.mp3"};

        Log.d("Next_First_File", rp1);
        Log.d("Next_Second_File", rp2);

        RxFFmpegInvoke.getInstance().runCommandRxJava(command).subscribe(new RxFFmpegSubscriber() {
            @Override
            public void onFinish() {
                System.out.println("FINISHED MIX");
                if (mProgressDialog != null)
                    mProgressDialog.cancel();
                showDialog("Successful processing");// 处理成功

            }

            @Override
            public void onProgress(int progress) {
                if (mProgressDialog != null)
                    mProgressDialog.setProgress(progress);
            }

            @Override
            public void onCancel() {
                if (mProgressDialog != null)
                    mProgressDialog.cancel();
                showDialog("Cancelled");//已取消
            }

            @Override
            public void onError(String message) {
                if (mProgressDialog != null)
                    mProgressDialog.cancel();
                showDialog("Error onError：" + message);
            }
        });
    }

    public void openProgressDialog() {
        startTime = System.nanoTime();
        mProgressDialog = Utils.openProgressDialog(this);
    }

    private void showDialog(String message) {
        endTime = System.nanoTime();
        Utils.showDialog(this, message, Utils.convertUsToTime((endTime - startTime) / 1000, false));
    }

}

