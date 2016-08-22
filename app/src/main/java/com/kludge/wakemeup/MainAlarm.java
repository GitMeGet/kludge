package com.kludge.wakemeup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.NumberFormat;
import java.util.ArrayList;

public class MainAlarm extends AppCompatActivity {

    //keys
    static final int ID_GOOGLE_SIGNIN = 400;

    static final int ID_ADD_ALARM = 100;
    static final int ID_EDIT_ALARM = 101;

    static final int ID_CONTEXT_EDIT = 200;
    static final int ID_CONTEXT_DELETE = 201;

    static final int ID_NOTIFICATION_ALARM = 300;
    static final int ID_NOTIFICATION_SLEEP = 301;

    // alarms
    static ArrayList<AlarmDetails> alarms; //array containing DESCRIPTION OF ALARMS? !!!! MUST IT BE STATIC???
    static AlarmAdapter alarmAdapter; //arrayAdapter for the ListView

    // for notifications
    private static Context mContext; // not sure if this is a good idea???
    private static NotificationManager notificationManager;

    //shared preferences
    private SharedPreferences sharedPrefs;

    //firebase
    private DatabaseReference mDatabase;
    private FirebaseUser fUser;

    protected static void createNotification(int type, AlarmDetails alarm) {

        System.out.println("createNotification");

        Intent i = new Intent(mContext, MainAlarm.class);
        PendingIntent pi = PendingIntent.getActivity(mContext, 0, i, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        Notification notification;

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);

        switch (type) {
            case ID_NOTIFICATION_ALARM:
                String time = "No Alarm";

                AlarmDetails earliestAlarm = AlarmLab.get(mContext).getEarliestAlarm();
                if (earliestAlarm != null) {
                    time = "Next Alarm: " + earliestAlarm.getHour() + ":" + nf.format(earliestAlarm.getMin());
                }

                mBuilder.setContentTitle(time)
                        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                        // persistent notification
                        .setOngoing(true)
                        // notification opens up MainAlarm when pressed
                        .setContentIntent(pi);

                notification = mBuilder.build();
                notificationManager.notify(ID_NOTIFICATION_ALARM, notification);
                break;
            case ID_NOTIFICATION_SLEEP:

                double hoursToAlarm = (alarm.getTimeInMillis() - System.currentTimeMillis()) * (2.77778e-7);
                double minsToAlarm = (hoursToAlarm - (int) hoursToAlarm) * 60;
                String minutesToAlarm = String.format("%.0f", minsToAlarm);

                String hTA = "Time till wake: " + (int) hoursToAlarm + "hrs "
                        + minutesToAlarm + "mins";

                mBuilder.setContentTitle("Please Sleep Soon")
                        .setContentText(hTA)
                        .setSmallIcon(android.R.drawable.ic_lock_power_off)
                        // notification opens up MainAlarm when pressed
                        .setContentIntent(pi);

                try {
                    Uri notif = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(mContext, notif);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                notification = mBuilder.build();
                notificationManager.notify(ID_NOTIFICATION_SLEEP, notification);

                break;
        }
    }

    protected static void destroyNotificaton(int type, AlarmDetails alarm) {
        switch (type) {
            case ID_NOTIFICATION_ALARM:
                notificationManager.cancel(ID_NOTIFICATION_ALARM);
                break;
            case ID_NOTIFICATION_SLEEP:
                notificationManager.cancel(ID_NOTIFICATION_SLEEP);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alarm);

        sharedPrefs = getSharedPreferences("preferences_main", MODE_PRIVATE);

        //google auth login
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            signIn();
        }

        // for notifications
        mContext = this;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (sharedPrefs.getBoolean("preference_persistent_notification", false)) {
            createNotification(ID_NOTIFICATION_ALARM, null);
        }

        // initialise alarms ArrayList if empty, else load from FILE
        alarms = AlarmLab.get(getApplicationContext()).getAlarms();

        //initiates AlarmAdapter for ListView, (context, layout, strArray)
        alarmAdapter = new AlarmAdapter(this, alarms);

        //sets up listView and attach the adapter to this ListView
        ListView listView = (ListView) findViewById(R.id.list_alarms);
        assert listView != null;
        listView.setAdapter(alarmAdapter);

        //sets up listView for contextMenu
        registerForContextMenu(listView);
    }

