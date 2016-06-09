package com.kludge.wakemeup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

public class InputAlarm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_alarm);

        TimePicker viewTimePicker = ((TimePicker) findViewById(R.id.time_picker));
        viewTimePicker.setIs24HourView(true);
    }


    public void addAlarm(View view){
        EditText viewAlarmName = ((EditText) findViewById(R.id.alarm_name));
        assert viewAlarmName != null;
        String alarmName = viewAlarmName.getText().toString();

        TimePicker viewTimePicker = ((TimePicker) findViewById(R.id.time_picker));

        Intent data = new Intent();
        data.putExtra("alarm_name", alarmName);
        data.putExtra("hour", viewTimePicker.getHour());
        data.putExtra("minute", viewTimePicker.getMinute());

        setResult(MainAlarm.RESULT_OK, data);
        finish();
    }
}
