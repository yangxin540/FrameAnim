package com.juan.frameanimdemo;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.juan.frameanimdemo.util.FrameAnimationUtil;
import com.juan.frameanimdemo.util.Utils;
import com.juan.frameanimdemo.view.FrameImageView;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private FrameImageView mFrameImageView;
    private ViewGroup mLayoutFrame;
    private FrameAnimationUtil mFrameAnimationUtil;

    private Timer mMuliSlideTimer;
    private Timer mNoHandleTimer;
    private Timer mTipTimer;

    private Map<Integer, LinkedList<Integer>> mTipMap;

    private boolean isSlideFlag = true;
    private boolean isPlayNomalAnim;

    private float mXDistance;
    private float mYDistance;
    private float mXLast;
    private float mYLast;

    private static final String TAG = "HomeActivity";
    private static final int PIC_HEIGHT = Utils.dip2pxInt(160);
    private static final int PIC_WIDTH = Utils.dip2pxInt(160);
    private static final int CLICK_VIEW_OUTSIDE = -1;
    private static final int CLICK_VIEW_LEFT = 1;
    private static final int CLICK_VIEW_TOP = 2;
    private static final int CLICK_VIEW_RIGHT = 3;
    private static final int CLICK_VIEW_OTHER = 4;
    private static final int RECT_SUB_LEN = 60;
    private static final int PLAY_SIGN_DELAYMILLIS = 500;
    private static final int NO_HANDLE_DELAYMILLIS = 8500;

    private static final int WHAT_PLAY_LEFT_ANIM = 10;
    private static final int WHAT_PLAY_MULI_LEFT_ANIM = 11;
    private static final int WHAT_PLAY_RIGHT_ANIM = 12;
    private static final int WHAT_PLAY_MULI_RIGHT_ANIM = 13;
    private static final int WHAT_PLAY_NOMAL_REPEAT_ANIM = 14;
    private static final int WHAT_PLAY_NOMAL_ONCE_ANIM = 15;
    private static final int WHAT_FRAMEIMAGEVIEW_EXCEPTION = 16;
    private static final int WHAT_HIDE_TIP = 18;
    private static final int WHAT_PLAY_SKILL_AIR_ANIM = 19;
    private static final int WHAT_PLAY_SKILL_WEATHER_ANIM = 20;
    private static final int WHAT_PLAY_SKILL_CHAT_ANIM = 21;
    private static final int WHAT_PLAY_SKILL_CONSTELLATION_ANIM = 22;
    private static final int WHAT_PLAY_SKILL_FM_ANIM = 23;
    private static final int WHAT_PLAY_SKILL_REMIND_ANIM = 24;
    private static final int WHAT_PLAY_SKILL_SOS_ANIM = 26;
    private static final int WHAT_PLAY_SKILL_TIME_ANIM = 27;
    private static final int WHAT_SHOW_SKILL_TIP = 25;
    private static final int WHAT_DELAY_PLAY_NOMAL_AND_SHOW_TIP = 99;
    private static final int WHAT_DELAY_PLAY_NOMAL = 100;
    private static final int WHAT_SHOW_BLUR = 102;

    private boolean isTopActivity = false;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                switch (msg.what) {
                    case WHAT_PLAY_LEFT_ANIM:
                        playSlideLeftAnim();
                        break;
                    case WHAT_PLAY_MULI_LEFT_ANIM:
                        playSlideMultLeftAnim();
                        break;
                    case WHAT_PLAY_RIGHT_ANIM:
                        playSlideRightAnim();
                        break;
                    case WHAT_PLAY_MULI_RIGHT_ANIM:
                        playSlideMultRightAnim();
                        break;
                    case WHAT_PLAY_NOMAL_REPEAT_ANIM:
                        playNomalRepeatAnim();
                        break;
                    case WHAT_PLAY_NOMAL_ONCE_ANIM:
                        playNomalOnceAnim();
                        break;
                    case WHAT_FRAMEIMAGEVIEW_EXCEPTION:
                        createFrame();
                        break;
                    case WHAT_PLAY_SKILL_AIR_ANIM:
                        playSkillAirAnim();
                        break;
                    case WHAT_PLAY_SKILL_WEATHER_ANIM:
                        playSkillWeatherAnim();
                        break;
                    case WHAT_PLAY_SKILL_CHAT_ANIM:
                        playSkillChatAnim();
                        break;
                    case WHAT_PLAY_SKILL_CONSTELLATION_ANIM:
                        playSkillConstellationAnim();
                        break;
                    case WHAT_PLAY_SKILL_FM_ANIM:
                        playSkillFMAnim();
                        break;
                    case WHAT_PLAY_SKILL_REMIND_ANIM:
                        playSkillRemindAnim();
                        break;
                    case WHAT_PLAY_SKILL_SOS_ANIM:
                        playSkillSosAnim();
                        break;
                    case WHAT_PLAY_SKILL_TIME_ANIM:
                        playSkillTimeAnim();
                        break;
                    case WHAT_DELAY_PLAY_NOMAL_AND_SHOW_TIP:
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mHandler.sendEmptyMessage(WHAT_PLAY_NOMAL_REPEAT_ANIM);
                                mHandler.sendEmptyMessage(WHAT_SHOW_SKILL_TIP);
                            }
                        }, 100);
                        break;
                    case WHAT_DELAY_PLAY_NOMAL:
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mHandler.sendEmptyMessage(WHAT_PLAY_NOMAL_REPEAT_ANIM);
                            }
                        }, 100);
                        break;
                    case WHAT_SHOW_BLUR:
                        try {
                            Bitmap blurBitmap = (Bitmap) msg.obj;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        createFrame();
    }

    private void initView() {
        mLayoutFrame = (ViewGroup) findViewById(R.id.layout_frame);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isTopActivity = true;
        if (mHandler != null) {
            mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(WHAT_PLAY_NOMAL_REPEAT_ANIM);
        destroyTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        destroyFrame();
        destroyTimer();
        if (mTipMap != null) {
            mTipMap.clear();
            mTipMap = null;
        }
    }

    private void destroyFrame() {
        if (mFrameImageView != null) {
            mFrameAnimationUtil.unRegistExceptionListener();
            mFrameImageView.destroy();
            mFrameImageView = null;
        }
        mLayoutFrame.removeAllViews();
    }

    private void createFrame() {
        if (mFrameImageView != null) {
            destroyFrame();
        }
        mFrameImageView = new FrameImageView(this);
        mFrameImageView.setOnExceptionListener(new FrameImageView.OnExceptionListener() {

            @Override
            public void haveException() {
                mHandler.sendEmptyMessage(WHAT_FRAMEIMAGEVIEW_EXCEPTION);
            }
        });
        mLayoutFrame.addView(mFrameImageView);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mFrameAnimationUtil = FrameAnimationUtil.getInstace();
                    mFrameAnimationUtil.setOnExceptionListener(new FrameImageView.OnExceptionListener() {

                        @Override
                        public void haveException() {
                            mHandler.sendEmptyMessage(WHAT_FRAMEIMAGEVIEW_EXCEPTION);
                        }
                    });
                    mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void playNomalRepeatAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_NORMAL_REPEAT);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setEnabled(true);
        mFrameImageView.setOneShot(false);
        mFrameImageView.setOnClickListener(animViewOnClickListener);
        mFrameImageView.setOnTouchListener(animViewOnTouchListener);
        noHandleListener();
    }

    private void playNomalOnceAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_NORMAL_ONCE);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnClickListener(animViewOnClickListener);
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private void playSlideUpAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }

        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SLIDE_UP);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnClickListener(animViewOnClickListener);
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                // BeepUtil.stopBeep();
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private void playSlideDownAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }

        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SLIDE_DOWN);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnClickListener(animViewOnClickListener);
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                // BeepUtil.stopBeep();
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private void playSlideRightAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SLIDE_RIGHT);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnClickListener(animViewOnClickListener);
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                // BeepUtil.stopBeep();
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private void playSlideMultRightAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SLIDE_MULT_RIGHT);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnClickListener(animViewOnClickListener);
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                // BeepUtil.stopBeep();
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private void playSlideLeftAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SLIDE_LEFT);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnClickListener(animViewOnClickListener);
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                // BeepUtil.stopBeep();
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private void playSlideMultLeftAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SLIDE_MULT_LEFT);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnClickListener(animViewOnClickListener);
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                // BeepUtil.stopBeep();
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private void playTouchHeadAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_TOUCH_HEAD);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnClickListener(animViewOnClickListener);
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                // BeepUtil.stopBeep();
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private void playTouchRightHandAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_TOUCH_RIGHT_HAND);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnClickListener(animViewOnClickListener);
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                // BeepUtil.stopBeep();
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private void playTouchLeftHandAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_TOUCH_LEFT_HAND);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnClickListener(animViewOnClickListener);
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                // BeepUtil.stopBeep();
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private void playOnclickAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_CLICK);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                // BeepUtil.stopBeep();
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private void playSkillAirAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        destroyNoHandleTimer();
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SKILL_AIR);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL_AND_SHOW_TIP);
            }
        });
    }

    private void playSkillWeatherAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        destroyNoHandleTimer();
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SKILL_WEATHER);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
//        playBeep(R.raw.weather);//因为动画与音效在不同手机不一致,效果不好,所有去掉
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL_AND_SHOW_TIP);
            }
        });
    }

    private void playSkillChatAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        destroyNoHandleTimer();
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SKILL_CHAT);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
//        playBeep(R.raw.chat);//因为动画与音效在不同手机不一致,效果不好,所有去掉
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL_AND_SHOW_TIP);
            }
        });
    }

    private void playSkillConstellationAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        destroyNoHandleTimer();
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SKILL_CONSTELLATION);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
//        playBeep(R.raw.constellation);//因为动画与音效在不同手机不一致,效果不好,所有去掉
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL_AND_SHOW_TIP);
            }
        });
    }

    private void playSkillFMAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        destroyNoHandleTimer();
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SKILL_FM);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
//        playBeep(R.raw.fm);//因为动画与音效在不同手机不一致,效果不好,所有去掉
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL_AND_SHOW_TIP);
            }
        });
    }

    private void playSkillRemindAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        destroyNoHandleTimer();
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SKILL_NAOZHONG);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
//        playBeep(R.raw.remind);//因为动画与音效在不同手机不一致,效果不好,所有去掉
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL_AND_SHOW_TIP);
            }
        });
    }

    private void playSkillSosAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        destroyNoHandleTimer();
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SKILL_SOS);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
//        playBeep(R.raw.sos);//因为动画与音效在不同手机不一致,效果不好,所有去掉
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL_AND_SHOW_TIP);
            }
        });
    }

    private void playSkillTimeAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        destroyNoHandleTimer();
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_SKILL_TIME);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
//        playBeep(R.raw.time);//因为动画与音效在不同手机不一致,效果不好,所有去掉
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL_AND_SHOW_TIP);
            }
        });
    }

    private void playMotionSensorAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        destroyNoHandleTimer();
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_MOTION_SENSOR_ALARM);
        if (files == null || files.isEmpty()) {
            //do something

            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private void playMotionDetectionAnim() {
        if (mFrameAnimationUtil == null || mFrameImageView == null) {
            return;
        }
        destroyNoHandleTimer();
        mFrameImageView.setOneShot(true);
        List<String> files = mFrameAnimationUtil.getFrameByKey(FrameAnimationUtil.FRAME_KEY_MOTION_DETECTION_ALARM);
        if (files == null || files.isEmpty()) {
            //do something
            return;
        }
        mFrameImageView.stop();
        mFrameImageView.setResourceFile(files, mFrameAnimationUtil.isUseLoc());
        mFrameImageView.start();
        mFrameImageView.setOnFrameStopListener(new FrameImageView.OnFrameStopListener() {
            @Override
            public void onFrameStop() {
                mFrameImageView.stop();
                mFrameImageView.setOnFrameStopListener(null);
                mHandler.sendEmptyMessage(WHAT_DELAY_PLAY_NOMAL);
            }
        });
    }

    private View.OnClickListener animViewOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        }
    };

    private View.OnTouchListener animViewOnTouchListener = new View.OnTouchListener() {

        private int res;
        private int slideLeftCount = 0;
        private int slideRightCount = 0;
        private static final int SLIDE_COUNT = 3;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                int mMoveMinLen = 200;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        destroyNoHandleTimer();
                        isSlideFlag = true;
                        mXDistance = mYDistance = 0f;
                        mXLast = event.getRawX();
                        mYLast = event.getRawY();
                        res = touchPicArea((int) mXLast, (int) mYLast);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        boolean islideArea = isSlideArea((int) event.getRawX(), (int) event.getRawY());
                        if (!islideArea) {
                            isSlideFlag = false;
                        }
                        destroyTimer();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (!isSlideFlag) {
                            break;
                        }
                        final float curX = event.getRawX();
                        final float curY = event.getRawY();

                        // noHandleListener();

                        mXDistance += Math.abs(curX - mXLast);
                        mYDistance += Math.abs(curY - mYLast);
                        if (curX > mXLast && mXDistance > mYDistance && mXDistance > mMoveMinLen) {
                            mHandler.sendEmptyMessage(WHAT_PLAY_RIGHT_ANIM);
                            slideRightCount++;
                            mXLast = curX;
                            mYLast = curY;
                            try {
                                mMuliSlideTimer = new Timer("mMuliSlideTimer");
                                mMuliSlideTimer.schedule(new TimerTask() {

                                    @Override
                                    public void run() {
                                        if (slideRightCount >= SLIDE_COUNT) {
                                            mHandler.sendEmptyMessage(WHAT_PLAY_MULI_RIGHT_ANIM);
                                        }
                                        slideRightCount = 0;
                                    }
                                }, PLAY_SIGN_DELAYMILLIS);
                            } catch (Exception e) {
                            }
                            return true;
                        }
                        if (curX < mXLast && mXDistance > mYDistance && mXDistance > mMoveMinLen) {
                            mHandler.sendEmptyMessage(WHAT_PLAY_LEFT_ANIM);
                            slideLeftCount++;
                            mXLast = curX;
                            mYLast = curY;
                            try {
                                mMuliSlideTimer = new Timer("mMuliSlideTimer");
                                mMuliSlideTimer.schedule(new TimerTask() {

                                    @Override
                                    public void run() {
                                        if (slideLeftCount >= SLIDE_COUNT) {
                                            mHandler.sendEmptyMessage(WHAT_PLAY_MULI_LEFT_ANIM);
                                        }
                                        slideLeftCount = 0;
                                    }
                                }, PLAY_SIGN_DELAYMILLIS);
                            } catch (Exception e) {
                            }
                            return true;
                        }
                        if (curY > mYLast && mXDistance < mYDistance && mYDistance > mMoveMinLen) {
                            mXLast = curX;
                            mYLast = curY;
                            playSlideDownAnim();
                            return true;
                        }
                        if (curY < mYLast && mXDistance < mYDistance && mYDistance > mMoveMinLen) {
                            mXLast = curX;
                            mYLast = curY;
                            playSlideUpAnim();
                            return true;
                        }
                        if (CLICK_VIEW_TOP == res) {
                            playTouchHeadAnim();
                        } else if (CLICK_VIEW_RIGHT == res) {
                            playTouchRightHandAnim();
                        } else if (CLICK_VIEW_LEFT == res) {
                            playTouchLeftHandAnim();
                        } else if (CLICK_VIEW_OTHER == res) {
                            playOnclickAnim();
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };

    private void noHandleListener() {
        if (isTopActivity && !isPlayNomalAnim) {
            mNoHandleTimer = new Timer("mNoHandleTimer");
            mNoHandleTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(WHAT_PLAY_NOMAL_ONCE_ANIM);
                }
            }, NO_HANDLE_DELAYMILLIS);
        }
    }

    private int touchPicArea(int x, int y) {
        int top = (Utils.WINDOW_HEIGHT - PIC_HEIGHT) / 2;
        int left = (Utils.WINDOW_WIDTH - PIC_WIDTH) / 2;
        int right = left + PIC_WIDTH;
        int bottom = top + PIC_HEIGHT;
        int rightH = left + PIC_WIDTH / 2;
        Rect rect = new Rect(left, top, right, bottom);
        Rect topRect = new Rect(left + PIC_WIDTH / 4, top, right - PIC_WIDTH / 4, top + PIC_HEIGHT / 4);
        Rect leftRect = new Rect(left, top + RECT_SUB_LEN, right - PIC_WIDTH / 2 - RECT_SUB_LEN, bottom - RECT_SUB_LEN);
        Rect rightRect = new Rect(rightH + RECT_SUB_LEN, top + RECT_SUB_LEN, right, bottom - RECT_SUB_LEN);
        boolean contains = rect.contains(x, y);

        if (contains) {
            if (topRect.contains(x, y)) {
                return CLICK_VIEW_TOP;
            } else if (leftRect.contains(x, y)) {
                return CLICK_VIEW_LEFT;
            } else if (rightRect.contains(x, y)) {
                return CLICK_VIEW_RIGHT;
            } else {
                return CLICK_VIEW_OTHER;
            }
        }
        return CLICK_VIEW_OUTSIDE;
    }

    private boolean isSlideArea(int x, int y) {
        int top = (Utils.WINDOW_HEIGHT - PIC_HEIGHT) / 2;
        int left = (Utils.WINDOW_WIDTH - PIC_WIDTH) / 2;
        int right = left + PIC_WIDTH;
        int bottom = top + PIC_HEIGHT;
        Rect vRect = new Rect(left, 0, right, Utils.WINDOW_HEIGHT);
        Rect hRect = new Rect(0, top, Utils.WINDOW_WIDTH, bottom);
        if (vRect.contains(x, y)) {
            return true;
        } else if (hRect.contains(x, y)) {
            return true;
        }
        return false;
    }

    private void destroyTimer() {
        if (mMuliSlideTimer != null) {
            mMuliSlideTimer.cancel();
            mMuliSlideTimer = null;
        }
        destroyNoHandleTimer();
        destroyTipTimer();
    }

    private void destroyNoHandleTimer() {
        if (mNoHandleTimer != null) {
            mNoHandleTimer.cancel();
            mNoHandleTimer = null;
        }
    }

    private void destroyTipTimer() {
        if (mTipTimer != null) {
            mTipTimer.cancel();
            mTipTimer = null;
        }
    }
}
