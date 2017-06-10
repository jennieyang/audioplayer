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
import java.util.Random;

public class AudioPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private TextView audioTitleLabel;
    private TextView currentPositionLabel;
    private TextView totalDurationLabel;

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private ArrayList<HashMap<String, String>> audioList = new ArrayList<HashMap<String, String>>();

    private SeekBar progressBar;
    private Handler handler;
    private ProgressHelper progressHelper;
    private int SKIP_TIME = 5000; // 5000 milliseconds forward or backwards
    private int currentAudioIndex = 0;

    private boolean isShuffle = false;
    private boolean isRepeat = false;

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
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        audioTitleLabel = (TextView) findViewById(R.id.songTitle);
        currentPositionLabel = (TextView) findViewById(R.id.currentPositionLabel);
        totalDurationLabel = (TextView) findViewById(R.id.totalDurationLabel);
        progressBar = (SeekBar) findViewById(R.id.progressBar);

        mediaPlayer = new MediaPlayer();
        audioManager = new AudioManager();
        handler = new Handler();
        progressHelper = new ProgressHelper();

        audioList = audioManager.getPlayList();

        progressBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);

        playAudio(0);

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

        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // play next track if it exists, else loop back to beginning of list and play first track
                if (currentAudioIndex < audioList.size() - 1) {
                    playAudio(++currentAudioIndex);
                } else {
                    playAudio(0);
                    currentAudioIndex = 0;
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // play previous track if it exists, else skip to end of list and play last track
                if (currentAudioIndex > 0) {
                    playAudio(--currentAudioIndex);
                } else {
                    playAudio(audioList.size() - 1);
                    currentAudioIndex = audioList.size() - 1;
                }
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isRepeat) {
                    isRepeat = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                } else {
                    isRepeat = true;
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_pressed);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isShuffle) {
                    isShuffle = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                } else {
                    isShuffle = true;
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_pressed);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });
    }

    public void playAudio(int audioIndex) {
        try {
            // Play audio
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioList.get(audioIndex).get("audioPath"));
            mediaPlayer.prepare();
            mediaPlayer.start();

            // Display audio title
            String audioTitle = audioList.get(audioIndex).get("audioTitle");
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

            currentPositionLabel.setText(progressHelper.millisecondsToTime(currentPosition));
            totalDurationLabel.setText(progressHelper.millisecondsToTime(totalDuration));
            handler.postDelayed(this, 100);
        }
    };

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask); // prevents Handler from updating progress bar
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = progressHelper.progressToTime(seekBar.getProgress(), totalDuration);
        mediaPlayer.seekTo(currentPosition); // play audio from selected position
        updateProgressBar(); // update time progress again
    }

    public void onCompletion(MediaPlayer mp) {
        if (isRepeat) {
            // repeat the same track
            playAudio(currentAudioIndex);
        } else if (isShuffle) {
            // play a random track
            Random rand = new Random();
            currentAudioIndex = rand.nextInt(audioList.size() - 1);
            playAudio(currentAudioIndex);
        } else {
            // play next track
            if (currentAudioIndex < audioList.size() - 1) {
                playAudio(++currentAudioIndex);
            } else {
                playAudio(0);
                currentAudioIndex = 0;
            }
        }
    }
}
