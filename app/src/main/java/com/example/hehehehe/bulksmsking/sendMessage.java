package com.example.hehehehe.bulksmsking;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by hehehehe on 1/30/2018.
 */

public class sendMessage extends android.support.v4.app.Fragment {

    private final static String TAG = "sendMessage";
    private List<addressname> arrayList = new ArrayList<>();
    private btretrieveall bt;
    private TextView textView, textView2;
    private DatabaseHelper dh;
    private ImageView imageView,send1;
    private  Intent serviceIntent;
    private TextView count;
    String t = null;
    Context mContext;
    private static final int REQUEST = 112;
    int i=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.sendmessage, container,false);
        textView = (TextView) rootview.findViewById(R.id.select);
        count = (TextView) rootview.findViewById(R.id.count);
        textView2 = (TextView) rootview.findViewById(R.id.message);
        imageView = (ImageView) rootview.findViewById(R.id.deleteSender);
        send1 = (ImageView) rootview.findViewById(R.id.send);
        mContext = getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootview.setNestedScrollingEnabled(true);
        }

        textView2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG,String.valueOf(s.length()));
                count.setText("Count: "+String.valueOf(s.length()));
            }
        });
        dh = new DatabaseHelper(getContext());
        if (Build.VERSION.SDK_INT >= 23)
        {
            String[] PERMISSIONS = {android.Manifest.permission.SEND_SMS};
            if (!hasPermissions(mContext, PERMISSIONS))
            {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {

            }
        } else
        {

        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(dh.getCount()>0)) {
                    Toast.makeText(getContext(), "Please Choose File First", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    bt = new btretrieveall(getContext());
                    arrayList = bt.execute("retrieveAll").get();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Files to Send SmS");
                String[] fileNames = new String[arrayList.size()];
                for(int i=0;i<arrayList.size();i++){
                    fileNames[i] = arrayList.get(i).getFileName();
                }

                boolean[] checkedItems = {false, false, false, false, false, false, false,false, false, false, false, false, false};
                builder.setMultiChoiceItems(fileNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;

                        // Get the current focused item
                        String currentItem = fileNames[which];

                        // Notify the current action
                    }
                });

// add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder b = new StringBuilder();
                        for(int i=0;i<fileNames.length;i++){
                            if(checkedItems[i]){
                                b.append(fileNames[i]+"  ");

                            }
                        }
                        textView.setText(b);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                if(textView.getText().equals("Select File to Send SmS"))
                    imageView.setVisibility(View.GONE);
                else
                    imageView.setVisibility(View.VISIBLE);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("Select File to Send SmS");
                imageView.setVisibility(View.GONE);
            }
        });
        Handler mHandler = new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {

                if(textView.getText().equals("Select File to Send SmS"))
                    imageView.setVisibility(View.GONE);
                else
                    imageView.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(this, 100);

            }
        };
        mHandler.post(runnable);
        serviceIntent=new Intent(getContext(),MyService.class);
        return rootview;
    }



    @Override
    public void onStart() {
        super.onStart();
        send1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String s = textView.getText().toString();
                String t = textView2.getText().toString();
                if (s.equals("Select File to Send SmS") || (textView2.getText().equals(""))) {
                    if (t.equals("")) {

                        Toast.makeText(getContext(), "Please Write message First", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getContext(), "Please choose file first", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Build.VERSION.SDK_INT >= 23)
                {
                    String[] PERMISSIONS = {android.Manifest.permission.SEND_SMS};
                    if (!hasPermissions(mContext, PERMISSIONS))
                    {
                        ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
                    } else {
                        openDialog();
                    }
                } else
                {
                    openDialog();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private String[] personsToSend;
    private boolean b = false;
    public void openDialog() {

        new AlertDialog.Builder(getContext())
                .setTitle("Send Message")
                .setMessage("Are you sure you want to send the message? ")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = textView.getText().toString();
                        String t = textView2.getText().toString();

//                Toast.makeText(getContext(), "Messages are about to send", Toast.LENGTH_SHORT).show();
//                serviceIntent.putExtra("contactsString", s);
//                serviceIntent.putExtra("message", t);
//                getActivity().startService(serviceIntent);

                        String[] file = s.split("\\s+");
                        int count = file.length;
                        if (count == 1) {
                            String filePa = "";
                            int foundAtIndex = 0;

                            for (int j = 0; j < arrayList.size(); j++) {
                                if (arrayList.get(j).getFileName().equals(file[count - 1])) {
                                    foundAtIndex = j;
                                }
                            }
                            if (!(filePa.length() > 0)) {
                                Log.e("internal error", filePa);
                            }
                            String aBuffer = "";
                            try {
                                File myFile = new File(arrayList.get(foundAtIndex).getFilePath());
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

                            Log.e(TAG, "SMS Tab aBuffer " + aBuffer);
                            String[] personsToSend = aBuffer.split("\\s+");
                            Intent i = new Intent(getContext(), SMSSender.class);
                            i.putExtra(SMSSender.EXTRA_MESSAGE, t);
                            i.putExtra(SMSSender.EXTRA_RECEIVERS, personsToSend);
                            getActivity().startService(i);
                        } else {
                            for(int i = 0;i<file.length;i++){
                                String filePa = "";
                                int foundAtIndex = 0;

                                for (int j = 0; j < arrayList.size(); j++) {
                                    if (arrayList.get(j).getFileName().equals(file[i])) {
                                        foundAtIndex = j;
                                    }
                                }
                                if (!(filePa.length() > 0)) {
                                    Log.e("internal error", filePa);
                                }
                                String aBuffer = "";
                                try {
                                    File myFile = new File(arrayList.get(foundAtIndex).getFilePath());
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

                                Log.e(TAG, "SMS Tab aBuffer " + aBuffer);
                                if(!b){
                                    personsToSend =  aBuffer.split("\\s+");
                                    b = true;
                                }else
                                    personsToSend = concatenate(personsToSend,aBuffer.split("\\s+"));
                            }
                            Intent i = new Intent(getContext(), SMSSender.class);
                            i.putExtra(SMSSender.EXTRA_MESSAGE, t);
                            i.putExtra(SMSSender.EXTRA_RECEIVERS, personsToSend);
                            getActivity().startService(i);
                        }
                    }
                })
                .show();
    }

    public <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
