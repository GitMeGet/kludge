package com.kludge.wakemeup;

import android.content.Context;

/*
 * Created by Yu Peng on 7/7/2016.
 */
public class GCMParams {

    private Context mContext;
    private String type;
    private String userId;
    private String token;
    private String targetId;
    private String timeInMillis;
    private String message;
    private String alarmId;

    public GCMParams(){}

    public GCMParams(Context context, String type, String userId, String token, String targetId,
                     String timeInMillis, String message, String alarmId){
        mContext = context;
        this.type = type;
        this.userId = userId;
        this.token = token;
        this.targetId = targetId;
        this.timeInMillis = timeInMillis;
        this.message = message;
        this.alarmId = alarmId;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public void setTimeInMillis(String timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public Context getmContext() {
        return mContext;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getTimeInMillis() {
        return timeInMillis;
    }

    public String getMessage() {
        return message;
    }

    public String getAlarmId() {
        return alarmId;
    }
}