    public void signIn(){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
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
                // unregister alarm with alarm manager
                alarm.registerAlarmIntent(this, AlarmDetails.CANCEL_ALARM);

                Intent addAlarm = new Intent(getApplicationContext(), InputAlarm.class);
                addAlarm.putExtra("alarmId", alarm.getId());

                startActivityForResult(addAlarm, ID_EDIT_ALARM);

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
                data.getStringExtra("ringtone"),
                data.getIntExtra("game", AlarmDetails.GAME_DISABLED),
                data.getIntExtra("mathqns", 1),
                data.getIntExtra("mathdifficulty", 1),
                data.getIntExtra("sleepdur", 6),
                "");

        alarms.add(newAlarm);

        updateAll(newAlarm);
    }

    private void editAlarm(Intent data) {

        long alarmId = data.getLongExtra("alarmId", -1);
        AlarmDetails oldAlarm = AlarmLab.get(this).getAlarmDetails(alarmId);

        oldAlarm.setTime(data.getIntExtra("hour", 0), data.getIntExtra("minute", 0));
        oldAlarm.setName(data.getStringExtra("alarm_name"));
        oldAlarm.setRepeat(data.getBooleanExtra("repeat", false));
        oldAlarm.setSnooze(data.getIntExtra("snooze", 1));
        oldAlarm.setRingtone(data.getStringExtra("ringtone"));

        oldAlarm.setGame(data.getIntExtra("game", AlarmDetails.GAME_DISABLED));
        oldAlarm.setMathQns(data.getIntExtra("mathqns", 1));
        oldAlarm.setMathDifficulty(data.getIntExtra("mathdifficulty", 1));

        oldAlarm.setSleepDur(data.getIntExtra("sleepdur", 6));

        updateAll(oldAlarm);
    }

    //updates notifications, registers pendingintents, refreshes listview, saves alarms
    private void updateAll(AlarmDetails alarm) {
        if (alarm.isOnState()) {
            alarm.registerAlarmIntent(getApplicationContext(), AlarmDetails.ADD_ALARM);
            
            // update notification
            if (mContext.getSharedPreferences("preferences_main", Context.MODE_PRIVATE)
                    .getBoolean("preference_persistent_notification", false))
                createNotification(ID_NOTIFICATION_ALARM, alarm);
        }

        //save the alarms
        AlarmLab.get(getApplicationContext()).saveAlarms();

        alarmAdapter.notifyDataSetChanged();
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
    protected void onResume() {
        super.onResume();
        alarmAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AlarmLab.get(getApplicationContext()).saveAlarms();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.menu_login:
                intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_scoreboard:
                intent = new Intent(getApplicationContext(), ScoreboardActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_plus:
                requestAddAlarm();
                break;
            case R.id.menu_settings:
                intent = new Intent(getApplicationContext(), MainPreference.class);
                startActivity(intent);
                break;

            default:
                return false;
        }
        return true;
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

        Button socializeButton = (Button) convertView.findViewById(R.id.socializeButton);

        socializeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), GCMRequestActivity.class);

                i.putExtra("userId", getContext().getSharedPreferences("preferences_user", Context.MODE_PRIVATE).getString("userID",""));
                i.putExtra("alarmId", alarm.getId());

                getContext().startActivity(i);
            }
        });

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.toggleOnState();

                //checks the pendingIntent for the alarm
                if (alarm.isOnState())
                    alarm.registerAlarmIntent(getContext(), AlarmDetails.ADD_ALARM);
                else //cancel the alarm
                    alarm.registerAlarmIntent(getContext(), AlarmDetails.CANCEL_ALARM);

                // update persistent notification
                if (getContext().getSharedPreferences("preferences_main", Context.MODE_PRIVATE)
                        .getBoolean("preference_persistent_notification", false))
                    MainAlarm.createNotification(MainAlarm.ID_NOTIFICATION_ALARM, null);

                notifyDataSetChanged();
            }
        });

        //updates the Views with the data
        alarmName.setText(alarm.getName());
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);

        alarmTime.setText(alarm.getHour() + ":" + nf.format(alarm.getMin()));
                /*
                + (alarm.isOnState() ? " ON" : " OFF")
                + " Repeat: " + (alarm.isRepeat() ? "YES" : "NO")
                + " Snooze: " + (alarm.getnSnooze())
                + " Ringtone: " + (alarm.getRingtone())); //todo: CHANGE THIS, ON OFF JUST TO TEST ONLY
                   */
        //return completed view to render on screen
        return convertView;
    }


}


