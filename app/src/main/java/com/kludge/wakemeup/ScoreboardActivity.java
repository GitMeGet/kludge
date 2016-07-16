package com.kludge.wakemeup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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


import com.example.sylta.myapplication.wmudatastore.myApi.MyApi;
import com.example.sylta.myapplication.wmudatastore.myApi.model.*;
import com.example.sylta.myapplication.wmudatastore.myApi.model.UserEntity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardActivity extends AppCompatActivity {

    ArrayList<Pair<String, String>> userInfo; //userInfo has USERNAME and SNOOZE_FREQ+TIMING pair

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        //TEMP!! // TODO: 14/7/2016
        loadUsers();

        new DatastoreTask().execute(new Pair<Context, String>(this, "Manfredo"));

        ListView scoreList = (ListView) findViewById(R.id.view_scoreboard_list);
        assert scoreList != null;
        ScoreAdapter scoreAdapter = new ScoreAdapter(this, userInfo);

        scoreList.setAdapter(scoreAdapter);
    }

    private ArrayList<Pair<String,String>> loadUsers(){
        if(userInfo == null)
            userInfo = new ArrayList<>();

        new DatastoreLoadAsyncTask().execute(new Pair<>(getApplicationContext(), "name"));

        return userInfo;
    }

    public class DatastoreLoadAsyncTask extends AsyncTask<Pair<Context, String>, Void, List<UserEntity>> {
        private MyApi myApiService = null;
        private Context context;

        @Override
        protected List<UserEntity> doInBackground(Pair<Context, String>... params) {
            if(myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("https://wakemeup-1373.appspot.com/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }

            context = params[0].first;
            String command = params[0].second;

            try {
                UserEntityCollection userList =  myApiService.loadAllUsers().execute();

                for(UserEntity user : userList.getItems()){
                    userInfo.add(new Pair<>(user.getUsername(), "has snoozed "+user.getSnoozeFreq()+" times since "+"??????"));
                }

                userInfo.add(new Pair<>("pasczaBiceps", "has snoozed "+333+" times since "+"??????"));

                return userList.getItems();
            } catch (IOException e) {
                return null;
                //e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(List<UserEntity> result) {
            if(result == null) {
                Toast.makeText(context, "FAIL", Toast.LENGTH_LONG).show();
                return;
            }

            for(UserEntity user : result){
                userInfo.add(new Pair<>(user.getUsername(), "has snoozed "+user.getSnoozeFreq()+" times since "+"??????"));
            }
        }
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


