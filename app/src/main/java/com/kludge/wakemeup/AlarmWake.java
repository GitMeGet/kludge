package com.kludge.wakemeup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class AlarmWake extends AppCompatActivity {

    public static final int MATH_GAME = 1;

    Intent ringService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_wake);

        final Context c = getApplicationContext();
        long alarmId = getIntent().getLongExtra("alarmId", 0);
        final AlarmDetails alarm = AlarmLab.get(c).getAlarmDetails(alarmId);

        // wake_lock
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                //  force the screen and/or keyboard to turn on immediately, when the WakeLock is acquired
                PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "MyWakelockTag");
        wakeLock.acquire();

        //make it RING
        ringService = new Intent(this, RingtoneService.class);
        startService(ringService);

        Button buttDismiss = (Button) findViewById(R.id.butt_dismiss_alarm);
        assert buttDismiss != null;
        buttDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel RingtoneService and PendingIntent
                stopService(ringService);

                // release wake_lock
                wakeLock.release();

                finish();
            }
        });

        Button buttonSnooze = (Button) findViewById(R.id.buttonSnooze);
        assert buttonSnooze != null;
        buttonSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel RingtoneService and PendingIntent
                stopService(ringService);

                // re-register snoozed alarm
                alarm.registerAlarmIntent(c, AlarmDetails.SNOOZE_ALARM);

                // release wake_lock
                wakeLock.release();

                finish();
            }
        });
    }

    // disables back button
    @Override
    public void onBackPressed() {
    }
}
