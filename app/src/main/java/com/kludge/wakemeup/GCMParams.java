package com.kludge.wakemeup;

import android.content.Context;

/*
 * Created by Yu Peng on 7/7/2016.
 */
public class GCMParams {

    public Context mContext;
    public String type;
    public String userId;
    public String token;
    public String targetId;
    public String timeInMillis;
    public String message;
    public String alarmId;


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

}
