package com.kludge.wakemeup;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

/*
 * Created by Yu Peng on 10/7/2016.
 */

public class GCMResponseService extends Service {
    String response;
    String requestId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationManager n = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // cancels GCM push notification when action (either yes/no) clicked
        n.cancel(GCMListenerService.REQUEST_NOTIFICATION_ID);

        response = intent.getStringExtra("response");
        requestId = intent.getStringExtra("requestId"); //todo: user InstanceID or something

        if (response.equals("yes")) {

            // saves targetId in SharedPrefs
            SharedPreferences sharedPreferences = getSharedPreferences("preferences_user", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("targetId", requestId);
            editor.apply();

            // include userId
            new GCMRegistrationIntentService.ServletPostAsyncTask().execute(new GCMParams(
                    getApplicationContext(), "requestAccepted", GCMRegisterActivity.userId, "", requestId, ""));
        }

        else {
            // include userId
            new GCMRegistrationIntentService.ServletPostAsyncTask().execute(new GCMParams(
                    getApplicationContext(), "requestRejected", GCMRegisterActivity.userId, "", requestId, ""));
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
