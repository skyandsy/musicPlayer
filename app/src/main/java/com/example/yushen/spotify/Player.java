package com.example.yushen.spotify;

/**
 * Created by yushen on 2/16/16.
 */


import android.media.MediaPlayer;
import android.media.AudioManager;
import android.util.Log;

import java.io.*;

import static android.util.Log.*;

public class Player {
    MediaPlayer mediaPlayer = new MediaPlayer();
    public static Player player;
    String url = "";
    //initialize
    public Player(){
        this.player = this;
    }

    //url contains mp3 file
    public void playStream(String url){
        if(mediaPlayer!=null){
            try{
                mediaPlayer.stop();
            }catch(Exception e){
                e.printStackTrace();
            }
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try{
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    //mediaPlayer.start();
                    playPlayer();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                public void onCompletion(MediaPlayer mp){
                    MainActivity.flipPlayPauseButton(false);
                }
            });
            mediaPlayer.prepareAsync();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void pausePlayer(){
        try{
            mediaPlayer.pause();
            MainActivity.flipPlayPauseButton(false);
        }catch(Exception e){
            Log.d("EXCEPTION", "failed to pause media player");
        }
    }
    public void playPlayer(){
        try{
            mediaPlayer.start();
            MainActivity.flipPlayPauseButton(true);
        }catch(Exception e){
            Log.d("EXCEPTION", "failed to pause media player");
        }
    }

    public void togglePlayer(){
        try{
            if(mediaPlayer.isPlaying())
                pausePlayer();
            else
                playPlayer();
        }catch(Exception e){
            Log.d("Exception", "failed to toggle media player");
        }
    }
}
