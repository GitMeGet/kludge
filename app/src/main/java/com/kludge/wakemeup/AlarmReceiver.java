package com.kludge.wakemeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Zex on 8/6/2016.
 * yes pls
 */
public class AlarmReceiver extends BroadcastReceiver {

    //start AlarmWake activity w/ RingtoneService
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("alarm_received", "going to start AlarmWake");
        Intent startAlarm = new Intent(context, AlarmWake.class);
        startAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(startAlarm);
    }
}
