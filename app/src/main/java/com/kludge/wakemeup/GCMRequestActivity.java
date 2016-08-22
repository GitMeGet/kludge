package com.kludge.wakemeup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Created by Yu Peng on 19/7/2016.
 */
public class GCMRequestActivity extends AppCompatActivity {

    public static final String SENDER_ID = "744483356919";
    public static final int ID_SEND_REQUEST = 100;

    public static String targetId; // save targetId too!
    EditText mTargetIdEditText;

    //firebase and reqAdapter stuff
    ArrayList<Pair<String, Pair<String, String>>> userInfo = new ArrayList<>(); //pair of strings of USERNAME + USERID
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(); //firebase ref
    RequestAdapter requestlistAdapter;

    private AlarmDetails alarm;

    private String userId;
    private String username;
    private String timeInMillis;
    private String requestMessage;
   private long alarmId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_gcm);

        userId = getSharedPreferences("preferences_user", MODE_PRIVATE).getString("userId", "");
        username = getSharedPreferences("preferences_user", MODE_PRIVATE).getString("username", "");

        // get alarm from AlarmLab
        alarmId = getIntent().getLongExtra("alarmId", -1);
        alarm = AlarmLab.get(getApplicationContext()).getAlarmDetails(alarmId);

        // if user had previously entered targetId, show it
        targetId = alarm.getTargetId();

        // get timeInMillis
        timeInMillis = Long.toString(alarm.getTimeInMillis());

        // get message
        requestMessage = "Can you WakeMeUp at "+alarm.getHour()+":"+(alarm.getMin()<10?"0":"")+alarm.getMin()+"?";

        //firebase list of users
        loadUsers(userInfo);

        ListView requestList = (ListView) findViewById(R.id.view_requestlist);
        assert requestList != null;
        requestlistAdapter = new RequestAdapter(this, userInfo);

        requestList.setAdapter(requestlistAdapter);
        requestlistAdapter.notifyDataSetChanged();

        registerForContextMenu(requestList);
    }

    //just to send request
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

                targetId = userInfo.get(info.position).second.first; //fetch userID from the position and arrayList

                new ServletPostAsyncTask().execute(new GCMParams(
                        getApplicationContext(), "requestTarget", userId , username,"", targetId,
                        timeInMillis, requestMessage, Long.toString(alarmId)));

                Toast.makeText(getApplicationContext(), "Request Sent!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean validateInputs(){
        // if no targetId provided by user
        if (targetId.equals("") && mTargetIdEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "OI! Fill in targetId leh!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    //fetch data from firebase database and load in the arraylist with data
    private void loadUsers(final ArrayList<Pair<String, Pair<String,String>>> userInfo){

        rootRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    HashMap userMap = (HashMap) data.getValue();
                    userInfo.add(new Pair<>((String)userMap.get("username"), new Pair<>((String)userMap.get("userID"),(String)userMap.get("photoUrl"))));
                }

                requestlistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "The read failed: " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}

//arrayAdapter for the 'scoreboard'
class RequestAdapter extends ArrayAdapter<Pair<String, Pair<String,String>>> {

    public RequestAdapter(Context context, ArrayList<Pair<String, Pair<String,String>>> userInfo){
        super(context, 0, userInfo);
    }

    //viewholder
    public class ViewHolder{
        TextView requestbUser;
        ImageView requestbPropic;
    }

    //todo: add in onClickListener to wake user up or something
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //retrieve individual userInfo
        final Pair<String, Pair<String, String>> userInfo = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.requestlist_list_item, parent, false);

        convertView.setLongClickable(true);

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.requestbUser = (TextView) convertView.findViewById(R.id.view_requestlist_username);
        viewHolder.requestbPropic = (ImageView) convertView.findViewById(R.id.view_requestlist_propic);

        viewHolder.requestbUser.setText(userInfo.first);
        new ReqLoadImageFromURL(viewHolder).execute(userInfo.second.second);

        return convertView;
        //super.getView(position, convertView, parent);
    }
}

class ReqLoadImageFromURL extends AsyncTask<String, Void, Bitmap>{

    RequestAdapter.ViewHolder viewHolder;

    ReqLoadImageFromURL(RequestAdapter.ViewHolder viewHolder) {this.viewHolder = viewHolder;}

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

        viewHolder.requestbPropic.setImageBitmap(result);
    }

}

