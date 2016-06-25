package com.kludge.wakemeup;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/*
 * Created by Yu Peng on 8/6/2016.
 */
public class AlarmLab {

    // constants to save to JSON
    private static final String TAG = "AlarmLab";
    private static final String FILENAME = "alarms.json";

    private ArrayList<AlarmDetails> mAlarms;
    private AlarmJSONSerializer mSerializer;

    private static AlarmLab sAlarmLab;
    private Context mAppContext;

    // Context parameter allows the singleton to start activities,
    // access project resources, find your application’s private storage, and more
    private AlarmLab(Context appContext) {
        mAppContext = appContext;

        mSerializer = new AlarmJSONSerializer(mAppContext, FILENAME);
        try {
            // loads alarms from database
            mAlarms = mSerializer.loadAlarms();
        } catch (Exception e) {
            mAlarms = new ArrayList<AlarmDetails>();
            Log.e(TAG, "Error loading alarms: ", e);
        }
    }

    public boolean saveAlarms() {
        try {
            // pass arraylist of alarms to serializer
            mSerializer.saveAlarms(mAlarms);
            Log.d(TAG, "alarms saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "error saving alarms: ", e);
            return false;
        }
    }


    // to retrieve instance of AlarmLab, call get(Context) method

    public static AlarmLab get(Context c) {
        if (sAlarmLab == null) {
            // application context is a Context that is global to your application.
            // used passed in Context c to access that
            // can't just use Context c as it might not exist for the life of the application
            // given it's the context of an activity/service that called get()
            sAlarmLab = new AlarmLab(c.getApplicationContext());
        }
        return sAlarmLab;
    }

    // return entire arraylist of crimes
    public ArrayList<AlarmDetails> getAlarms() {
        return mAlarms;
    }

    // return particular instance of crime
    public AlarmDetails getAlarmDetails(long id) {
        for (AlarmDetails a : mAlarms) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    // retrieve earliest alarm
    public AlarmDetails getEarliestAlarm() {

        Log.d("Notification", "get earliest alarm");

        // if there is at least 1 alarm
        if (mAlarms.size() > 0) {
            AlarmDetails min = null;

            // find 'on' alarms
            for (AlarmDetails a : mAlarms) {
                if (a.isOnState()) {
                    min = a;
                }
            }

            // if there is at least 1 'on' alarm
            // find the earliest amongst them
            if (min != null) {
                for (AlarmDetails a : mAlarms) {
                    if (a.isOnState() && min.getTimeInMillis() > a.getTimeInMillis()) {
                        min = a;
                    }
                }
            }

            return min;
        }

        return null;
    }

    public void addAlarm(AlarmDetails a) {
        mAlarms.add(a);
    }


}
