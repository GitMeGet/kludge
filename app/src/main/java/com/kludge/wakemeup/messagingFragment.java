package com.kludge.wakemeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/*
 * Created by Yu Peng on 13/7/2016.
 */
public class MessagingFragment extends android.support.v4.app.Fragment {

    private String userId, targetId;
    private TextView mP2PMessageTextView;
    private boolean isP2PReceiverRegistered;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messaging, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get userId and targetId from SharedPreferences
        userId = getActivity().getSharedPreferences("preferences_user", getActivity().MODE_PRIVATE).getString("userId", "");

        // *** potentially more than 1 unique targetId, shouldn't store in SharedPrefs; should be in AlarmDetails ***
        targetId = getActivity().getSharedPreferences("preferences_user", getActivity().MODE_PRIVATE).getString("targetId", "");
        Log.i("Messaging Fragment", "targetId is " + targetId);

        // make text view scrollable (need custom listView???)
        mP2PMessageTextView = (TextView) view.findViewById(R.id.P2PMessageTextView);
        final EditText mP2PMessageEditText = (EditText) view.findViewById(R.id.P2PMessageEditText);
        Button mSendP2PMessageButton = (Button) view.findViewById(R.id.sendP2PMessageButton);

        mSendP2PMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mP2PMessageEditText.getText().toString();
                mP2PMessageEditText.getText().clear();

                // add current message to P2P message text view




                sendP2PMessage(message);

                registerReceiver();

            }
        });

    }

    // post P2P message to server
    private void sendP2PMessage(String message) {
        new GCMRegistrationIntentService.ServletPostAsyncTask().execute(new GCMParams(
                getContext(), "sendP2PMessage", userId, "", targetId, message));
    }

    // set broadcast receiver to listen from GCMListenerService
    private BroadcastReceiver mP2PMessageBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            mP2PMessageTextView.setText(message);
        }
    };

    private void registerReceiver() {
        if (!isP2PReceiverRegistered) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mP2PMessageBroadcastReceiver,
                    new IntentFilter("incomingP2PMessage"));
            isP2PReceiverRegistered = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // re-register P2P broadcast receiver
        registerReceiver();
    }

    @Override
    public void onPause() {
        // unregister P2P message broadcast receiver
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mP2PMessageBroadcastReceiver);
        isP2PReceiverRegistered = false;
        super.onPause();
    }
}
