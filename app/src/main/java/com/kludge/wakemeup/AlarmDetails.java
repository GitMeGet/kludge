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
    private int nGame;
    private int nMathQns;
    private int nMathDifficulty;
    private int nSleepDur;
    private String targetId;

    //create alarm ID
    private long mId;

    //game type ID
    public static final int GAME_DISABLED = 0;
    public static final int GAME_MATH = 1;
    public static final int GAME_PONG = 2;

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
    private static final String JSON_GAME = "game";
    private static final String JSON_MATHQNS = "mathqns";
    private static final String JSON_SLEEPDUR = "sleepdur";
    private static final String JSON_TARGETID = "targetId";

    public AlarmDetails(int nHour, int nMin, String strName, boolean bRepeat, int nSnooze,
                        String uriRingtone, int nGame, int nMathQns, int nMathDifficulty, int nSleepDur, String targetId) {
        this.mId = System.currentTimeMillis();

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
        this.nSnooze = nSnooze;
        this.uriRingtone = uriRingtone;

        this.nGame = nGame;
        this.nMathQns = nMathQns;
        this.nMathDifficulty = nMathDifficulty;

        this.nSleepDur = nSleepDur;

        this.targetId = targetId;
    }

    // constructor that accepts JSON object
    public AlarmDetails(JSONObject json) throws JSONException {
        mId = json.getLong(JSON_ID);

        nHour = json.getInt(JSON_HOUR);
        nMin = json.getInt(JSON_MIN);
        lTimeInMillis = json.getLong(JSON_MILLIS);
        strName = json.getString(JSON_STR_NAME);
        bOnState = json.getBoolean(JSON_ON_STATE);
        bRepeat = json.getBoolean(JSON_REPEAT);

        nSnooze = json.getInt(JSON_SNOOZE);
        uriRingtone = json.getString(JSON_RINGTONE);

        nGame = json.getInt(JSON_GAME);
        nMathQns = json.getInt(JSON_MATHQNS);

        nSleepDur = json.getInt(JSON_SLEEPDUR);

        targetId = json.getString(JSON_TARGETID);
    }

    // converts AlarmDetails.java  to a JSON object
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(JSON_ID, mId);

        json.put(JSON_HOUR, nHour);
        json.put(JSON_MIN, nMin);
        json.put(JSON_MILLIS, lTimeInMillis);
        json.put(JSON_STR_NAME, strName);
        json.put(JSON_ON_STATE, bOnState);
        json.put(JSON_REPEAT, bRepeat);
        json.put(JSON_SNOOZE, nSnooze);
        json.put(JSON_RINGTONE, uriRingtone);

        json.put(JSON_GAME, nGame);
        json.put(JSON_MATHQNS, nMathQns);

        json.put(JSON_SLEEPDUR, nSleepDur);

        json.put(JSON_TARGETID, targetId);

        return json;
    }

    private void updateSleepNotification(Context context, PendingIntent notifPI){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {                    // build before kit kat
            if (getTimeInMillis() - getSleepDur()*3600000 < System.currentTimeMillis()) {       // if less than sleepDuration to go till alarm ring, register notification reminder
                MainAlarm.createNotification(MainAlarm.ID_NOTIFICATION_SLEEP, this);
            }
            else {                                                                              // register pendingIntent for notification
                alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMillis() - getSleepDur()*3600000, notifPI);
            }
        }
        else {                                                                       // build after kit kat
            // register notification reminder
            // if less than 8 hrs to go till alarm ring
            if (getTimeInMillis() - getSleepDur()*3600000 < System.currentTimeMillis()) {
                MainAlarm.createNotification(MainAlarm.ID_NOTIFICATION_SLEEP, this);
            }
            // more than 8 hrs till alarm ring
            else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, getTimeInMillis() - getSleepDur()*3600000, notifPI);
            }
        }
    }

    private void updateAlarmIntent(Context context, PendingIntent alarmPI){

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {                    // build before kit kat
            alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMillis(), alarmPI);   // register alarm
        }
        else {                                                                       // build after kit kat
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, getTimeInMillis(), alarmPI);
        }
    }


    // registers alarm with alarm manager
    // also registers sleep reminder notification with alarm manager
    public void registerAlarmIntent(Context context, int requestCode) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("alarmId", getId());
        PendingIntent alarmPI = PendingIntent.getBroadcast(context, (int) getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("alarmId", getId());
        PendingIntent notifPI = PendingIntent.getBroadcast(context, (int) getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        switch (requestCode) {
            case ADD_ALARM:
                if (getTimeInMillis() < System.currentTimeMillis()) {                          // if alarm is due for tomorrow
                    lTimeInMillis = getTimeInMillis() + (long) 8.64e+7;                        // update timeInMillis to one-day-later's time
                }
                updateAlarmIntent(context, alarmPI);
                if(context.getSharedPreferences("preferences_main", Context.MODE_PRIVATE).getBoolean("preference_sleep_notification", false))
                    updateSleepNotification(context, notifPI);
                break;
            case CANCEL_ALARM:
                alarmManager.cancel(alarmPI);
                alarmManager.cancel(notifPI);
                break;
            case CHECK_ALARM:

                updateSleepNotification(context, notifPI);
                break;
            case SNOOZE_ALARM:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nSnooze * 60000, alarmPI);
                else
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + nSnooze * 60000, alarmPI);
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

    public int getGame() {return nGame;}
    public int getMathQns() {return nMathQns;}
    public int getMathDifficulty() {return nMathDifficulty;}

    public int getSleepDur() {return nSleepDur;}

    public String getTargetId(){return targetId;}


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

    public void setGame(int nGame) {this.nGame = nGame;}
    public void setMathQns(int nMathQns) {this.nMathQns = nMathQns;}
    public void setMathDifficulty(int nMathDifficulty) {this.nMathDifficulty = nMathDifficulty;}

    public void setSleepDur(int nSleepDur) {this.nSleepDur = nSleepDur;}

    public void setTargetId(String targetId) {this.targetId = targetId;}

}
