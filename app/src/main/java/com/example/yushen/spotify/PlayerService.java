package com.example.yushen.spotify;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Binder;

import java.io.IOException;

/* The main characteristic of a Service is that it runs in background,
decoupled from Activity life cycle. Imagine the following situation,
you are working on a Media Player application and would like to let
 the users play music,even when they exit the application, in background.
*/
public class PlayerService extends Service {
    MediaPlayer mediaPlayer = new MediaPlayer();
    private final IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //url is passed over
        //services that should only remain running
        // while processing any commands sent to them
        //service ends when the music ends
        if (intent.getStringExtra("myurl") != null)
            playStream(intent.getStringExtra("myurl"));

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i("info", "Start foreground service");
            showNotification();
        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Log.i("info", "Prev pressed");
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            System.out.print("aaaaaaaaaaa");
            togglePlayer();
            Log.i("info", "Play pressed");
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Log.i("info", "Next pressed");
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i("info", "Stop foreground pressed");
            stopForeground(true);
            stopSelf();
        }

        // return START_NOT_STICKY;
        //return START_STICKY;
        return START_REDELIVER_INTENT;
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        //When notification is clicked, this is the main action you would like me to do
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        //FLAG_ACTIVITY_NEW_TASK: If set, this activity will
        // become the start of a new task on this history stack.
        //FLAG_ACTIVITY_CLEAR_TASK:
        //If set in an Intent passed to Context.startActivity(),
        // this flag will cause any existing task that would be
        // associated with the activity to be cleared before the activity is started.

        //start the activity again and clear the activity
        //clear the last played music and start play again
       // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //allow an action to happen as though the main user of this service was calling it,
        // like permission. pendingIntent allows any other app to use this service
        // as though it's their service
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Intent previousIntent = new Intent(this, PlayerService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        //previousIntent does not need flag that is related to activity
        PendingIntent ppreviousIntent = PendingIntent.getActivity(this, 0, previousIntent, 0);

        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getActivity(this, 0, playIntent, 0);

        Intent nextIntent = new Intent(this, PlayerService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getActivity(this, 0, nextIntent, 0);

        Bitmap music = BitmapFactory.decodeResource(getResources(), R.drawable.music);

        //being able to run on all android, compat
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Music Player")
                .setTicker("Playing music")
                .setContentText("My Song")
                .setSmallIcon(R.drawable.music)
                .setLargeIcon(Bitmap.createScaledBitmap(music, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous, "Previous", ppreviousIntent)
                .addAction(android.R.drawable.ic_media_play, "Play", pplayIntent)
                .addAction(android.R.drawable.ic_media_next, "Next", pnextIntent)
                .build();
        //file notification
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);


    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void playStream(String url) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    //mediaPlayer.start();
                    playPlayer();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    flipPlayPauseButton(false);
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void pausePlayer() {
        try {
            mediaPlayer.pause();
            flipPlayPauseButton(false);
        } catch (Exception e) {
            Log.d("EXCEPTION", "failed to pause media player");
        }
    }

    public void playPlayer() {
        try {
            mediaPlayer.start();
            flipPlayPauseButton(true);
        } catch (Exception e) {
            Log.d("EXCEPTION", "failed to pause media player");
        }
    }

    public void flipPlayPauseButton(boolean isPlaying) {
        //Before in pausePlayer/playPlayer, MainActivity.flipPlayPauseButton: problem!
        //If you run the service as a foreground service and your APP DIEs
        // in the background,
        //the service will keep running; service tries to flip, get a crash
        //postman broadcast service
        //communicate with main thread
        Intent intent = new Intent("changePlayButton");
        intent.putExtra("isPlaying", isPlaying);
        //pass over this(points to the current instance,) as a context
        //postman sends...
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void togglePlayer() {
        try {
            if (mediaPlayer.isPlaying())
                pausePlayer();
            else
                playPlayer();
        } catch (Exception e) {
            Log.d("Exception", "failed to toggle media player");
        }
    }
}
