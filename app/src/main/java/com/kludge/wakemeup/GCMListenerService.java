package com.kludge.wakemeup;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/*
 * Created by Yu Peng on 7/7/2016.
 */
public class GCMListenerService extends GcmListenerService {

    private static final String TAG = "GCMListenerService";
    public static final int RESPONSE_NOTIFICATION_ID = 7;
    public static final int REQUEST_NOTIFICATION_ID = 17;


    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */

    /**
     * Production applications would usually process the message here.
     * Eg: - Syncing with server.
     *     - Store message in local database.
     *     - Update UI.
     */


    @Override
    public void onMessageReceived(String from, Bundle data) {
        String messageType = data.getString("messageType");
        String requestId = data.getString("requestId");
        String message = data.getString("message");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + messageType);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        assert messageType != null;
        switch (messageType) {
            case "requestTarget":
                sendRequestNotification(requestId);
                break;
            case "requestAccepted":

                // saves targetId in SharedPrefs
                SharedPreferences sharedPreferences = getSharedPreferences("preferences_user", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("targetId", requestId);
                editor.apply();

                sendResponseNotification(messageType);
                break;
            case "requestRejected":
                sendResponseNotification(messageType);
                break;
            case "incomingP2PMessage":
                // Notify MessagingFragment of new incoming message
                Intent newMessage = new Intent("incomingP2PMessage");
                newMessage.putExtra("message", message);
                boolean b = LocalBroadcastManager.getInstance(this).sendBroadcast(newMessage);

                Log.d(TAG, "sent broadcast" + " " + b);

                break;
        }
    }

    private void sendResponseNotification(String messageType) {

        System.out.println(getSharedPreferences("preferences_user", MODE_PRIVATE).getString("targetId", ""));
        Log.d("GCMListenerService1", getSharedPreferences("preferences_user", MODE_PRIVATE).getString("targetId", ""));

        Intent intent = new Intent(this, MainAlarm.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("GCM Message")
                .setContentText(messageType)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(RESPONSE_NOTIFICATION_ID, notificationBuilder.build());
    }

    private void sendRequestNotification(String requestId) {

        Intent i1 = new Intent(this, GCMResponseService.class);
        i1.putExtra("requestId", requestId);
        i1.putExtra("response", "yes");
        // using FLAG_ONE_SHOT only allows this particular PendingIntent to be used once
        PendingIntent yesIntent = PendingIntent.getService(this, 227, i1, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Tom requests for your services")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .addAction(R.drawable.common_google_signin_btn_icon_dark,
                        "Yes",
                        yesIntent);

        Intent i2 = new Intent(this, GCMResponseService.class);
        i2.putExtra("requestId", requestId);
        i2.putExtra("response", "no");
        PendingIntent noIntent = PendingIntent.getService(this, 117, i2, PendingIntent.FLAG_ONE_SHOT);

        notificationBuilder.addAction(R.drawable.common_google_signin_btn_icon_dark,
                "No",
                noIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(REQUEST_NOTIFICATION_ID, notificationBuilder.build());
    }


}
