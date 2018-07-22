package com.example.hehehehe.bulksmsking;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static android.nfc.NfcAdapter.EXTRA_ID;


public class SMSSender extends IntentService {

    public static final String INTENT_MESSAGE_SENT = "message.sent";
    public static final String INTENT_MESSAGE_DELIVERED = "message.delivered";

    public static final String EXTRA_MESSAGE = "extra.message";
    public static final String EXTRA_RECEIVERS = "extra.receivers";

    public SMSSender() {
        super("SMSSender");
    }

    private final String TAG = "SendSMS";


    private static class IDGenerator {

        private static final AtomicInteger counter = new AtomicInteger();

        public static int nextValue() {
            return counter.getAndIncrement();
        }
    }
    private BroadcastReceiver messageSent; // <- stored as a field
    private BroadcastReceiver messageDelivered;
    private ArrayList<PendingIntent> sentIntents = null;
    private ArrayList<PendingIntent> deliveredIntents = null;
    private PendingIntent sentPI = null;
    private PendingIntent deliveredPI = null;
    private int k = 0;
    private void sendSMS(String message, String[] receivers) {

        SmsManager sm = SmsManager.getDefault();

        ArrayList<String> parts = sm.divideMessage(message);


        Intent sentIntent = new Intent(INTENT_MESSAGE_SENT);

        int sentID = IDGenerator.nextValue();
        sentPI = PendingIntent.getBroadcast(SMSSender.this, sentID, sentIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Intent deliveryIntent = new Intent(INTENT_MESSAGE_DELIVERED);

        int deliveredID = IDGenerator.nextValue();
        deliveredPI = PendingIntent.getBroadcast(SMSSender.this, deliveredID,
                deliveryIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Log.i(TAG, "sending SMS: parts: " + parts.size() + " message: "
                + message);
// ---when the SMS has been sent---

        messageSent = new SentMessage();
        registerReceiver(messageSent, new IntentFilter(SMSSender.INTENT_MESSAGE_SENT));

        // ---when the SMS has been delivered---
         // <- stored as a field
        messageDelivered = new DeliveredMessage();
        registerReceiver(messageDelivered, new IntentFilter(
                SMSSender.INTENT_MESSAGE_DELIVERED));
        if (parts.size() > 1) {


            sentIntents = new ArrayList<PendingIntent>();
            deliveredIntents = new ArrayList<PendingIntent>();

            for (int i = 0; i < parts.size(); i++) {
                sentIntents.add(sentPI);
                deliveredIntents.add(deliveredPI);
            }


                try {
                    Handler mHandler = new Handler();
                    Runnable runnable=new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG,"outside k = "+k);
                            if (k == receivers.length) {

                            }else {
                                sm.sendMultipartTextMessage(receivers[k], null, parts,
                                        sentIntents, deliveredIntents);
                                mHandler.postDelayed(this, 100);
                            }
                            k++;
                        }
                    };
                    mHandler.post(runnable);

                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "illegal receiver: " + receivers[k]);
                }


        } else {
                try {
                    Handler mHandler = new Handler();
                    Runnable runnable=new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG,"outside k = "+k);
                            if (k == receivers.length) {

                            }else {
                                sm.sendTextMessage(receivers[k], null, parts.get(0), sentPI,
                                        deliveredPI);
                                mHandler.postDelayed(this, 100);
                            }
                            k++;
                        }
                    };
                    mHandler.post(runnable);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "illegal receiver: " + receivers[k]);
                }
            }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        String[] receivers = intent.getStringArrayExtra(EXTRA_RECEIVERS);
        k = 0;
        sendSMS(message, receivers);

    }

    public class SentMessage extends BroadcastReceiver {

        private final String TAG = "SentMessage";

        @Override
        public void onReceive(Context context, Intent intent) {
            long _id = intent.getLongExtra(EXTRA_ID, -1);

            Log.d(TAG, "SentMessage " + EXTRA_MESSAGE);
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Log.d(TAG, "RESULT_OK");
                    Log.d(TAG, String.valueOf(_id));
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Log.d(TAG, "RESULT_ERROR_GENERIC_FAILURE");
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Log.d(TAG, "RESULT_ERROR_NO_SERVICE");
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Log.d(TAG, "RESULT_ERROR_NULL_PDU");
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Log.d(TAG, "RESULT_ERROR_RADIO_OFF");
                    break;
            }

        }
    }

    public class DeliveredMessage extends BroadcastReceiver {

        private final String TAG = "DeliveredMessage ";


        @Override
        public void onReceive(Context context, Intent intent) {

            long _id = intent.getLongExtra(EXTRA_ID, -1);
            //long protocol_id = intent.getLongExtra(EXTRA_PROTOCOL, -1);
            switch (getResultCode()) {
                case Activity.RESULT_OK:
//                    if (_id != -1 && MessageData.deliveredMessage(_id, protocol_id)) {
//                        try {
//                            Database.messageDelivered(_id);
//                            Cursor messageCursor = Database.getCursorByID(MessageOutboxContentProvider.CONTENT_URI, MessageOutboxContentProvider._ID, _id);
//                            messageCursor.close();
//                        } catch (DatabaseRowNotFoundException e) {
//                            Log.e(TAG, e.toString(), e);
//                        }
//                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        // remember to unregister
        unregisterReceiver(messageSent);
        unregisterReceiver(messageDelivered );
    }
}