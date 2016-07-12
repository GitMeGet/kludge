package com.kludge.wakemeup;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sylta on 12/7/2016.
 */
public class UserManager {

    public static int SCORE_THRESHOLD = 3; //how many points do user earn for no-snooze dismiss
    public static int SNOOZE_PRICE = 2;

    SharedPreferences userDetails;
    Context c;

    public UserManager(Context c){
        this.c = c;
        userDetails = c.getSharedPreferences("preferences_user", Context.MODE_PRIVATE);
    }

    public void increaseScore(int increment) {
        int score = userDetails.getInt("user_score", 0);
        userDetails.edit().putInt("user_score", score+=(increment<0?0:increment)).apply();
    }

    public void decreaseScore(int decrement) {
        int score = userDetails.getInt("user_score", 0);
        userDetails.edit().putInt("user_score", score-=(decrement<0?0:decrement)).apply();
    }

    public void increaseSnooze() {
        int snoozes = userDetails.getInt("user_snoozes", 0);
        userDetails.edit().putInt("user_snoozes", ++snoozes).apply();}

    public void resetSnooze() {
        userDetails.edit().putInt("user_snoozes", 0).apply();
    }

    public int getScore() {return userDetails.getInt("user_score", 0);}
    public int getSnooze() {return userDetails.getInt("user_snoozes", 0);}
}
