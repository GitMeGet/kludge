package com.kludge.wakemeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * Created by Yu Peng on 18/7/2016.
 */
public class WakerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String targetId = intent.getStringExtra("targetId");

        System.out.println("2 " + targetId);

        Intent startWaker = new Intent(context, MessagingActivity.class);
        startWaker.putExtra("targetId", targetId);
        startWaker.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(startWaker);

    }
}
