/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.example.Zex.myapplication.backend;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {
    public static final String API_KEY = "AIzaSyAT-2zA20bT6baw2brAkiyCwMJ6OiK33AY"; //firebase API key
    HashMap<String, String> mHashMap = new HashMap<>();
    HashMap<String, String> mUserIdTokenMap = new HashMap<>();
    ArrayList<String> tokenArray = new ArrayList<>();
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Please use the form to POST to this url0");
    }
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String type = req.getParameter("type");
        String token = req.getParameter("token");
        String userId = req.getParameter("userId");
        String targetId = req.getParameter("targetId");
        String timeInMillis = req.getParameter("timeInMillis");
        String message = req.getParameter("message");
        String alarmId = req.getParameter("alarmId");
        resp.setContentType("application/json");
        String requestToken; // token belonging to alarm requester
        String targetToken;

        System.out.println("SERVER RAN!");

        /*
        // this is how to send a response back
        resp.getWriter().println(text);
         */
        try {
            switch (type) {
                case "saveToken":
                    tokenArray.add(token);
                    mUserIdTokenMap.put(userId, token);
                    System.out.println(token);
                    break;
                case "requestTarget":
                    // retrieve token of targetId
                    targetToken = mUserIdTokenMap.get(targetId);
                    sendPushNotification(userId, targetToken, timeInMillis, message, alarmId, "requestTarget");
                    pushFCMNotification(userId, targetToken, timeInMillis, message, alarmId, "requestTarget");
                    break;
                case "requestAccepted":
                    requestToken = mUserIdTokenMap.get(targetId);
                    sendPushNotification(userId, requestToken, "", "", alarmId, "requestAccepted");
                    pushFCMNotification(userId, requestToken, "", "", alarmId, "requestAccepted");
                    break;
                case "requestRejected":
                    requestToken = mUserIdTokenMap.get(targetId);
                    sendPushNotification(userId, requestToken, "", "", "", "requestRejected");
                    pushFCMNotification(userId, requestToken, "", "", "", "requestRejected");
                    break;
                case "sendP2PMessage":
                    targetToken = mUserIdTokenMap.get(targetId);
                    sendP2PMessage(userId, targetToken, "incomingP2PMessage", message);
                    pushFCMp2p(userId, targetToken, "incomingP2PMessage", message);
                    break;
            }
        }
        catch(Exception e){
        }
    }

    // Method to send Notifications from server to client end.

    public final static String AUTH_KEY_FCM = "AIzaSyDw-NeLlUXei_B-f_XEEAf_p_1osnBhms8";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

// userDeviceIdKey is the device id you will query from your database

    public static void pushFCMNotification(String userDeviceIdKey, String token, String time, String message, String alarmId, String type) throws Exception{

        String authKey = AUTH_KEY_FCM;   // You FCM AUTH key
        String FMCurl = API_URL_FCM;

        URL url = new URL(FMCurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization","key="+authKey);
        conn.setRequestProperty("Content-Type","application/json");

        JSONObject json = new JSONObject();
        json.put("to",userDeviceIdKey.trim());
        JSONObject info = new JSONObject();
        info.put("title", "Notificatoin Title");   // Notification title
        info.put("body", "Hello Test notification"); // Notification body
        json.put("notification", info);

        json.put("messageType", type);
        json.put("userId", userDeviceIdKey);
        json.put("timeInMillis", time);
        json.put("message", message);
        json.put("alarmId", alarmId);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(json.toString());
        wr.flush();
        conn.getInputStream();
    }

    // sends push notification to user associated to token
    private void sendPushNotification(String userId, String targetToken, String timeInMillis,
                                      String message, String alarmId, String messageType) {

        try {
            //Please add here your project API key: "Key for browser apps (with referers)".
            //If you added "API key Key for server apps (with IP locking)" or "Key for Android apps (with certificates)" here
            //then you may get error responses.
            Sender sender = new Sender(API_KEY);
            // use this to send message with payload data
            Message pushNotification = new Message.Builder()
                    .delayWhileIdle(true)
                    .addData("messageType", messageType) //receive this message on client side app
                    .addData("userId", userId)
                    .addData("timeInMillis", timeInMillis)
                    .addData("message", message)
                    .addData("alarmId", alarmId)
                    .build();
            //Use this code to send notification message to a single device
            Result result = sender.send(pushNotification, targetToken, 1);
            System.out.println("Message Result: " + result.toString()); //Print message result on console
            /*
            //Use this code for multicast messages
            MulticastResult multicastResult = sender.send(message, tokenArray, 0);
            //Print multicast message result on console
            System.out.println("Message Result: " + multicastResult.toString());
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendP2PMessage(String userId, String targetToken, String messageType, String message){
        try {
            Sender sender = new Sender(API_KEY);
            // use this to send message with payload data
            Message P2PMessage = new Message.Builder()
                    .delayWhileIdle(true)
                    .addData("messageType", messageType)
                    .addData("message", message)
                    .addData("userId", userId)
                    .build();

            System.out.println(userId + " " + targetToken + "  " + message);
            //Use this code to send notification message to a single device
            Result result = sender.send(P2PMessage, targetToken, 1);
            System.out.println("Message Result: " + result.toString()); //Print message result on console
            /*
            //Use this code for multicast messages
            MulticastResult multicastResult = sender.send(message, tokenArray, 0);
            //Print multicast message result on console
            System.out.println("Message Result: " + multicastResult.toString());
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pushFCMp2p(String userId, String targetToken, String messageType, String message){
        try {
            String authKey = AUTH_KEY_FCM;   // You FCM AUTH key
            String FMCurl = API_URL_FCM;

            URL url = new URL(FMCurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + authKey);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject json = new JSONObject();
            json.put("to", userId.trim());
            JSONObject info = new JSONObject();
            info.put("title", "Notificatoin Title");   // Notification title
            info.put("body", "Hello Test notification"); // Notification body

            json.put("notification", info);
            json.put("messageType", messageType);
            json.put("userId", userId);
            json.put("message", message);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            conn.getInputStream();
        }
        catch(Exception e){
        }
    }

}
