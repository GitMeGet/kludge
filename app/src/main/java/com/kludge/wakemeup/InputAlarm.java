package com.kludge.wakemeup;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Set;

public class InputAlarm extends PreferenceActivity {

    long alarmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_alarm);

        //sets up Preferences fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SettingsFragment settingsFragment = new SettingsFragment();
        fragmentTransaction.add(android.R.id.content, settingsFragment, "SETTINGS_FRAGMENT");
        fragmentTransaction.commit();

        TimePicker viewTimePicker = ((TimePicker) findViewById(R.id.time_picker));
        assert viewTimePicker != null;
        viewTimePicker.setIs24HourView(true);

        /*
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
        */
    }

    public static class SettingsFragment extends PreferenceFragment{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.alarm_preferences);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {





            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }


    public void addAlarm(View view){
        /*
        EditText viewAlarmName = ((EditText) findViewById(R.id.alarm_name));
        assert viewAlarmName != null;
        String alarmName = viewAlarmName.getText().toString();

        TimePicker viewTimePicker = ((TimePicker) findViewById(R.id.time_picker));
        assert viewTimePicker != null;

        Intent data = new Intent();
        data.putExtra("alarm_name", alarmName);
        data.putExtra("hour", viewTimePicker.getHour());
        data.putExtra("minute", viewTimePicker.getMinute());
        if (alarmId != -1){
            data.putExtra("alarmId", alarmId);
        }

        setResult(MainAlarm.RESULT_OK, data);
        */
        finish();
    }
}
