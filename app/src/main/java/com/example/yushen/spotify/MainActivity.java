package com.example.yushen.spotify;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.media.MediaPlayer;
import android.media.AudioManager;
import java.io.IOException;
import android.content.Intent;
public class MainActivity extends AppCompatActivity {

    static FloatingActionButton playPauseButton;
    PlayerService mBoundService;
    boolean mServiceBound =false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.MyBinder myBinder = (PlayerService.MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPlaying =  intent.getBooleanExtra("isPlaying", false);
            flipPlayPauseButton(isPlaying);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //fab is referencing the button
        playPauseButton = (FloatingActionButton) findViewById(R.id.fab);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   Player.player.togglePlayer();

                if(mServiceBound)
                    mBoundService.togglePlayer();
            }
        });

       // String url = "http://www.yusmusic.site88.net/yusmusic/cute.mp3";
       /* if (Player.player == null)
            new Player();

        Player.player.playStream(url);*/
        startStreamingService("http://www.yusmusic.site88.net/yusmusic/cute.mp3");
    }

    private void startStreamingService(String url){
        Intent i = new Intent(this, PlayerService.class);
        i.putExtra("myurl", url);
        i.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(i);
        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        //when you swipe away the app, the service go away as well
        super.onStop();
        if(mServiceBound){
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //If someone sends out a message that has an intentfilter "changePlayButton",
        //I want to know and send to mMessageReceiver;
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("changePlayButton"));
    }

    //When activity disappers, onpause() gets filed

    /*As your activity enters the paused state, the system calls
    the onPause() method on your Activity, which allows you to
     stop ongoing actions that should not continue while paused
      (such as a video) or persist any information that should
       be permanently saved in case the user continues to leave your app.
    */
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public static void flipPlayPauseButton(boolean isPlaying){
        if(isPlaying){
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        }else{
            //isPlaying is false, play!
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
