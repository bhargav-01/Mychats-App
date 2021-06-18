package com.example.mymessage.fragments.Adapter;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class sendNotification {

    public sendNotification(String message, String heading, String notificationKey) throws JSONException {
        JSONObject notificationObject=new JSONObject(
                "{'contents':{'en':'" + message + "'},"+
                        "'include_player_ids':['" + notificationKey + "'],"+
                        "'headings':{'en':'" + heading + "'}}");

        OneSignal.postNotification(notificationObject,null);
    }



}
