package com.juan.frameanimdemo;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

import com.juan.frameanimdemo.util.Utils;

/**
 * Created by yangxin on 16/5/10.
 */
public class GlobalApplication extends Application {

    public static Context mAppContetxt;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContetxt = this;
        initSize();
    }

    public void initSize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (Utils.WINDOW_WIDTH <= 0) {
            Utils.WINDOW_WIDTH = dm.widthPixels;
        }
        if (Utils.WINDOW_HEIGHT <= 0) {
            Utils.WINDOW_HEIGHT = dm.heightPixels;
        }
    }

}
