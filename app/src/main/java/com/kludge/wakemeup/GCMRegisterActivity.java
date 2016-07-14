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

    private AlarmDetails alarm;

    public static String userId; // make sure to save userId
    public static String targetId; // save targetId too!

    EditText mUserIdEditText;
    EditText mTargetIdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gcm);

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
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        // Registering BroadcastReceiver to receive if GCMRegistrationService is successful
        registerReceiver();

        // get alarm from AlarmLab
        long alarmId = getIntent().getLongExtra("alarmId", -1);
        alarm = AlarmLab.get(getApplicationContext()).getAlarmDetails(alarmId);

        mUserIdEditText =(EditText) findViewById(R.id.userIdEditText);
        mTargetIdEditText = (EditText) findViewById(R.id.targetIdEditText);

        // if user had previously entered userId, show it
        userId = getSharedPreferences("preferences_user", MODE_PRIVATE).getString("userId", "");
        mUserIdEditText.setText(userId);

        // if user had previously entered targetId, show it
        targetId = getSharedPreferences("preferences_user", MODE_PRIVATE).getString("targetId", "");
        mTargetIdEditText.setText(targetId);

        Button mSaveUserIdButton = (Button) findViewById(R.id.saveUserIdButton);
        assert mSaveUserIdButton != null;
        mSaveUserIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPlayServices()) {
                    // make sure userId field is filled
                    if (!validateInputs(1))
                        return;

                    // Start IntentService to register this application with GCM.
                    Intent intent = new Intent(getApplicationContext(), GCMRegistrationIntentService.class);

                    // add username to intent extra
                    userId = mUserIdEditText.getText().toString();
                    intent.putExtra("userId", userId);

                    startService(intent);
                }
            }
        });

        Button mRequestTargetIdButton = (Button) findViewById(R.id.requestTargetIdButton);
        assert mRequestTargetIdButton != null;
        mRequestTargetIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make sure both input fields are filled
                if (!validateInputs(2))
                    return;

                targetId = mTargetIdEditText.getText().toString();

                new GCMRegistrationIntentService.ServletPostAsyncTask().execute(new GCMParams(
                        getApplicationContext(), "requestTarget", userId , "", targetId, ""));
            }
        });

        Button mP2PMessagingButton = (Button) findViewById(R.id.buttonP2PMessaging);
        assert mP2PMessagingButton != null;
        mP2PMessagingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MessagingActivity.class);
                startActivity(i);
            }
        });



    }

    // make sure necessary input fields are filled
    protected boolean validateInputs(int type){
        // if no userId provided by user
        if (userId.equals("") && mUserIdEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "OI! Fill in userId leh!", Toast.LENGTH_LONG).show();
            return false;
        }

        // if no targetId provided by user
        else if (type == 2 && targetId.equals("") && mTargetIdEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "OI! Fill in targetId leh!", Toast.LENGTH_LONG).show();
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
