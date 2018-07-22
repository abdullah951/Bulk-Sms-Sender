package com.example.hehehehe.bulksmsking;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;




/**
 * Created by hehehehe on 2/10/2018.
 */

public class MyService extends Service {
    private final static String TAG = "MyServices";
    private DatabaseHelper dh;
    List<addressname> arrayList = new ArrayList<>();
    btretrieveall bt;
    private int countSentMess = 0;
    private int countDelMess = 0;
    private int i;
    private int k = 0;
    String message = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db();
        String contacts = null;

        if(intent!=null||intent.getExtras()!=null){
            contacts= (String) intent.getExtras().get("contactsString");
            message =(String)intent.getExtras().get("message");
        }

        Log.e(TAG,"SMS Tab textView "+contacts);
        Log.e(TAG,"SMS Tab textView "+message);
        String[] file = contacts.split("\\s+");
        Log.e(TAG,"file size "+String.valueOf(file.length));
        int count=file.length;

        int timeToCompete = 0;
        while(count>0){
            String filePa = "";
            int foundAtIndex = 0;

            for(int j=0;j<arrayList.size();j++){
                if(arrayList.get(j).getFileName().equals(file[count-1])){
                    foundAtIndex = j;
                }
            }
            if(!(filePa.length()>0)){
                Log.e("internal error",filePa);
            }
            String aBuffer = "";
            try {
                File myFile = new File( arrayList.get(foundAtIndex).getFilePath());
                FileInputStream fIn = new FileInputStream(myFile);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                String aDataRow = "";
                while ((aDataRow = myReader.readLine()) != null) {
                    aBuffer += aDataRow;
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e(TAG,"SMS Tab aBuffer "+aBuffer);
            String[] personsToSend = aBuffer.split("\\s+");
            i=0;
            timeToCompete = timeToCompete + 10*personsToSend.length;
            Handler mHandler = new Handler();
            Runnable runnable=new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG,"outside k = "+k);
                    if (k == personsToSend.length) {

                    }else {
                        sendSMS(personsToSend[k], message);
                        mHandler.postDelayed(this, 100);
                    }
                    k++;
                }
            };
            mHandler.post(runnable);
            k = 0;
            count--;
        }
        stopSelf(30000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void db() {
        dh = new DatabaseHelper(getApplicationContext());
        if(!(dh.getCount()>0)) {
            Toast.makeText(getApplicationContext(), "Please Choose File First", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            bt = new btretrieveall(getApplicationContext());
            arrayList = bt.execute("retrieveAll").get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class sentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode())
            {
                case Activity.RESULT_OK:{
                    Toast.makeText(getBaseContext(), "SMS sent ",
                            Toast.LENGTH_SHORT).show();
                    countSentMess++;}
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Generic failure",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "No service",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "Null PDU",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio off",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    class deliverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode())
            {
                case Activity.RESULT_OK:{
                    Toast.makeText(getBaseContext(), "SMS delivered",
                            Toast.LENGTH_SHORT).show();
                    countDelMess++;}
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getBaseContext(), "SMS not delivered",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";


        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
        PendingIntent sentPI = PendingIntent.getBroadcast(getBaseContext(), 0,
                new Intent(SENT), PendingIntent.FLAG_CANCEL_CURRENT);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(getBaseContext(), 0,
                new Intent(DELIVERED), PendingIntent.FLAG_CANCEL_CURRENT);


        //---when the SMS has been sent---
        getApplicationContext().registerReceiver(new sentReceiver(),new IntentFilter(SENT));

        //---when the SMS has been delivered---
        getApplicationContext().registerReceiver(new deliverReceiver(), new IntentFilter(DELIVERED));

        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber,null,message,sentPI,deliveredPI);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "SMS sending failed...",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
