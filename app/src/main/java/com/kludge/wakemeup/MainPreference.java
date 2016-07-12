package com.kludge.wakemeup;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Zex on 27/6/2016.
 */
public class MainPreference extends AppCompatActivity {

    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pong_game); //re-using because linear layout

        //sets up Preferences fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SettingsFragment settingsFragment = new SettingsFragment();
        fragmentTransaction.add(android.R.id.content, settingsFragment, "SETTINGS_FRAGMENT");
        fragmentTransaction.commit();

        sharedPrefs = getSharedPreferences("preferences_main", MODE_PRIVATE);
        PreferenceManager.setDefaultValues(this, "preferences_main", Context.MODE_PRIVATE, R.xml.main_preferences, false);
    }

    //borrow input_alarm's MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_input_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.menu_done:
                SharedPreferences.Editor editor = sharedPrefs.edit();

                editor.putBoolean("preference_persistent_notification", false);
                editor.putBoolean("preference_sleep_notification", true);

                editor.apply();
                finish();
                break;
            case android.R.id.home: //reject changes
                finish();
                break;
            default:
                return false;
        }

        return true;
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.main_preferences);
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);

            return v;
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.i("changed"," called");
            switch(key){
                case "preference_persistent_notification":
                    CheckBoxPreference prefNotif = (CheckBoxPreference) findPreference(key);
                    if(prefNotif.isChecked()) //if persist notif is enabled, create notif, else disable notif
                        MainAlarm.createNotification(MainAlarm.ID_NOTIFICATION_ALARM, null);
                    else
                        MainAlarm.destroyNotificaton(MainAlarm.ID_NOTIFICATION_ALARM, null);
                    break;
                case "preference_sleep_notification":
                    CheckBoxPreference prefSleep = (CheckBoxPreference) findPreference(key);
                    if(prefSleep.isChecked());

                    else
                        MainAlarm.destroyNotificaton(MainAlarm.ID_NOTIFICATION_SLEEP, null);
            }
        }
    }
}
