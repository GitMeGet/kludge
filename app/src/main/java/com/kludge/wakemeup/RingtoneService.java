package com.kludge.wakemeup;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;

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



        alarm_ringer = MediaPlayer.create(getApplicationContext(), R.raw.souls);
        alarm_ringer.start();


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        alarm_ringer.stop();

        super.onDestroy();
    }
}
