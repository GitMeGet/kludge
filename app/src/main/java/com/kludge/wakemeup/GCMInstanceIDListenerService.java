package com.kludge.wakemeup;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by Yu Peng on 7/7/2016.
 */
public class GCMInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.
        //sendRegistrationToServer("shitzu", refreshedToken);
    }
    // [END refresh_token]

    // sends userId/token pair to backend server
    private void sendRegistrationToServer(String userId, String token) {

        new ServletPostAsyncTask().execute(new GCMParams(
                getApplicationContext(), "saveToken", userId, token, "", "", "", "")
        );

    }

}
