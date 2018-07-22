package com.example.hehehehe.bulksmsking;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehehehe on 2/5/2018.
 */

public class btretrieveall extends AsyncTask<String,Void,List<addressname>> {

    private List<addressname> paths ;
    Context context;
    private DatabaseHelper dh;
    public btretrieveall(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        dh = new DatabaseHelper(context);
        paths = new ArrayList<>();
    }

    @Override
    protected void onPostExecute(List<addressname> aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected List<addressname> doInBackground(String... params) {
        String method = params[0];
        switch(method){
            case "retrieveAll":{
                if(dh.getCount()>0){
                    Cursor c = dh.getAllData();
                    if(c.getCount() != 0 && c!=null) {
                        while (c.moveToNext()) {
                            paths.add(new addressname(Integer.parseInt(c.getString(0)), c.getString(1), c.getString(2)));
                        }
                        return paths;
                    }else
                        return null;
                }
            }
            case "delete":{
                if(dh.getCount()>0){
                    String id = params[1];
                    dh.deleteData(id);
                }

            }
        }
        return null;
    }
}
