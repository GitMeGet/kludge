package com.kludge.wakemeup;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/*
 * Created by Yu Peng on 8/6/2016.
 */
public class AlarmJSONSerializer {

    private Context mContext;
    private String mFilename;

    public AlarmJSONSerializer(Context c, String f) {
        mContext = c;
        mFilename = f;
    }

    public void saveAlarms(ArrayList<AlarmDetails> alarms) throws JSONException, IOException {

        // Build an array in JSON
        JSONArray array = new JSONArray();
        for (AlarmDetails a : alarms)
            array.put(a.toJSON());

        // Write the file to disk
        Writer writer = null;
        try {
            // open file for writing
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            // write array onto file as string
            writer.write(array.toString());
        } finally {
            if (writer != null) writer.close();
        }
    }

    public ArrayList<AlarmDetails> loadAlarms() throws IOException, JSONException {
        ArrayList<AlarmDetails> alarms = new ArrayList<AlarmDetails>();
        BufferedReader reader = null;
        try {        // Open and read the file into a StringBuilder
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // Parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            // Build the array of crimes from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                alarms.add(new AlarmDetails(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // Ignore this one; it happens when starting fresh
        } finally {
            if (reader != null) reader.close();
        }
        return alarms;
    }


}
