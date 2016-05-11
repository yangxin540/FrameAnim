package com.juan.frameanimdemo.util;

import android.text.TextUtils;
import android.util.Log;

import com.juan.frameanimdemo.GlobalApplication;
import com.juan.frameanimdemo.model.Folder;
import com.juan.frameanimdemo.model.FrameAnimation;
import com.juan.frameanimdemo.model.FrameTime;
import com.juan.frameanimdemo.view.FrameImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FrameAnimationUtil {

    private final static String TAG = "FrameImage";
    private static FrameAnimationUtil self = null;
    private static HashMap<String, FrameAnimation> FRAMES = null;
    private FrameImageView.OnExceptionListener onExceptionListener;

    public static final String ANIM_CACHE_DIR = Utils.getCacheDir(GlobalApplication.mAppContetxt) + File.separator + "frames";
    public static final String ANIM_CONFIG_FILENAME = "config.json";
    public static final String ANIM_DOWNLOAD_FILE_END_WITH_ZIP = ".zip";
    public static final String ANIM_HUDONG_DIANLIAN_01 = "hudong_dianlian_01_png";
    public static final String ANIM_HUDONG_DIANLIAN_02 = "hudong_dianlian_02_png";
    public static final String ANIM_HUDONG_DIANLIAN_03 = "hudong_dianlian_03_png";
    public static final String ANIM_HUDONG_DIANLIAN_04 = "hudong_dianlian_04_png";
    public static final String ANIM_HUDONG_NIZHUAN = "hudong_nizhuan_png";
    public static final String ANIM_HUDONG_NIZHUAN_02 = "hudong_nizhuan02_png";
    public static final String ANIM_HUDONG_SHANGHUA = "hudong_shanghua_png";
    public static final String ANIM_HUDONG_SHUNZHUAN = "hudong_shunzhuan_png";
    public static final String ANIM_HUDONG_SHUNZHUAN_02 = "hudong_shunzhuan02_png";
    public static final String ANIM_HUDONG_TOUGAI = "hudong_tougai_png";
    public static final String ANIM_HUDONG_XIAHUA = "hudong_xiahua_png";
    public static final String ANIM_HUDONG_YOUGAI = "hudong_yougai_png";
    public static final String ANIM_HUDONG_ZUOGAI = "hudong_zuogai_png";
    public static final String ANIM_RD_BUDAOWENG = "rd_budaoweng_png";
    public static final String ANIM_RD_CHUANGANQI = "rd_chuanganqi_png";
    public static final String ANIM_RD_JKDFDT = "rd_jkdfdt_png";
    public static final String ANIM_RD_MOREN = "rd_moren_png";
    public static final String ANIM_RD_SAYHELLO = "rd_sayhello_png";
    public static final String ANIM_RD_TAIKONG = "rd_taikong_png";

    public static final String FRAME_KEY_HELLO = "hello";
    public static final String FRAME_KEY_NORMAL_REPEAT = "normal_repeat";
    public static final String FRAME_KEY_NORMAL_ONCE = "normal_once";
    public static final String FRAME_KEY_MOTION_SENSOR_ALARM = "motion_sensor_alarm";
    public static final String FRAME_KEY_MOTION_DETECTION_ALARM = "motion_detection_alarm";
    public static final String FRAME_KEY_CLICK = "click";
    public static final String FRAME_KEY_SLIDE_RIGHT = "slide_right";
    public static final String FRAME_KEY_SLIDE_MULT_RIGHT = "slide_mult_right";
    public static final String FRAME_KEY_SLIDE_UP = "slide_up";
    public static final String FRAME_KEY_SLIDE_LEFT = "slide_left";
    public static final String FRAME_KEY_SLIDE_MULT_LEFT = "slide_mult_left";
    public static final String FRAME_KEY_SLIDE_DOWN = "slide_down";
    public static final String FRAME_KEY_TOUCH_HEAD = "touch_head";
    public static final String FRAME_KEY_TOUCH_RIGHT_HAND = "touch_right_hand";
    public static final String FRAME_KEY_TOUCH_LEFT_HAND = "touch_left_hand";
    public static final String FRAME_KEY_SKILL_AIR = "skill_air";
    public static final String FRAME_KEY_SKILL_WEATHER = "skill_weather";
    public static final String FRAME_KEY_SKILL_CHAT = "skill_chat";
    public static final String FRAME_KEY_SKILL_CONSTELLATION = "skill_constellation";
    public static final String FRAME_KEY_SKILL_FM = "skill_FM";
    public static final String FRAME_KEY_SKILL_NAOZHONG = "skill_naozhong";
    public static final String FRAME_KEY_SKILL_SOS = "skill_sos";
    public static final String FRAME_KEY_SKILL_TIME = "skill_time";
    public static final String FRAME_KEY_BACKGROUND_DAY = "buding_taiyang";
    public static final String FRAME_KEY_BACKGROUND_NIGHT = "buding_yueliang";

    private static String CACHE_FILE = "";
    private static String CONFIG_FILE = "";
    private static String CONFIG_FILE_ASSETS = "json/config.json";
    private static String ANIMATION_FILE = "frame_animation_files.zip";

    private boolean isUseLoc;

    private String mFolder;

    private FrameAnimationUtil() {

    }

    public static FrameAnimationUtil getInstace() {
        return getInstace(null, false);
    }

    public static FrameAnimationUtil getInstace(boolean isFromDownload) {
        return getInstace(null, isFromDownload);
    }

    private static FrameAnimationUtil getInstace(OnLoadingListener onLoadingListener, boolean isFromDownload) {
        if (self == null) {
            self = new FrameAnimationUtil();
            self.setOnLoadingListener(onLoadingListener);
            self.init();
        } else {
            self.setOnLoadingListener(onLoadingListener);
        }
        if ((FRAMES == null || FRAMES.size() == 0) && !isFromDownload) {
            self.loadFrameAnimations();
        }
        return self;
    }

    public void init() {
        CACHE_FILE = ANIM_CACHE_DIR + File.separator + ANIMATION_FILE;
        CONFIG_FILE = ANIM_CACHE_DIR + File.separator + "config.json";

        File cacheDir = new File(ANIM_CACHE_DIR);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    public void loadFrameAnimations() {
        // unzipAssetToCache();
        readFrameConfig();
    }

    public List<String> getFrameByKey(String key) {
        try {
            FrameAnimation frame = FRAMES.get(key);
            if (frame == null) {
                return null;
            }

            List<FrameTime> times = frame.getTimes();
            if (times == null || times.isEmpty()) {
                return null;
            }

            FrameTime usedTime = null;
            for (FrameTime time : times) {
                if (time == null) {
                    continue;
                }
                if (usedTime == null) {
                    usedTime = time;
                    continue;
                }
                String startTime = time.getStartTime();
                String endTime = time.getEndTime();
                if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
                    continue;
                }
                if (DateUtil.isIn(startTime, endTime)) {
                    usedTime = time;
                    break;
                }
            }
            return listFromTime(usedTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> listFromTime(FrameTime time) {
        if (time == null) {
            return null;
        }
        List<Folder> folders = time.getFolders();
        if (folders == null || folders.isEmpty()) {
            return null;
        }

        int pos = new Random().nextInt(folders.size());
        Folder fold = folders.get(pos);
        mFolder = fold.getFolder();

        List<String> files = new ArrayList<String>();
        setUseLoc(fold.isUseLocal());
        if (!fold.isUseLocal()) {
            File folderFile = new File(ANIM_CACHE_DIR + File.separator + mFolder);
            File[] subFiles = folderFile.listFiles();
            if (subFiles != null && subFiles.length > 0) {
                for (File f : subFiles) {
                    files.add(f.getAbsolutePath());
                }
            }
        } else {
            String[] list = null;
            try {
                File file = new File(ANIM_CACHE_DIR + File.separator + mFolder);
                if (file.exists()) {
                    list = file.list();
                } else {
                    list = GlobalApplication.mAppContetxt.getAssets().list(mFolder);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (list == null || list.length == 0) {
                return null;
            }
            // List<String> files = Arrays.asList(list);
            for (String f : list) {
                files.add(mFolder + File.separator + f);
            }
        }

        // Util.log(TAG, "读取到的文件列表：" + files);
        return files;
    }

    public List<FrameAnimation> readFrameConfig() {
        try {
            InputStream inputStream = null;
            Log.d(TAG, "state readFrameConfig");

            // 检查缓存中有没有配置文件，要是有就直接读取此文件，没有就去读assets下面的
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                inputStream = GlobalApplication.mAppContetxt.getAssets().open(CONFIG_FILE_ASSETS);
            } else {
                inputStream = new FileInputStream(configFile);
            }

            StringBuilder buffer = new StringBuilder();

            BufferedReader buffreader = null;
            try {
                buffreader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = buffreader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                buffreader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (buffreader != null) {
                    try {
                        buffreader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (TextUtils.isEmpty(buffer)) {
                return null;
            }

            FRAMES = new HashMap<String, FrameAnimation>();
            List<FrameAnimation> anims = JsonUtil.getObjects(buffer.toString(), FrameAnimation.class);
            Log.d(TAG, "anims:" + anims);
            for (FrameAnimation frameAnimation : anims) {
                FRAMES.put(frameAnimation.getKey(), frameAnimation);
            }
            Log.d(TAG, "FRAMES:" + FRAMES);
            return anims;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static boolean isReleased() {
//        return SharedPreferencesUtil.getBooleanValue(ANIMATION_FILE, false);
//    }

    /*public boolean unzipAssetToCache() {
        try {
            // SharedPreferencesUtil.setBooleanValue(srcFile, false);
            if (isReleased()) {
                return false;
            }
            File cacheFile = new File(CACHE_FILE);
            boolean releaseRslt = AssetUtil.releaseFile(ANIMATION_FILE, CACHE_FILE, getOnLoadingListener());
            if (!releaseRslt) {
                return false;
            }

            boolean unzipRslt = ZipUtil.unzipFile(cacheFile, Base.ANIM_CACHE_DIR, getOnLoadingListener());
            if (!unzipRslt) {
                return false;
            }

            SharedPreferencesUtil.setBooleanValue(ANIMATION_FILE, true);
        } catch (Exception e) {
            e.printStackTrace();
            if (onExceptionListener != null) {
                onExceptionListener.haveException();
            }
        }
        return false;
    }*/

    public OnLoadingListener getOnLoadingListener() {
        return onLoadingListener;
    }

    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        this.onLoadingListener = onLoadingListener;
    }

    private OnLoadingListener onLoadingListener;

    public static interface OnLoadingListener {
        public void onLoadingChanged(int percent);
    }

    public void setOnExceptionListener(FrameImageView.OnExceptionListener listener) {
        this.onExceptionListener = listener;
    }

    public void unRegistExceptionListener() {
        this.onExceptionListener = null;
    }

    public boolean isUseLoc() {
        return isUseLoc;
    }

    public void setUseLoc(boolean isUseLoc) {
        this.isUseLoc = isUseLoc;
    }

    public String getFolder() {
        return mFolder;
    }

}
