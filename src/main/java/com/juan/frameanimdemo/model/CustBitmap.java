package com.juan.frameanimdemo.model;

import android.graphics.Bitmap;

public class CustBitmap {

    private boolean isLast;
    private Bitmap bitmap;
    private String file;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean isLast) {
        this.isLast = isLast;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
