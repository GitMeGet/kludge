package com.kludge.wakemeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*
 * Created by Yu Peng on 25/6/2016.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("alarm_received", "going to create reminder notification ");

        long alarmId = intent.getLongExtra("alarmId", 0);
        AlarmDetails alarm = AlarmLab.get(context).getAlarmDetails(alarmId);

        MainAlarm.createNotification(1, alarm);
    }
}
