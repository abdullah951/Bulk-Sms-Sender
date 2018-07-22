package com.example.hehehehe.bulksmsking;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by hehehehe on 2/4/2018.
 */

public class backgroundTask extends AsyncTask<String, Void, String[]> {

    Context ctx;
    DatabaseHelper dh;
    backgroundTask(Context ctx){
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        dh = new DatabaseHelper(ctx);
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(String... params) {

        String method = params[0];
        switch (method){
            case "insert":{

                String fileName = params[1];
                String filePath = params[2];
                Cursor c = dh.getAllData();
                boolean b = true;
                while(c.moveToNext()){
                    if(fileName.equals(c.getString(1))&&filePath.equals(c.getString(2))){
                        b = false;
                    }
                }
                if(b){
                    SQLiteDatabase db = dh.getWritableDatabase();
                    dh.insertData(fileName,filePath);
                    db.close();
                    return null;
                }

                return new String[]{};
            }

            case "retrieve":{
                Cursor c = dh.getAllData();
                c.moveToLast();
                String[] str = new String[]{c.getString(0),c.getString(1),c.getString(2)};
                Log.e("information","retrieved");
                return str;
            }
            case "read":{

            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] aVoid) {
        super.onPostExecute(aVoid);

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}
