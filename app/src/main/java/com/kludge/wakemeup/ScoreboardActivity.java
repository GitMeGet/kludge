package com.kludge.wakemeup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoreboardActivity extends AppCompatActivity {

    ArrayList<Pair<String, String>> userInfo = new ArrayList<>(); //pair of strings of USERNAME + SNOOZEFREQ
    Firebase rootRef = new Firebase("https://wakemeup-1373.firebaseio.com"); //firebase ref


    ScoreAdapter scoreAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        //TEMP!! // TODO: 14/7/2016
        loadUsers(userInfo);

        ListView scoreList = (ListView) findViewById(R.id.view_scoreboard_list);
        assert scoreList != null;
        scoreAdapter = new ScoreAdapter(this, userInfo);

        scoreList.setAdapter(scoreAdapter);
        scoreAdapter.notifyDataSetChanged();
    }

    //fetch data from firebase database and load in the arraylist with data
    private void loadUsers(final ArrayList<Pair<String, String>> userInfo){

        rootRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    HashMap userMap = (HashMap) data.getValue();
                    userInfo.add(new Pair<>((String)userMap.get("username"),
                            (Integer.parseInt(userMap.get("snoozeFreq").toString())>0)?"has snoozed "+ userMap.get("snoozeFreq")+" times already!":"has not snoozed yet!"));
                }

                scoreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "The read failed: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
//arrayAdapter for the 'scoreboard'
class ScoreAdapter extends ArrayAdapter<Pair<String, String>>{

    public ScoreAdapter(Context context, ArrayList<Pair<String, String>> userInfo){
        super(context, 0, userInfo);
    }

    //todo: add in onClickListener to wake user up or something
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //retrieve individual userInfo
        final Pair<String, String> userInfo = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.scoreboard_list_item, parent, false);

        TextView scorebUser = (TextView) convertView.findViewById(R.id.view_scoreboard_username);
        TextView scorebSnoozes = (TextView) convertView.findViewById(R.id.view_scoreboard_snoozes);

        scorebUser.setText(userInfo.first);
        scorebSnoozes.setText(userInfo.second);

        return convertView;
        //super.getView(position, convertView, parent);
    }
}