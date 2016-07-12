package com.kludge.wakemeup;

import android.content.Context;

/**
 * Created by Yu Peng on 7/7/2016.
 */
public class GCMParams {

    public Context mContext;
    public String type;
    public String userId;
    public String token;
    public String targetId;
    public String text;

    public GCMParams(Context context, String type, String userId, String token, String targetId, String text){
        mContext = context;
        this.type = type;
        this.userId = userId;
        this.token = token;
        this.targetId = targetId;
        this.text = text;
    }

}
