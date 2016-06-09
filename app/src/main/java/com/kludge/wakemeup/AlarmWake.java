package com.kludge.wakemeup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AlarmWake extends AppCompatActivity {

    Intent ringService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_wake);

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
                finish();
            }
        });
    }
}
