package com.kludge.wakemeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/*
 * Created by Zex on 26/5/2016.
 * yes
 */
public class AlarmDetails {
    private int nHour;
    private int nMin;
    private long lTimeInMillis;
    private String strName;
    private boolean bOnState;
    private boolean bRepeat;
    private int nSnooze;
    private String uriRingtone;

    //create alarm ID
    private long mId;

    //request codes
    public static final int ADD_ALARM = 1;
    public static final int CANCEL_ALARM = 2;
    public static final int CHECK_ALARM = 3;
    public static final int SNOOZE_ALARM = 4;

    // JSON keys
    private static final String JSON_HOUR = "hour";
    private static final String JSON_MIN = "min";
    private static final String JSON_MILLIS = "millis";
    private static final String JSON_ID = "id";
    private static final String JSON_STR_NAME = "strName";
    private static final String JSON_ON_STATE = "onState";
    private static final String JSON_REPEAT = "repeat";
    private static final String JSON_RINGTONE = "ringtone";
    private static final String JSON_SNOOZE = "snooze";

    public AlarmDetails(int nHour, int nMin, String strName, boolean bRepeat, int nSnooze, String uriRingtone) {
        this.nHour = nHour;
        this.nMin = nMin;

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, nHour);
        alarmTime.set(Calendar.MINUTE, nMin);
        alarmTime.set(Calendar.SECOND, 0);
        this.lTimeInMillis = alarmTime.getTimeInMillis();

        this.strName = strName;
        this.bRepeat = bRepeat;

        this.bOnState = true;
        this.mId = System.currentTimeMillis();

        this.nSnooze = nSnooze;
        this.uriRingtone = uriRingtone;
    }

    // constructor that accepts JSON object
    public AlarmDetails(JSONObject json) throws JSONException {
        nHour = json.getInt(JSON_HOUR);
        nMin = json.getInt(JSON_MIN);
        lTimeInMillis = json.getLong(JSON_MILLIS);
        strName = json.getString(JSON_STR_NAME);
        bOnState = json.getBoolean(JSON_ON_STATE);
        bRepeat = json.getBoolean(JSON_REPEAT);
        mId = json.getLong(JSON_ID);
        nSnooze = json.getInt(JSON_SNOOZE);
        uriRingtone = json.getString(JSON_RINGTONE);
    }

    // converts AlarmDetails.java  to a JSON object
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(JSON_HOUR, nHour);
        json.put(JSON_MIN, nMin);
        json.put(JSON_MILLIS, lTimeInMillis);
        json.put(JSON_STR_NAME, strName);
        json.put(JSON_ON_STATE, bOnState);
        json.put(JSON_REPEAT, bRepeat);
        json.put(JSON_ID, mId);
        json.put(JSON_SNOOZE, nSnooze);
        json.put(JSON_RINGTONE, uriRingtone);

        return json;
    }


    // registers alarm with alarm manager
    // also registers sleep reminder notification with alarm manager
    public void registerAlarmIntent(Context context, int requestCode) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("alarmId", getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("alarmId", getId());
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, (int) getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        switch (requestCode) {
            case ADD_ALARM:

                // if alarm is due for tomorrow
                if (getTimeInMillis() < System.currentTimeMillis()) {

                    // update timeInMillis to one-day-later's time
                    lTimeInMillis = getTimeInMillis() + (long) 8.64e+7;

                    // build before kit kat
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        // register alarm
                        alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMillis() + (long) 8.64e+7, pendingIntent);

                        // register notification reminder
                        // if less than 8 hrs to go till alarm ring
                        if (getTimeInMillis() - (long) 2.88e+7 < System.currentTimeMillis()) {
                            MainAlarm.createNotification(1,this);
                        }
                        // more than 8 hrs till alarm ring
                        else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMillis() + (long) 8.64e+7 - (long) 2.88e+7, pendingIntent1);
                        }

                    }

                    // build after kit kat
                    else {
                        // register alarm
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, getTimeInMillis() + (long) 8.64e+7, pendingIntent);

                        // register notification reminder
                        // if less than 8 hrs to go till alarm ring
                        if (getTimeInMillis() - (long) 2.88e+7 < System.currentTimeMillis()) {
                            MainAlarm.createNotification(1,this);
                        }
                        // more than 8 hrs till alarm ring
                        else {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, getTimeInMillis() + (long) 8.64e+7, pendingIntent1);
                        }

                    }
                }

                // alarm is due later today
                else {
                    // build earlier than kit kat
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {

                        // register alarm
                        alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMillis(), pendingIntent);

                        // register notification reminder
                        // if less than 8 hrs to go till alarm ring
                        if (getTimeInMillis() - (long) 2.88e+7 < System.currentTimeMillis()) {
                            MainAlarm.createNotification(1,this);
                        }
                        // more than 8 hrs till alarm ring
                        else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMillis() - (long) 2.88e+7, pendingIntent1);
                        }

                    }

                    // build later than kit kat
                    else {

                        // register alarm
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, getTimeInMillis(), pendingIntent);

                        // register notification reminder
                        // if less than 8 hrs to go till alarm ring
                        if (getTimeInMillis() - (long) 2.88e+7 < System.currentTimeMillis()) {
                            MainAlarm.createNotification(1,this);
                        }
                        // more than 8 hrs till alarm ring
                        else {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, getTimeInMillis() - (long) 2.88e+7, pendingIntent1);
                        }
                    }
                }


                break;
            case CANCEL_ALARM:
                alarmManager.cancel(pendingIntent);
                alarmManager.cancel(pendingIntent1);
                break;
            case CHECK_ALARM:
                break;
            case SNOOZE_ALARM:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                    alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMillis() + nSnooze * 60000, pendingIntent);
                else
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, getTimeInMillis() + nSnooze * 60000, pendingIntent);
                break;
        }

    }

    //getters
    public long getId() {
        return mId;
    }

    public boolean isOnState() {
        return bOnState;
    }

    public boolean isRepeat() {
        return bRepeat;
    }

    public long getTimeInMillis() {
        return lTimeInMillis;
    }

    public int getHour() {
        return nHour;
    }

    public int getMin() {
        return nMin;
    }

    public String getName() {
        return strName;
    }

    public int getnSnooze() {
        return nSnooze;
    }

    public String getRingtone() {
        return uriRingtone;
    }


    //setters
    public void setName(String strName) {
        this.strName = strName;
    }

    public void setTime(int nHour, int nMin) {
        this.nHour = nHour;
        this.nMin = nMin;

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, nHour);
        alarmTime.set(Calendar.MINUTE, nMin);
        alarmTime.set(Calendar.SECOND, 0);
        this.lTimeInMillis = alarmTime.getTimeInMillis();
    }

    public void setRepeat(boolean bRepeat) {
        this.bRepeat = bRepeat;
    }

    public void toggleOnState() {
        bOnState = !bOnState;
    }

    public void setOnState(boolean bOnState) {
        this.bOnState = bOnState;
    }

    public void setSnooze(int nSnooze) {
        this.nSnooze = nSnooze;
    }

    public void setRingtone(String uriRingtone) {
        this.uriRingtone = uriRingtone;
    }

}
