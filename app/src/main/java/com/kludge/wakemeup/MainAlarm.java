package com.kludge.wakemeup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class MainAlarm extends AppCompatActivity {

    //keys
    static final int ID_ADD_ALARM = 100;
    static final int ID_EDIT_ALARM = 101;

    static final int ID_CONTEXT_EDIT = 200;
    static final int ID_CONTEXT_DELETE = 201;

    static ArrayList<AlarmDetails> alarms; //array containing DESCRIPTION OF ALARMS? !!!! MUST IT BE STATIC???
    static AlarmAdapter alarmAdapter; //arrayAdapter for the ListView

    // for notifications
    private static Context mContext; // not sure if this is a good idea???
    private static Resources r;
    private static NotificationManager notificationManager;

    protected static void createNotification() {

        Intent i = new Intent(mContext, MainAlarm.class);
        PendingIntent pi = PendingIntent.getActivity(mContext, 0, i, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        String time = "No Alarm Set";
        String hTA = "";

        AlarmDetails earliestAlarm = AlarmLab.get(mContext).getEarliestAlarm();
        if (earliestAlarm != null){
            time = "Next Alarm: " + earliestAlarm.getHour() + ":" + earliestAlarm.getMin();

            double hoursToAlarm = (earliestAlarm.getTimeInMillis() - System.currentTimeMillis())*(2.77778e-7);
            hTA = "Hours till wake: " + String.format("%.2f",hoursToAlarm);

        }

        mBuilder.setContentTitle(time)
                .setContentText(hTA)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                // persistent
                .setOngoing(true)
                .setContentIntent(pi);

        Notification notification = mBuilder.build();

        notificationManager.notify(0, notification);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alarm);

        // for notifications
        mContext = this;
        r = getResources();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotification();

        //initialise alarmManager
        //alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // initialise alarms ArrayList if empty, else load from FILE
        alarms = AlarmLab.get(getApplicationContext()).getAlarms();

        //initiates AlarmAdapter for ListView, (context, layout, strArray)
        alarmAdapter = new AlarmAdapter(this, alarms);

        //sets up listView and attach the adapter to this ListView
        ListView listView = (ListView) findViewById(R.id.list_alarms);
        assert listView != null;
        listView.setAdapter(alarmAdapter);


        //sets up addAlarm button and listener
        FloatingActionButton buttAddAlarm = (FloatingActionButton) findViewById(R.id.float_add_alarm);
        assert buttAddAlarm != null;
        buttAddAlarm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //opens add alarm activity
                requestAddAlarm();
            }
        });

        //sets up listView for contextMenu
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        switch (view.getId()) {
            case R.id.list_alarms:
                menu.add(0, ID_CONTEXT_EDIT, 0, "Edit");
                menu.add(0, ID_CONTEXT_DELETE, 0, "Delete"); //(?, key, order, text)
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        AlarmDetails alarm = alarms.get(info.position);

        switch (item.getItemId()) {
            case ID_CONTEXT_EDIT:
                //todo: create edit alarm functionality

                // unregister alarm with alarm manager
                alarm.registerAlarmIntent(this, AlarmDetails.CANCEL_ALARM);

                Intent addAlarm = new Intent(getApplicationContext(), InputAlarm.class);
                addAlarm.putExtra("alarmId", alarm.getId());

                startActivityForResult(addAlarm, ID_EDIT_ALARM);

                // register new alarm with alarm manager
                return super.onContextItemSelected(item);
            case ID_CONTEXT_DELETE:
                alarm.registerAlarmIntent(getApplicationContext(), AlarmDetails.CANCEL_ALARM);
                alarmAdapter.remove(alarm);
                return super.onContextItemSelected(item);
        }
        return false;
    }

    //opens up activity to addAlarm
    private void requestAddAlarm() {
        //sets up intent to inputAlarm
        Intent addAlarm = new Intent(getApplicationContext(), InputAlarm.class);
        startActivityForResult(addAlarm, ID_ADD_ALARM);
    }

    //adds alarm to the alarms ArrayList, calls addAlarmIntent to setup pendingIntent
    private void addAlarm(Intent data) {
        AlarmDetails newAlarm = new AlarmDetails(data.getIntExtra("hour", 0),
                data.getIntExtra("minute", 0),
                data.getStringExtra("alarm_name"),
                data.getBooleanExtra("repeat", false),
                data.getIntExtra("snooze", 1),
                data.getStringExtra("ringtone"));

        newAlarm.registerAlarmIntent(getApplicationContext(), AlarmDetails.ADD_ALARM);
        alarms.add(newAlarm);

        //save the alarms
        AlarmLab.get(getApplicationContext()).saveAlarms();

        // update persistent notification
        createNotification();

        alarmAdapter.notifyDataSetChanged();
    }

    private void editAlarm(Intent data) {

        long alarmId = data.getLongExtra("alarmId", -1);
        AlarmDetails oldAlarm = AlarmLab.get(this).getAlarmDetails(alarmId);

        oldAlarm.setTime(data.getIntExtra("hour", 0), data.getIntExtra("minute", 0));
        oldAlarm.setName(data.getStringExtra("alarm_name"));
        oldAlarm.setRepeat(data.getBooleanExtra("repeat", false));
        oldAlarm.setSnooze(data.getIntExtra("snooze", 1));
        oldAlarm.setRingtone(data.getStringExtra("ringtone"));

        oldAlarm.registerAlarmIntent(getApplicationContext(), AlarmDetails.ADD_ALARM);

        //save the alarms
        AlarmLab.get(getApplicationContext()).saveAlarms();

        // update persistent notification
        createNotification();

        alarmAdapter.notifyDataSetChanged();
    }


    //show the timePicker dialog inside a DialogFragment
    private void showTimePickerDialog() {
        DialogFragment newFrag = new TimePickerFragment();
        newFrag.show(getSupportFragmentManager(), "timePicker"); //requires instance of a FragmentManager, + unique tag for this fragment
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ID_ADD_ALARM:
                    addAlarm(data);
                    break;
                case ID_EDIT_ALARM:
                    editAlarm(data);
                    break;
            }
        }
    }

    // saves arraylist of alarms to database
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //save info by doing eg. outState.putString("key", varName), outState.putFloatArray("key",..
        AlarmLab.get(getApplicationContext()).saveAlarms();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //restore info by taking it out eg. var = savedInstanceState.getString("key");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AlarmLab.get(getApplicationContext()).saveAlarms();
    }

}

