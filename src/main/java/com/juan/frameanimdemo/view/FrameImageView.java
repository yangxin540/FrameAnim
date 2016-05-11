package com.juan.frameanimdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.juan.frameanimdemo.model.CustBitmap;
import com.juan.frameanimdemo.util.BitmapUtils;
import com.juan.frameanimdemo.util.Utils;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class FrameImageView extends SurfaceView implements Callback {

    private final static String TAG = "FrameImage";
    private final static String LOCK_DISPLAY = "lock_display";
    private final static String LOCK_CHECK = "lock_check";

    private LinkedBlockingQueue<CustBitmap> mCachedBitmaps;
    private List<String> mResourceFile;

    private SurfaceHolder mSurfaceHolder;

    private int mCurrentItem;
    private int mCachedSize = 2;
    private int mCheckSize = 1;

    private DisplayThread mDisplayThread;
    private CheckCachedThread mCheckCachedThread;

    private int mDuration = 1000 / 42;
    private boolean alignTop = false;
    private boolean oneShot = false;
    private boolean isUseLoc;
    private OnFrameStopListener onFrameStopListener;
    private OnExceptionListener onExceptionListener;

    private Bitmap mLastBitmap;
    private Paint mDrawPaint;
    private Matrix mDrawMatrix;

    public FrameImageView(Context context) {
        super(context);
        init();
    }

    public FrameImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCachedBitmaps = new LinkedBlockingQueue<CustBitmap>();
        mDrawPaint = new Paint();
        mDrawMatrix = new Matrix();
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        setZOrderOnTop(true);// 设置画布 背景透明
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mLastBitmap = null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            Log.d(TAG, "surfaceCreated");
            //解决从首页进入设置页面-进入设置页面任一子页面，返回-返回到首页，动画显示空白的问题； 
            //原因：从消息中心返回到设置页面时，首页可见，于是就是调用surfaceview的create方法，进而启动线上动画线程  
            //解决：注释掉surfaceCreated方法中start动画方法，我们自己来控制
            //start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        stop();
    }

    public void start() {
        try {
            Log.d(TAG, "start()");

            if (mCheckCachedThread == null || !mCheckCachedThread.isRunning()) {
                mCheckCachedThread = new CheckCachedThread();
            }

            if (mDisplayThread == null || !mDisplayThread.isRunning()) {
                mDisplayThread = new DisplayThread();
            }
            if (!mCheckCachedThread.isAlive())
                mCheckCachedThread.start();
            if (!mDisplayThread.isAlive())
                mDisplayThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearResource() {
        try {
            mLastBitmap = mCachedBitmaps.poll().getBitmap();
            for (int i = 0; i < mCachedBitmaps.size(); i++) {
                mCachedBitmaps.poll().getBitmap().recycle();
            }
            mCachedBitmaps.clear();
            mResourceFile = null;
            mCurrentItem = 0;
        } catch (Exception e) {
            //do nothing
        }
    }

    public void stop() {
        try {
            Log.d(TAG, "stop()");

            if (mCheckCachedThread != null) {
                mCheckCachedThread.stopThread();
                mCheckCachedThread.interrupt();
                mCheckCachedThread = null;
            }

            if (mDisplayThread != null) {
                mDisplayThread.stopThread();
                mDisplayThread.interrupt();
                mDisplayThread = null;
            }

            clearResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mDisplayThread != null) {
            mDisplayThread.pauseThread();
        }
        if (mCheckCachedThread != null) {
            mCheckCachedThread.pauseThread();
        }
    }

    public void resume() {
        if (mDisplayThread != null) {
            mDisplayThread.resumeThread();
        }
        if (mCheckCachedThread != null) {
            mCheckCachedThread.resumeThread();
        }
    }

    public Bitmap getOneBitmap() {
        try {
            if (mLastBitmap == null && mCachedBitmaps != null) {
                mLastBitmap = mCachedBitmaps.poll().getBitmap();
            }
            return mLastBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class CheckCachedThread extends Thread {
        private volatile boolean mRunning = false;
        private volatile boolean mPause = false;

        @Override
        public void run() {
            try {
                setName("thread-checkcache");
                mRunning = true;

                Log.i("FrameImageView", "CheckCachedThread enter mRunning:" + mRunning);

                mPause = false;
                while (mRunning) {
                    checkCache();

                    if (mPause) {
                        synchronized (LOCK_CHECK) {
                            LOCK_CHECK.wait();
                        }
                    }
                    SystemClock.sleep(20);
                }

                Log.i("FrameImageView", "CheckCachedThread exit mRunning:" + mRunning);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean isRunning() {
            return mRunning;
        }

        public void stopThread() {
            try {
                Log.i("FrameImageView", "DisplayThread pauseThread() set mRunning = false");
                mRunning = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void pauseThread() {
            try {
                Log.i("FrameImageView", "DisplayThread pauseThread() set mPause = true");
                mPause = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void resumeThread() {
            try {
                mPause = false;

                synchronized (LOCK_CHECK) {
                    LOCK_CHECK.notifyAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DisplayThread extends Thread {
        private volatile boolean mRunning = false;
        private volatile boolean mPause = false;

        @Override
        public void run() {
            try {
                setName("thread-display");
                mRunning = true;

                Log.i("FrameImageView", "DisplayThread enter mRunning:" + mRunning);

                while (mRunning) {
                    if (mCachedBitmaps == null || mCachedBitmaps.isEmpty()) {
                        SystemClock.sleep(mDuration);
                        continue;
                    }
                    CustBitmap bitmap = mCachedBitmaps.poll();
                    drawBitmap(bitmap);
                    if (bitmap.isLast() && onFrameStopListener != null) {
                        onFrameStopListener.onFrameStop();
                    }

                    if (mPause) {
                        synchronized (LOCK_DISPLAY) {
                            LOCK_DISPLAY.wait();
                        }
                    }
                    SystemClock.sleep(mDuration);
                }

                Log.i("FrameImageView", "DisplayThread exit mRunning:" + mRunning);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean isRunning() {
            return mRunning;
        }

        public void stopThread() {
            try {
                Log.i("FrameImageView", "DisplayThread stopThread() set mRunning = false");
                mRunning = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void pauseThread() {
            try {
                Log.i("FrameImageView", "DisplayThread pauseThread() set mPause = true");
                mPause = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void resumeThread() {
            try {
                mPause = false;

                synchronized (LOCK_DISPLAY) {
                    LOCK_DISPLAY.notifyAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void drawBitmap(CustBitmap cb) {
        try {
            Bitmap bitmap = cb.getBitmap();
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            float scale = (float) Utils.WINDOW_WIDTH / (float) bitmapWidth;
            int height = (int) (bitmapHeight * scale);
            int top = -(height - Utils.WINDOW_HEIGHT) / 2;

            Canvas canvas = mSurfaceHolder.lockCanvas();
            if (canvas != null) { // BUG 1118
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//			canvas.drawPaint(mClearPaint);

                mDrawPaint.setAntiAlias(true);
                mDrawMatrix.setScale(scale, scale);
                canvas.setMatrix(mDrawMatrix);
                if (isAlignTop()) {
                    canvas.drawBitmap(bitmap, 0, 0, mDrawPaint);
                } else {
                    canvas.drawBitmap(bitmap, 0, top / scale, mDrawPaint);
                }

                // bitmap.recycle();
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        } catch (Exception e) {
            if (onExceptionListener != null) {
                onExceptionListener.haveException();
            }
            e.printStackTrace();
        }
    }

    public void checkCache() {
        try {
            if (mCachedBitmaps == null) {
                Log.d(TAG, "checkCache mCachedBitmaps is null,return");
                return;
            }
            if (mCachedBitmaps.size() >= mCheckSize) {
//                Log.d(TAG, "checkCache mCachedBitmaps mCachedBitmaps.size() >= mCheckSize,return-->cache is full");
                return;
            }

            int fillSize = mCachedSize - mCachedBitmaps.size();

//            Log.d(TAG, "checkCache fillSize:" + fillSize + "  mCurrentItem:" + mCurrentItem);

            String fileName = "";
            for (int i = 0; i < fillSize; i++) {
                Bitmap bitmap = null;

                if (mResourceFile != null && mCurrentItem < mResourceFile.size()) {
                    fileName = mResourceFile.get(mCurrentItem);
                    if (!isUseLoc) {
                        bitmap = BitmapUtils.readBitMap(fileName);
                    } else {
                        bitmap = BitmapUtils.readBitMapForAssets(getContext(), fileName);
                    }
                }

//                Log.d(TAG, "mCurrentItem:" + mCurrentItem + ";fileName" + fileName);
                mCurrentItem++;
                if (bitmap == null) {
//                    Log.d(TAG, "bitmap is null,fileName:" + fileName);
                    continue;
                }
                CustBitmap cb = new CustBitmap();
                cb.setFile(fileName);

                if (mCurrentItem >= getResourceLength()) {
                    cb.setLast(true);
//                    Log.d(TAG, "set mCurrentItem=0");
                    mCurrentItem = 0;
                }
                cb.setBitmap(bitmap);
                mCachedBitmaps.add(cb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getResourceLength() {
        try {
            return mResourceFile.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public void setResourceFile(List<String> files, boolean isUseLoc) {
        try {
            this.isUseLoc = isUseLoc;
            mResourceFile = null;
            this.mResourceFile = files;
            if (mResourceFile == null || mResourceFile.isEmpty()) {
                return;
            }
            setCachedSize(mResourceFile.size());
            mCurrentItem = 0;
//            Log.d(TAG, "mResourceFile:" + mResourceFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCachedSize(int resourceSize) {
        mCachedSize = resourceSize / 10;
        if (mCachedSize <= 0) {
            mCachedSize = 2;
        }
        mCheckSize = mCachedSize / 2;
    }

    public boolean isAlignTop() {
        return alignTop;
    }

    public void setAlignTop(boolean alignTop) {
        this.alignTop = alignTop;
    }

    public OnFrameStopListener getOnFrameStopListener() {
        return onFrameStopListener;
    }

    public void setOnFrameStopListener(OnFrameStopListener onFrameStopListener) {
        this.onFrameStopListener = onFrameStopListener;
    }

    public static interface OnFrameStopListener {
        public void onFrameStop();
    }

    public boolean isOneShot() {
        return oneShot;
    }

    public void setOneShot(boolean oneShot) {
        this.oneShot = oneShot;
    }

    public void setOnExceptionListener(OnExceptionListener listener) {
        this.onExceptionListener = listener;
    }

    public interface OnExceptionListener {
        void haveException();
    }

    public void destroy() {
        try {
            stop();
            mCachedBitmaps = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
