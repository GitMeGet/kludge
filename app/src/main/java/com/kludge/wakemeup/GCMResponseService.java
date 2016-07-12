package com.kludge.wakemeup;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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
        requestId = intent.getStringExtra("requestId");

        Log.v("GCMResponseService", "hello");

        if (response.equals("yes")) {
            // include userId
            Log.i("dumb", "i am stupid");
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
