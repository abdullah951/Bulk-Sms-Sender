package com.example.hehehehe.bulksmsking;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hehehehe on 1/31/2018.
 */

public class addressname {
    private String fileName;
    private String filePath;
    private int counter;

    public addressname(int counter, String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.counter = counter;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}