package com.kludge.wakemeup;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScoreboardActivity extends AppCompatActivity {

    public static final int ID_SEND_REQUEST = 100;

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
                            (Integer.parseInt(userMap.get("snoozeFreq").toString())>0)?"has snoozed "+ userMap.get("snoozeFreq")+" times since "+userMap.get("lastAlarmTime")+" already!":"has not snoozed yet!"));
                }

                scoreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "The read failed: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        switch (view.getId()) {
            case R.id.view_requestlist:
                menu.add(0, ID_SEND_REQUEST, 0, "Send Request");
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case ID_SEND_REQUEST:
                //send a forced activity to wake user up straight... call? text?


                Toast.makeText(getApplicationContext(), "Request Sent!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
//arrayAdapter for the 'scoreboard'
class ScoreAdapter extends ArrayAdapter<Pair<String, String>>{

    public ScoreAdapter(Context context, ArrayList<Pair<String, String>> userInfo){
        super(context, 0, userInfo);
    }

    //viewholder
    public class ViewHolder{
        TextView scorebUser;
        TextView scorebSnoozes;
        ImageView scorebPropic;
    }

    //todo: add in onClickListener to wake user up or something
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //retrieve individual userInfo
        final Pair<String, String> userInfo = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.scoreboard_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.scorebUser = (TextView) convertView.findViewById(R.id.view_scoreboard_username);
        viewHolder.scorebSnoozes = (TextView) convertView.findViewById(R.id.view_scoreboard_snoozes);
        viewHolder.scorebPropic = (ImageView) convertView.findViewById(R.id.view_scoreboard_propic);

        viewHolder.scorebUser.setText(userInfo.first);
        viewHolder.scorebSnoozes.setText(userInfo.second);
        new LoadImageFromURL(viewHolder).execute(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());

        return convertView;
        //super.getView(position, convertView, parent);
    }
}

class LoadImageFromURL extends AsyncTask<String, Void, Bitmap>{

    ScoreAdapter.ViewHolder viewHolder;

    LoadImageFromURL(ScoreAdapter.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }


    @Override
    protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {
                URL url = new URL(params[0]);
                InputStream is = url.openConnection().getInputStream();
                Bitmap bitMap = BitmapFactory.decodeStream(is);
                return bitMap;

            } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
            // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
            }

    @Override
    protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            viewHolder.scorebPropic.setImageBitmap(result);
            }

}

