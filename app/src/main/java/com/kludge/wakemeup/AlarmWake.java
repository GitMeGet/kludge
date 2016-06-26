package com.kludge.wakemeup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AlarmWake extends AppCompatActivity {

    public static final int MATH_GAME = 1;
    public static final int PONG_GAME = 2;
    public AlarmDetails alarm;
    public PowerManager.WakeLock wakeLock;

    Intent ringService;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            //cancel RingtoneService and PendingIntent
            stopService(ringService);

            //check if REPEAT is on
            if (alarm.isRepeat()) {
                alarm.registerAlarmIntent(getApplicationContext(), AlarmDetails.ADD_ALARM);
                alarm.setOnState(true);
            } else
                alarm.setOnState(false);

            // release wake_lock
            wakeLock.release();
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_wake);

        final Context c = getApplicationContext();
        long alarmId = getIntent().getLongExtra("alarmId", 0);
        alarm = AlarmLab.get(c).getAlarmDetails(alarmId);

        // wake_lock
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        //  force the screen and/or keyboard to turn on immediately, when the WakeLock is acquired
                        PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "MyWakelockTag");
        wakeLock.acquire();

        //make it RING
        ringService = new Intent(this, RingtoneService.class);
        ringService.putExtra("ringtone", alarm.getRingtone());
        startService(ringService);

        Button buttDismiss = (Button) findViewById(R.id.butt_dismiss);
        assert buttDismiss != null;
        buttDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i;

                switch(alarm.getGame()){
                    case AlarmDetails.GAME_DISABLED:
                        stopService(ringService);
                        wakeLock.release();
                        finish();
                        break;
                    case AlarmDetails.GAME_MATH:
                        i = new Intent(c, MathGameActivity.class);
                        i.putExtra("mathqns", alarm.getMathQns());
                        startActivityForResult(i, MATH_GAME);
                        break;
                    case AlarmDetails.GAME_PONG:
                        i = new Intent(c, PongGameActivity.class);
                        startActivityForResult(i, PONG_GAME);
                        break;
                }
            }
        });

        Button buttonSnooze = (Button) findViewById(R.id.butt_snooze_alarm);
        assert buttonSnooze != null;
        buttonSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel RingtoneService and PendingIntent
                stopService(ringService);

                // re-register snoozed alarm
                alarm.registerAlarmIntent(c, AlarmDetails.SNOOZE_ALARM);

                Toast.makeText(getApplicationContext(), ("Alarm will ring in " + alarm.getnSnooze() + " minutes"), Toast.LENGTH_SHORT).show();


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
