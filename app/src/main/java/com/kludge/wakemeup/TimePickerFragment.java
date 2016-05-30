package com.kludge.wakemeup;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by sylta on 30/5/2016.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //selects DEFAULT TIME when opening dialog
        final Calendar c = Calendar.getInstance(); //retrieves current time
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        //creates new instance of TimePickerDialog then return it, (context, listener, hour, minute, 24hrView?)
        return new TimePickerDialog(getActivity(), this, hour, min, DateFormat.is24HourFormat(getActivity()));
    }

    //does stuff with time selected by user
    public void onTimeSet(TimePicker view, int hour, int min){
        //changes the LAST alarm to the ListAdapter, which should be the alarm with only the name
        MainAlarm.alarms.get(MainAlarm.alarms.size()-1).setTime(hour, min);
        MainAlarm.alarmAdapter.notifyDataSetChanged();
    }
}
