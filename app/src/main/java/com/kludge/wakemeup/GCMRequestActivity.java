package com.kludge.wakemeup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

/*
 * Created by Yu Peng on 19/7/2016.
 */
public class GCMRequestActivity extends AppCompatActivity {

    public static final String SENDER_ID = "744483356919";

    public static String targetId; // save targetId too!
    EditText mTargetIdEditText;

    private AlarmDetails alarm;

    private String userId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_gcm);

        userId = getSharedPreferences("preferences_user", MODE_PRIVATE).getString("userId", "");

        // get alarm from AlarmLab
        final long alarmId = getIntent().getLongExtra("alarmId", -1);
        alarm = AlarmLab.get(getApplicationContext()).getAlarmDetails(alarmId);

        mTargetIdEditText = (EditText) findViewById(R.id.targetIdEditText);

        // if user had previously entered targetId, show it
        targetId = alarm.getTargetId();
        mTargetIdEditText.setText(targetId);

        // get timeInMillis
        final String timeInMillis = Long.toString(alarm.getTimeInMillis());

        // get message
        final String requestMessage = "I have important business tmr";

        Button mRequestTargetIdButton = (Button) findViewById(R.id.requestTargetIdButton);
        assert mRequestTargetIdButton != null;
        mRequestTargetIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make sure both input fields are filled
                if (!validateInputs())
                    return;

                targetId = mTargetIdEditText.getText().toString();

                //todo://IMPLEMENT THIS
                FirebaseMessaging fm = FirebaseMessaging.getInstance();
                fm.send(new RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                        .setMessageId("234")//Integer.toString(msgId.incrementAndGet()))
                        .addData("my_message", "Hello World")
                        .addData("my_action","SAY_HELLO")
                        .addData("messageType", "requestTarget")
                        .addData("userId", userId)
                        .addData("targetId", targetId)
                        .addData("timeInMillis", timeInMillis)
                        .addData("requestMessage", requestMessage)
                        .addData("alarmId", Long.toString(alarmId))
                        .build());

                new ServletPostAsyncTask().execute(new GCMParams(
                        getApplicationContext(), "requestTarget", userId , "", targetId,
                        timeInMillis, requestMessage, Long.toString(alarmId)));
            }
        });

        Button mP2PMessagingButton = (Button) findViewById(R.id.buttonP2PMessaging);
        assert mP2PMessagingButton != null;
        mP2PMessagingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MessagingActivity.class);
                i.putExtra("targetId", targetId);
                startActivity(i);
            }
        });


    }

    private boolean validateInputs(){
        // if no targetId provided by user
        if (targetId.equals("") && mTargetIdEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "OI! Fill in targetId leh!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
