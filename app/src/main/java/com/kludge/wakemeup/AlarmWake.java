package com.kludge.wakemeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class AlarmWake extends FragmentActivity {

    public static final int MATH_GAME = 1;
    public static final int PONG_GAME = 2;
    public AlarmDetails alarm;
    public PowerManager.WakeLock wakeLock;

    Intent ringService;
    UserManager userManager;
    private TextToSpeech mTextToSpeech;
    private boolean isP2PReceiverRegistered;
    public static ArrayList<Pair<String, String>> messageArrayList;



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
        final long alarmId = getIntent().getLongExtra("alarmId", 0);
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


        Button mStartP2PMessaging = (Button) findViewById(R.id.startP2PMessagingButton);

        // make button invisible
        mStartP2PMessaging.setVisibility(View.INVISIBLE);

        // init fragment if there's a targetId
        if (!alarm.getTargetId().equals("")) {

            final String targetId = alarm.getTargetId();

            // enable startP2PMessaging button
            mStartP2PMessaging.setVisibility(View.VISIBLE);
            mStartP2PMessaging.setText("Contact " + targetId);

            mStartP2PMessaging.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getApplicationContext(), MessagingActivity.class);

                    i.putExtra("targetId", targetId);

                    startActivity(i);
                }
            });

            messageArrayList = new ArrayList<>();

            initTextToSpeech();

            registerReceiver();


            /*
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
            */
        }


    }

    private void initTextToSpeech(){
        mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                    mTextToSpeech.setLanguage(Locale.UK);
                else
                    Toast.makeText(getParent(), "textToSpeech init failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerReceiver() {
        if (!isP2PReceiverRegistered) {
            LocalBroadcastManager.getInstance(getParent()).registerReceiver(mP2PMessageBroadcastReceiver,
                    new IntentFilter("incomingP2PMessage"));
            isP2PReceiverRegistered = true;
        }
    }

    // set broadcast receiver to listen from GCMListenerService
    private BroadcastReceiver mP2PMessageBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            String targetId = intent.getStringExtra("targetId");

            Pair<String, String> incomingP2PMessage = new Pair<>(targetId, message);
            messageArrayList.add(incomingP2PMessage);

            // read out incomingP2P message
            mTextToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, "TextToSpeechIncomingP2PMessage");
        }
    };

    // disables back button
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {

        // unregister P2P message broadcast receiver
        LocalBroadcastManager.getInstance(getParent()).unregisterReceiver(mP2PMessageBroadcastReceiver);
        isP2PReceiverRegistered = false;

        // release text to speech resources
        mTextToSpeech.stop();
        mTextToSpeech.shutdown();

        super.onDestroy();
    }
}
