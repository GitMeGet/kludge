package com.kludge.wakemeup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmWake extends FragmentActivity {

    public static final int MATH_GAME = 1;
    public static final int PONG_GAME = 2;
    public AlarmDetails alarm;
    public PowerManager.WakeLock wakeLock;

    Intent ringService;

    UserManager userManager;

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

        userManager = new UserManager(getApplicationContext());

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

        final TextView userScore = (TextView) findViewById(R.id.view_user_score);
        assert userScore != null;
        userScore.setText("You have "+userManager.getScore()+(userManager.getScore()==1?" point.":" points."));

        Button buttDismiss = (Button) findViewById(R.id.butt_dismiss);
        assert buttDismiss != null;
        buttDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i;

                userManager.increaseScore(UserManager.SCORE_THRESHOLD - userManager.getSnooze());
                userManager.resetSnooze();

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

        if(userManager.getScore() <= 0){
            buttonSnooze.setTextColor(getColor(R.color.colorLightGrey)); //set font to light grey colour
        }
        buttonSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userManager.getScore() <= 0) { //disable the snooze button if not enough points
                    Toast.makeText(getApplicationContext(), ("You don't have enough points!"), Toast.LENGTH_SHORT).show();

                }
                else{
                    //cancel RingtoneService and PendingIntent
                    stopService(ringService);

                    userManager.decreaseScore(UserManager.SNOOZE_PRICE);

                    // re-register snoozed alarm
                    alarm.registerAlarmIntent(c, AlarmDetails.SNOOZE_ALARM);

                    Toast.makeText(getApplicationContext(), ("Alarm will ring in " + alarm.getnSnooze() + " minutes"), Toast.LENGTH_SHORT).show();


                    // release wake_lock
                    wakeLock.release();

                    finish();
                }
            }
        });

        // init fragment if there's a targetId
        if (!alarm.getTargetId().equals("")) {
            // check if activity using layout with fragment_container FrameLayout
            if (findViewById(R.id.fragment_container) != null) {

                // don't do anything if being restored from previous saved state
                // or could have overlapping fragments
                if (savedInstanceState != null)
                    return;

                // create new MessagingFragment to place inside fragment_container
                MessagingFragment messagingFragment = new MessagingFragment();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                Bundle bundle = new Bundle();
                bundle.putLong("alarmId", alarmId);
                messagingFragment.setArguments(bundle);

                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, messagingFragment).commit();
            }
        }
    }

    // disables back button
    @Override
    public void onBackPressed() {
    }
}
