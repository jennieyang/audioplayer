package com.jennieyang.audioplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MusicPlayerActivity extends AppCompatActivity {
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private TextView audioTitleLabel;

    private MediaPlayer mediaPlayer;
    private ArrayList<HashMap<String, String>> audioList = new ArrayList<HashMap<String, String>>();

    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds

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

        mediaPlayer = new MediaPlayer();
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
                // forward audio by 5 seconds if possible, else forward to the end
                if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()) {
                    mediaPlayer.seekTo(currentPosition + seekForwardTime);
                } else {
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
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
            btnPlay.setImageResource(R.drawable.btn_pause);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
