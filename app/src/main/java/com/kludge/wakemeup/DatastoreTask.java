package com.kludge.wakemeup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.support.v4.util.Pair;
import android.widget.Toast;

import com.example.sylta.myapplication.wmudatastore.myApi.MyApi;
import com.example.sylta.myapplication.wmudatastore.myApi.model.*;
import com.example.sylta.myapplication.wmudatastore.myApi.model.UserEntity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.services.datastore.model.Query;
import com.googlecode.objectify.annotation.Entity;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sylta on 15/7/2016.
 */
//public class DatastoreTask extends

class DatastoreTask extends AsyncTask<Pair<Context, String>, Void, String> {
    private static MyApi myApiService = null;
    private Context context;
    SharedPreferences sharedPrefs;

    @Override
    protected String doInBackground(Pair<Context, String>... params) {
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
        String name = params[0].second;

        try {
            sharedPrefs = context.getSharedPreferences("preferences_user", Context.MODE_PRIVATE);

            UserEntity user = new UserEntity();

            user.setUsername(sharedPrefs.getString("user_username", "MANFREDO"));
            user.setUserID(sharedPrefs.getString("user_userID","NAME_ERROR"));
            user.setTokenID(sharedPrefs.getString("user_tokenID", "TOKEN_ERROR"));
            user.setSnoozeFreq(sharedPrefs.getInt("user_snoozes", 300));

            myApiService.saveData(user).execute();
            return "User Data saved!";

            //return myApiService.loadData().execute().getUsername();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }
}