//AlarmAdapter for the ListView
class AlarmAdapter extends ArrayAdapter<AlarmDetails> {
    public AlarmAdapter(Context context, ArrayList<AlarmDetails> alarmList) {
        super(context, 0, alarmList);
    }


    //returns actual View to be displayed as row within the alarm ListView
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        //retrieves alarms array for this position
        final AlarmDetails alarm = getItem(pos);

        //check if existing view is being reused, else just inflate the view with custom alarm_list_item xml
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_list_item, parent, false);

        //lookup the Views to be populated, ie. alarm name and alarm time
        TextView alarmName = (TextView) convertView.findViewById(R.id.view_alarm_name);
        TextView alarmTime = (TextView) convertView.findViewById(R.id.view_alarm_time);

        //gets the switch widget for the View
        Switch aSwitch = (Switch) convertView.findViewById(R.id.view_alarm_on_state);
        aSwitch.setChecked(MainAlarm.alarms.get(pos).isOnState());         //if the alarm state was on, set aSwitch accordingly

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.toggleOnState();

                // update persistent notification
                MainAlarm.createNotification();

                //checks the pendingIntent for the alarm
                if (alarm.isOnState())
                    alarm.registerAlarmIntent(getContext(), AlarmDetails.ADD_ALARM);
                else //cancel the alarm
                    alarm.registerAlarmIntent(getContext(), AlarmDetails.CANCEL_ALARM);

                notifyDataSetChanged();
            }
        });

        //updates the Views with the data
        alarmName.setText(alarm.getName());
        alarmTime.setText(alarm.getHour() + ":" + alarm.getMin() + (alarm.isOnState() ? " ON" : " OFF")
                +" Repeat: "+ (alarm.isRepeat()?"YES":"NO")
                +" Snooze: "+ (alarm.getnSnooze())
                +" Ringtone: "+ (alarm.getRingtone())); //todo: CHANGE THIS, ON OFF JUST TO TEST ONLY

        //return completed view to render on screen
        return convertView;
    }


}


