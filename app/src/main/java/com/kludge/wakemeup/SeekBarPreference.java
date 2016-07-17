package com.kludge.wakemeup;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by Zex on 23/6/2016.
 */
public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener{

    public SeekBarPreference(Context context, AttributeSet attrSet){
        super(context, attrSet);
    }

    @Override
    protected View onCreateDialogView() {
        return super.onCreateDialogView();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
