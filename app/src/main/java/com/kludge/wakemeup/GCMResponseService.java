package com.kludge.wakemeup;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

/*
 * Created by Yu Peng on 10/7/2016.
 */

public class GCMResponseService extends Service {
    String response;
    String userId;
    String username;
    String requestId;
    String targetUsername;
    long timeInMillis;
    String alarmId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationManager n = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // cancels GCM push notification when action (either yes/no) clicked
        n.cancel(GCMListenerService.REQUEST_NOTIFICATION_ID);

        response = intent.getStringExtra("response");
        requestId = intent.getStringExtra("requestId"); //todo: user InstanceID or something
        targetUsername = intent.getStringExtra("targetUsername");
        timeInMillis = intent.getLongExtra("timeInMillis", -1);
        alarmId = intent.getStringExtra("alarmId");

        System.out.println("1 " + timeInMillis);

        SharedPreferences sharedPreferences = getSharedPreferences("preferences_user", MODE_PRIVATE);

        userId = sharedPreferences.getString("userId", "");
        username = sharedPreferences.getString("username", "");

        if (response.equals("yes")) {

            // register with alarm manager to start activity to wake other user
            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            Intent alarmIntent = new Intent(getApplicationContext(), WakerReceiver.class);
            alarmIntent.putExtra("targetId", requestId);
            alarmIntent.putExtra("targetUsername", targetUsername);

            PendingIntent wakeIntent = PendingIntent.getBroadcast(getApplicationContext(), 135,
                    alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // if timeInMillis is before current timeInMillis, add 24hrs
            if (timeInMillis < System.currentTimeMillis())
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis + (long) 8.64e+7, wakeIntent);
            else
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, wakeIntent);

            // include userId
            new ServletPostAsyncTask().execute(new GCMParams(
                    getApplicationContext(), "requestAccepted", userId, username , "", requestId, "", "", alarmId));
        }

        else {
            // include userId
            new ServletPostAsyncTask().execute(new GCMParams(
                    getApplicationContext(), "requestRejected", userId, username, "", requestId, "", "", ""));
        }

        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
