package com.kludge.wakemeup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class InputAlarm extends AppCompatActivity {

    long alarmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_alarm);

        TimePicker viewTimePicker = ((TimePicker) findViewById(R.id.time_picker));
        viewTimePicker.setIs24HourView(true);

        TextView buttonSave = (TextView) findViewById(R.id.butt_add_alarm);
        buttonSave.setText("Add");

        // initialise view objects if editing existing alarm
        alarmId = getIntent().getLongExtra("alarmId", -1);

        if (alarmId != -1){

            AlarmDetails alarm = AlarmLab.get(getBaseContext()).getAlarmDetails(alarmId);
            viewTimePicker.setHour(alarm.getHour());
            viewTimePicker.setMinute(alarm.getMin());

            EditText alarmName = (EditText) findViewById(R.id.alarm_name);
            alarmName.setText(alarm.getName());

            buttonSave.setText("Save");
        }
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
        if (alarmId != -1){
            data.putExtra("alarmId", alarmId);
        }

        setResult(MainAlarm.RESULT_OK, data);
        finish();
    }
}
