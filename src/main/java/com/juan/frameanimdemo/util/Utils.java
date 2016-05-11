package com.juan.frameanimdemo.util;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.juan.frameanimdemo.GlobalApplication;

import java.io.File;

/**
 * Created by yangxin on 16/5/10.
 */
public class Utils {

    public static int WINDOW_WIDTH;
    public static int WINDOW_HEIGHT;

    public static String getCacheDir(Context context) {
        String cachePath = "";
        File file = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            file = context.getExternalCacheDir();
        } else {
            file = context.getCacheDir();
        }

        if (file != null) {
            cachePath = file.getPath();
        }

        return cachePath;
    }

    public static float dip2px(float dipValue) {
        final float scale = getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }

    public static int dip2pxInt(float dipValue) {
        return (int) dip2px(dipValue);
    }

    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        try {
            dm = GlobalApplication.mAppContetxt.getResources().getDisplayMetrics();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dm;
    }


}
