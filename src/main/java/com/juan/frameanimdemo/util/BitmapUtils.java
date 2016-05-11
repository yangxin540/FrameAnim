package com.juan.frameanimdemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yangxin on 16/5/10.
 */
public class BitmapUtils {

    public static Bitmap readBitMap(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        Bitmap bitmap = null;
        BitmapFactory.Options opts = getBitmapOptions();
        File animationFile = new File(fileName);
        if (animationFile.exists() && animationFile.isFile()) {
            bitmap = BitmapFactory.decodeFile(fileName, opts);
        }
        return bitmap;
    }

    private static BitmapFactory.Options getBitmapOptions() {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inTempStorage = new byte[16 * 1024];
        opt.inPreferredConfig = Bitmap.Config.ALPHA_8;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        return opt;
    }


    public static Bitmap readBitMapForAssets(Context context, String fileName) throws IOException {
        BitmapFactory.Options opt = getBitmapOptions();
        InputStream is = context.getAssets().open(fileName);
        return BitmapFactory.decodeStream(is, null, opt);
    }

}
