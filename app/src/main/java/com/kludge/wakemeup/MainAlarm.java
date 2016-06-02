package com.kludge.wakemeup;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

public class MainAlarm extends AppCompatActivity {
    private final int ID_CONTEXT_MENU_EDIT=1, ID_CONTEXT_MENU_DELETE=2; //id tags for the ContextMenu items

    //array containing DESCRIPTION OF ALARMS? !!!! MUST IT BE STATIC???
    static ArrayList<AlarmDetails> alarms = new ArrayList<AlarmDetails>();

    //arrayAdapter for the ListView
    static AlarmAdapter alarmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alarm);

        //initiates AlarmAdapter for ListView, (context, layout, strArray)
        alarmAdapter = new AlarmAdapter(this, alarms);

        //sets up the adapter to this ListView
        ListView listView = (ListView) findViewById(R.id.list_alarms);
        assert listView != null;
        listView.setAdapter(alarmAdapter);

        //sets up add_alarm button's onClickListener
        Button addAlarm = (Button) findViewById(R.id.add_alarm);
        assert addAlarm != null;
        addAlarm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                //adds alarm
                addAlarm();
            }
        });

        //registers ListView for Context Menu to Edit,Delete alarms
        registerForContextMenu(listView);
    }

    //setup the Context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,view,menuInfo);

        //if listView is being long clicked, inflate the subMenu
        if(view.getId() == R.id.list_alarms){
           menu.add(0, ID_CONTEXT_MENU_EDIT, 0, "Edit");   //(Group ID Key, MenuItem Key, Order of Item, String to Display)
           menu.add(0, ID_CONTEXT_MENU_DELETE, 0, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem menu){
        switch(menu.getItemId()){
            case ID_CONTEXT_MENU_EDIT:
                //open up edit alarm activity?
                return true;
            case ID_CONTEXT_MENU_DELETE:
                //fetches the AdapterView for the context menu, retrieves the position of the View, delete the alarm according to pos
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menu.getMenuInfo();
                alarmAdapter.remove(alarms.get(info.position));
                return true;
        }
        return false;
    }

    //adds alarm to the alarms ArrayList (TO BE CHANGED! open up new activity with IntentWithResult!
    private void addAlarm() {
        //fetches EditText field
        String alarmName = ((EditText) findViewById(R.id.alarm_name)).getText().toString();
        alarms.add(new AlarmDetails(0,0,alarmName));

        showTimePickerDialog();
    }

    //show the timePicker dialog inside a DialogFragment, TO BE MOVED TO NEW ACTIVITY!
    private void showTimePickerDialog(){
        DialogFragment newFrag = new TimePickerFragment();
        newFrag.show(getSupportFragmentManager(), "timePicker"); //requires instance of a FragmentManager, + unique tag for this fragment
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        //save info by doing eg. outState.putString("key", varName), outState.putFloatArray("key",..
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        //restore info by taking it out eg. var = savedInstanceState.getString("key");
    }
}

//AlarmAdapter for the ListView
class AlarmAdapter extends ArrayAdapter<AlarmDetails> {
    public AlarmAdapter(Context context, ArrayList<AlarmDetails> alarmList){
        super(context, 0, alarmList);
    }

    //returns actual View to be displayed as row within the alarm ListView
    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        //retrieves alarms array for this position
        final AlarmDetails alarm = getItem(pos);

        //check if existing view is being reused, else just inflate the view with custom alarm_list_item xml
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_list_item, parent, false);

        //lookup the TextViews to be populated, ie. alarm name and alarm time
        TextView alarmName = (TextView) convertView.findViewById(R.id.alarm_name);
        TextView alarmTime = (TextView) convertView.findViewById(R.id.alarm_time);

        //gets the switch widget for the View
        Switch aSwitch = (Switch) convertView.findViewById(R.id.alarm_on_state);

        //if the alarm state was on, set aSwitch accordingly ???


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm.toggleOnState();

                notifyDataSetChanged();
            }
        });

        //updates the Views with the data
        alarmName.setText(alarm.strName);
        alarmTime.setText(alarm.nHour+":"+alarm.nMin+(alarm.bOnState?" ON":" OFF")); //CHANGE THIS, ON OFF JUST TO TEST ONLY

        //return completed view to render on screen
        return convertView;
    }
}