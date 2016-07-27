package com.kludge.wakemeup;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
* Created by Yu Peng on 18/7/2016.
*/
public class ServletPostAsyncTask extends AsyncTask<GCMParams, Void, String> {
    private Context context;
    String type;
    String token;
    String userId;
    String targetId;
    String timeInMillis;
    String message;
    String alarmId;

    @Override
    protected String doInBackground(GCMParams... params) {
        context = params[0].getmContext();
        type = params[0].getType();
        token = params[0].getToken();
        userId = params[0].getUserId();
        targetId = params[0].getTargetId();
        timeInMillis = params[0].getTimeInMillis();
        message = params[0].getMessage();
        alarmId = params[0].getAlarmId();

        try {
            // Set up the request
            //URL url = new URL("http://10.0.2.2:8080/hello");
            URL url = new URL("http://wakemeup-1373.appspot.com/hello");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Build name data request params
            Map<String, String> nameValuePairs = new HashMap<>();
            nameValuePairs.put("type", type);
            nameValuePairs.put("token", token);
            nameValuePairs.put("userId", userId);
            nameValuePairs.put("targetId", targetId);
            nameValuePairs.put("timeInMillis", timeInMillis);
            nameValuePairs.put("message", message);
            nameValuePairs.put("alarmId", alarmId);

            String postParams = buildPostDataString(nameValuePairs);

            // Execute HTTP Post
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(postParams);
            writer.flush();
            writer.close();
            outputStream.close();
            connection.connect();

            // Read response
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                return response.toString();
            }
            return "Error: " + responseCode + " " + connection.getResponseMessage();

        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private String buildPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
    }
}
