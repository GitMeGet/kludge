package com.kludge.wakemeup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/*
 * Created by Yu Peng on 13/7/2016.
 */
public class MessagingActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_messaging);

        // check if activity using layout with fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null){

            // don't do anything if being restored from previous saved state
            // or could have overlapping fragments
            if (savedInstanceState != null)
                return;

            // create new MessagingFragment to place inside fragment_container
            MessagingFragment messagingFragment = new MessagingFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            Bundle bundle = new Bundle();
            String targetId = getIntent().getStringExtra("targetId");
            String targetUsername = getIntent().getStringExtra("targetUsername");

            System.out.println("4 " + targetId);

            bundle.putString("targetId", targetId);
            bundle.putString("targetUsername", targetUsername);
            messagingFragment.setArguments(bundle);

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, messagingFragment).commit();

        }

    }
}
