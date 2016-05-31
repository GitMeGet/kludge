package com.kludge.wakemeup;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

public class MainAlarm extends AppCompatActivity {

    //array containing DESCRIPTION OF ALARMS? !!!! MUST IT BE STATIC???
    static ArrayList<AlarmDetails> alarms = new ArrayList<AlarmDetails>();

    //arrayAdapter for the ListView
    static AlarmAdapter alarmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alarm);

        //initiates AlarmAdapter for ListView, (context, layout, strArray)
        alarmAdapter = new AlarmAdapter(this, alarms);

        //creates ListView to populate alarms, attach the adapter to this ListView
        ListView listView = (ListView) findViewById(R.id.list_alarms);
        listView.setAdapter(alarmAdapter);

        //sets up the longClick listener for the ListView to configure individual alarms
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                return false;
            }
        });

        //add_alarm button
        Button addAlarm = (Button) findViewById(R.id.add_alarm);
        assert addAlarm != null;
        addAlarm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                //adds alarm
                addAlarm();
            }
        });
    }

    //adds alarm to the alarms ArrayList
    private void addAlarm() {
        //fetches EditText field
        String alarmName = ((EditText) findViewById(R.id.alarm_name)).getText().toString();
        alarms.add(new AlarmDetails(0,0,alarmName));

        showTimePickerDialog();
    }

    //show the timePicker dialog inside a DialogFragment
    private void showTimePickerDialog(){
        DialogFragment newFrag = new TimePickerFragment();
        newFrag.show(getSupportFragmentManager(), "timePicker"); //requires instance of a FragmentManager, + unique tag for this fragment
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        //save info by doing eg. outState.putString("key", varName), outState.putFloatArray("key",..
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        //restore info by taking it out eg. var = savedInstanceState.getString("key");
    }
}

//AlarmAdapter for the ListView
class AlarmAdapter extends ArrayAdapter<AlarmDetails> {
    public AlarmAdapter(Context context, ArrayList<AlarmDetails> alarmList){
        super(context, 0, alarmList);
    }

    //returns actual View to be displayed as row within the alarm ListView
    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        //retrieves alarms array for this position
        final AlarmDetails alarm = getItem(pos);

        //check if existing view is being reused, else just inflate the view with custom alarm_list_item xml
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_list_item, parent, false);

        //lookup the Views to be populated, ie. alarm name and alarm time
        TextView alarmName = (TextView) convertView.findViewById(R.id.alarm_name);
        TextView alarmTime = (TextView) convertView.findViewById(R.id.alarm_time);

        //gets the switch widget for the View
        Switch aSwitch = (Switch) convertView.findViewById(R.id.alarm_on_state);

        //if the alarm state was on, set aSwitch accordingly


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm.toggleOnState();

                notifyDataSetChanged();
            }
        });

        //updates the Views with the data
        alarmName.setText(alarm.strName);
        alarmTime.setText(alarm.nHour+":"+alarm.nMin+(alarm.bOnState?" ON":" OFF")); //CHANGE THIS, ON OFF JUST TO TEST ONLY

        //return completed view to render on screen
        return convertView;
    }
}


