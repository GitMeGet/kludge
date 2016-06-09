package com.kludge.wakemeup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/*
 * Created by Zex on 26/5/2016.
 * yes
 */
public class AlarmDetails {
    public int nHour;
    public int nMin;
    public long lTimeInMillis;
    public String strName;
    public boolean bOnState;

    //create alarm ID
    private long mId;

    // JSON keys
    private static final String JSON_HOUR = "hour";
    private static final String JSON_MIN = "min";
    private static final String JSON_MILLIS = "millis";
    private static final String JSON_ID = "id";
    private static final String JSON_STR_NAME = "strName";
    private static final String JSON_ON_STATE = "onState";

    public AlarmDetails(int nHour, int nMin, String strName) {
        this.nHour = nHour;
        this.nMin = nMin;

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, nHour);
        alarmTime.set(Calendar.MINUTE, nMin);
        alarmTime.set(Calendar.SECOND, 0);
        this.lTimeInMillis = alarmTime.getTimeInMillis();

        this.strName = strName;
        this.bOnState = true;
        this.mId = System.currentTimeMillis();
    }

    // constructor that accepts JSON object
    public AlarmDetails(JSONObject json) throws JSONException {
        nHour = json.getInt(JSON_HOUR);
        nMin = json.getInt(JSON_MIN);
        lTimeInMillis = json.getLong(JSON_MILLIS);
        strName = json.getString(JSON_STR_NAME);
        bOnState = json.getBoolean(JSON_ON_STATE);
        mId = json.getLong(JSON_ID);
    }

    // converts AlarmDetails.java  to a JSON object
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(JSON_HOUR, nHour);
        json.put(JSON_MIN, nMin);
        json.put(JSON_MILLIS, lTimeInMillis);
        json.put(JSON_STR_NAME, strName);
        json.put(JSON_ON_STATE, bOnState);
        json.put(JSON_ID, mId);

        return json;
    }

    //getters
    public long getId() {
        return mId;
    }
    public boolean isOnState() {return bOnState;}
    public long getTimeInMillis() {return lTimeInMillis;}

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

    public void toggleOnState() {bOnState = !bOnState;}

}
