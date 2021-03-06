package com.kludge.wakemeup;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class RingtoneService extends Service {

    MediaPlayer alarm_ringer;
    AudioManager am;

    public RingtoneService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.



        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int START_ID) {
        //todo: read from filesystem
        //File sd = Environment.getExternalStorageDirectory();
        //String path = sd.getAbsolutePath() + "MH Song.wav";

        // set volume to maximum
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_ALARM,
                am.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                0);



            // play ringtone
            alarm_ringer = MediaPlayer.create(getApplicationContext(), Uri.parse(intent.getStringExtra("ringtone")));
            alarm_ringer.setLooping(true);

            alarm_ringer.start();


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        alarm_ringer.stop();

        // Abandon audio focus when playback complete

        super.onDestroy();
    }
}
