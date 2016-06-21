package com.kludge.wakemeup;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

public class RingtoneService extends Service {

    MediaPlayer alarm_ringer;

    public RingtoneService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.



        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int START_ID){
        //todo: read from filesystem
        //File sd = Environment.getExternalStorageDirectory();
        //String path = sd.getAbsolutePath() + "MH Song.wav";

        // set volume to maximum
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
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

        // reset original volume level


        super.onDestroy();
    }
}
