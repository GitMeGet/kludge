package com.kludge.wakemeup;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardActivity extends AppCompatActivity {

    ArrayList<Pair<String, String>> userInfo; //userInfo has USERNAME and SNOOZE_FREQ+TIMING pair

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        //TEMP!! // TODO: 14/7/2016
        userInfo = new ArrayList<>();
        userInfo.add(new Pair<>("paszaBiceps", "has snoozed 16-0 times since 69am!"));


        
        ListView scoreList = (ListView) findViewById(R.id.view_scoreboard_list);
        assert scoreList != null;
        ScoreAdapter scoreAdapter = new ScoreAdapter(this, userInfo);

        scoreList.setAdapter(scoreAdapter);
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
