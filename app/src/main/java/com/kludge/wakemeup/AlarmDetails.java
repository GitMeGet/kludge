package com.kludge.wakemeup;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Created by Zex on 26/5/2016.
 */
public class AlarmDetails {
    public int nHour;
    public int nMin;
    public String strName;
    public boolean bOnState;

    //create alarm ID
    private long mId;

    // JSON keys
    private static final String JSON_HOUR = "hour";
    private static final String JSON_MIN = "min";
    private static final String JSON_ID = "id";
    private static final String JSON_STR_NAME = "strName";
    private static final String JSON_ON_STATE = "onState";

    public AlarmDetails(int nHour, int nMin, String strName) {
        this.nHour = nHour;
        this.nMin = nMin;
        this.strName = strName;
        this.bOnState = false;
        this.mId = System.currentTimeMillis();
    }

    // constructor that accepts JSON object
    public AlarmDetails(JSONObject json) throws JSONException {
        nHour = json.getInt(JSON_HOUR);
        nHour = json.getInt(JSON_MIN);
        strName = json.getString(JSON_STR_NAME);
        bOnState = json.getBoolean(JSON_ON_STATE);
        mId = json.getLong(JSON_ID);
    }

    // converts AlarmDetails.java  to a JSON object
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(JSON_HOUR, nHour);
        json.put(JSON_MIN, nMin);
        json.put(JSON_STR_NAME, strName);
        json.put(JSON_ON_STATE, bOnState);
        json.put(JSON_ID, mId);

        return json;
    }

    public long getId() {
        return mId;
    }


    public void setName(String strName) {
        this.strName = strName;
    }

    public void setTime(int nHour, int nMin) {
        this.nHour = nHour;
        this.nMin = nMin;
    }

    public void toggleOnState() {
        bOnState = !bOnState;
    }

    public boolean isOnState() {
        return bOnState;
    }
}
