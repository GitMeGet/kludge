package com.kludge.wakemeup;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/*
 * Created by Yu Peng on 7/7/2016.
 */
public class GCMListenerService extends FirebaseMessagingService {

    private static final String TAG = "GCMListenerService";
    public static final int RESPONSE_NOTIFICATION_ID = 7;
    public static final int REQUEST_NOTIFICATION_ID = 17;

    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


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
    public void onMessageReceived(RemoteMessage rMessage) {
        String from = rMessage.getFrom();
        Map data = rMessage.getData();
        String notfication = "";

        if(rMessage.getNotification() != null)
            notfication = rMessage.getNotification().getBody();

        String messageType = (String) data.get("messageType");
        String userId = (String)data.get("userId");
        String username = (String)data.get("username");
        String message = (String)data.get("message");
        String timeInMillis = (String)data.get("timeInMillis");
        String alarmId = (String) data.get("alarmId");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + messageType);
        Log.d(TAG, "Notif: " + notfication);

        if(notfication != null){
            System.out.println(notfication);
        }

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }



        assert messageType != null;
        switch (messageType) {
            case "requestTarget":
                sendRequestNotification(userId, username, timeInMillis, message, alarmId);
                break;
            case "requestAccepted":

                // save targetId with specified alarmId
                AlarmDetails alarm = AlarmLab.get(getApplicationContext())
                        .getAlarmDetails(Long.parseLong(alarmId));

                alarm.setTargetId(userId);

                sendResponseNotification(messageType);
                break;
            case "requestRejected":
                sendResponseNotification(messageType);
                break;
            case "incomingP2PMessage":
                // Notify MessagingFragment of new incoming message
                Intent newMessage = new Intent("incomingP2PMessage");
                newMessage.putExtra("message", message);
                newMessage.putExtra("targetId", userId);
                boolean b = LocalBroadcastManager.getInstance(this).sendBroadcast(newMessage);

                Log.d(TAG, "sent incomingP2PMessage broadcast" + " " + b);

                break;
        }
    }

    private void sendResponseNotification(String messageType) {

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

    private void sendRequestNotification(String requestId, String username,String timeInMillis, String message, String alarmId) {

        // convert String timeInMillis to readable time
        Calendar calendar = Calendar.getInstance();

        long lTimeInMillis = Long.parseLong(timeInMillis);
        calendar.setTimeInMillis(lTimeInMillis);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);

        String min = nf.format(minute);
        String time = "" + hour + ":" + min;

        Intent i1 = new Intent(this, GCMResponseService.class);
        i1.putExtra("requestId", requestId);
        i1.putExtra("response", "yes");
        i1.putExtra("timeInMillis", lTimeInMillis);
        i1.putExtra("alarmId", alarmId);

        // using FLAG_ONE_SHOT only allows this particular PendingIntent to be used once
        PendingIntent yesIntent = PendingIntent.getService(this, 227, i1, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle( username + " @ " + time)
                .setContentText(message)
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
