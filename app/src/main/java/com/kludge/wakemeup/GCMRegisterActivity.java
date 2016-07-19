package com.kludge.wakemeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/*
 * Created by Yu Peng on 7/7/2016.
 */
public class GCMRegisterActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "GCMRegisterActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private boolean isReceiverRegistered;
    private boolean isUserRegistered;
    private long alarmId;
    private String userId; // make sure to save userId
    EditText mUserIdEditText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gcm);

        alarmId = getIntent().getLongExtra("alarmId", -1);

        isUserRegistered = PreferenceManager.getDefaultSharedPreferences(this).
                getBoolean("isUserRegistered", false);

        // if user has already been registered previously
        // proceed to request for other users to wake them up
        if (isUserRegistered == true){
            Intent i = new Intent(this, GCMRequestActivity.class);
            i.putExtra("alarmId", alarmId);
            finish();
            startActivity(i);
        }

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean("sentTokenToServer", false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));

                    sharedPreferences.edit().putBoolean("isUserRegistered", true).apply();

                    // once passed gcm token to backend server, user is registered
                    // proceed to request for other users to wake them up
                    Intent i = new Intent(context, GCMRequestActivity.class);
                    i.putExtra("alarmId", alarmId);
                    startActivity(i);
                    finish();
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };

        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        // Registering BroadcastReceiver to receive if GCMRegistrationService is successful
        registerReceiver();

        mUserIdEditText =(EditText) findViewById(R.id.userIdEditText);

        // if user had previously entered userId, show it
        userId = getSharedPreferences("preferences_user", MODE_PRIVATE).getString("userId", "");
        mUserIdEditText.setText(userId);

        Button mSaveUserIdButton = (Button) findViewById(R.id.saveUserIdButton);
        assert mSaveUserIdButton != null;
        mSaveUserIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPlayServices()) {
                    // make sure userId field is filled
                    if (!validateInputs())
                        return;

                    // Start IntentService to register this application with GCM.
                    Intent intent = new Intent(getApplicationContext(), GCMRegistrationIntentService.class);
                    // add username to intent extra
                    userId = mUserIdEditText.getText().toString();
                    intent.putExtra("userId", userId);

                    // save userId to SharedPrefs
                    SharedPreferences sharedPreferences = getSharedPreferences("preferences_user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", userId);
                    editor.apply();

                    startService(intent);
                }
            }
        });

    }

    // make sure necessary input fields are filled
    protected boolean validateInputs(){
        // if no userId provided by user
        if (userId.equals("") && mUserIdEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "OI! Fill in userId leh!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter("registrationComplete"));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }






}
