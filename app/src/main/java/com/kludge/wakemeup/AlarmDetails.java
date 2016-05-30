package com.kludge.wakemeup;

import java.util.UUID;

/**
 * Created by Zex on 26/5/2016.
 */
public class AlarmDetails {
    public int nHour;
    public int nMin;
    public String strName;
    public boolean bOnState;
    //create alarm ID


    public AlarmDetails(int nHour, int nMin, String strName){
        this.nHour = nHour;
        this.nMin = nMin;
        this.strName = strName;
        this.bOnState = false;

    }

    public void setName(String strName) {this.strName = strName;}
    public void setTime(int nHour, int nMin) {this.nHour = nHour; this.nMin = nMin;}
    public void toggleOnState() {bOnState = !bOnState;}

    public boolean isOnState() {return bOnState;}
}
