package com.jennieyang.audioplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AudioPlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private TextView audioTitleLabel;

    private MediaPlayer mediaPlayer;
    private ArrayList<HashMap<String, String>> audioList = new ArrayList<HashMap<String, String>>();

    private SeekBar progressBar;
    private Handler handler;
    private ProgressHelper progressHelper;
    private int SKIP_TIME = 5000; // 5000 milliseconds forward or backwards

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        // request permissions from user
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }
        }

        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        audioTitleLabel = (TextView) findViewById(R.id.songTitle);
        progressBar = (SeekBar) findViewById(R.id.progressBar);

        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        progressHelper = new ProgressHelper();

        progressBar.setOnSeekBarChangeListener(this);
        playAudio();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    // audio is already playing, so pause it and change pause button to play
                    if(mediaPlayer !=null){
                        mediaPlayer.pause();
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                } else {
                    // audio is paused, so resume playing it and change play button to pause
                    if(mediaPlayer !=null){
                        mediaPlayer.start();
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }

            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                // skip forward audio by 5 seconds if possible, else skip to the end
                if (currentPosition + SKIP_TIME <= mediaPlayer.getDuration()) {
                    mediaPlayer.seekTo(currentPosition + SKIP_TIME);
                } else {
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                }
            }
        });

        btnBackward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                // skip back audio by 5 seconds if possible, else skip back to the start
                if (currentPosition - SKIP_TIME >= 0) {
                    mediaPlayer.seekTo(currentPosition - SKIP_TIME);
                } else {
                    mediaPlayer.seekTo(0);
                }
            }
        });
    }

    public void playAudio() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/Music/song.mp3");
            mediaPlayer.prepare();
            mediaPlayer.start();
            String audioTitle = "Song";
            audioTitleLabel.setText(audioTitle);
            btnPlay.setImageResource(R.drawable.btn_pause); // change button image to pause

            progressBar.setProgress(0);
            progressBar.setMax(100);
            updateProgressBar();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        // this is required to implement SeekBar.OnSeekBarChangeListener
    }

    public void updateProgressBar() {
        handler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int totalDuration = mediaPlayer.getDuration();
            int progress = progressHelper.getProgressPercentage(currentPosition, totalDuration);
            progressBar.setProgress(progress);
            handler.postDelayed(this, 100);
        }
    };

    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask); // prevents Handler from updating progress bar
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = progressHelper.progressToTime(seekBar.getProgress(), totalDuration);
        mediaPlayer.seekTo(currentPosition); // play audio from selected position
        updateProgressBar(); // update time progress again
    }
}
