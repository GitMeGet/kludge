package com.kludge.wakemeup;

import android.content.Context;

/**
 * Created by Yu Peng on 7/7/2016.
 */
public class GCMParams {

    public Context mContext;
    public String type;
    public String token;
    public String id;
    public String text;

    public GCMParams(Context context, String type, String token, String id, String text){
        mContext = context;
        this.type = type;
        this.token = token;
        this.id = id;
        this.text = text;
    }

}
