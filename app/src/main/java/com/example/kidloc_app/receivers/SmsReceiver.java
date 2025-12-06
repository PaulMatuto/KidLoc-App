package com.example.kidloc_app.receivers;

import android.os.Bundle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.kidloc_app.database.DatabaseHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmsReceiver extends BroadcastReceiver {

    public String messageBody;
    public String sender;
    public static String latitude;
    public static String longitude;


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null || intent.getAction() == null) return;

        Bundle bundle = intent.getExtras();

        String format = intent.getStringExtra("format");

        if (bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus");
            assert pdus != null;
            for (Object pdu : pdus) {
                SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu, format);
                messageBody = msg.getMessageBody();
                sender = msg.getOriginatingAddress();

                Log.d("SMS", "From: " + sender + ", Msg: " + messageBody);
            }
        }

        if (messageBody.startsWith("GPS_") && messageBody.endsWith("_X")) {
            String[] parts = messageBody.substring(4, messageBody.length() - 2).split("_");
            if (parts.length == 2) {
                latitude = parts[0];
                longitude = parts[1];

                Log.d("GPS", "Lat: " + latitude + ", Lon: " + longitude);

                DatabaseHelper db = new DatabaseHelper(context);
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                db.saveLocationLog(latitude, longitude, timestamp);
            }
        }
    }

}
