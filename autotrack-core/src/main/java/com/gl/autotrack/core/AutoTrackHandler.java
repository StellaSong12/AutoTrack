package com.gl.autotrack.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.gl.autotrack.GLAutoTrackManager;
import com.gl.autotrack.constant.Constants;
import com.gl.autotrack.util.ViewMsgUtil;

import java.lang.ref.WeakReference;

public class AutoTrackHandler extends Handler {

    private static final String TAG = AutoTrackHandler.class.getSimpleName();

    public AutoTrackHandler(HandlerThread thread) {
        super(thread.getLooper());
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case Constants.MESSAGE_TRACK_VIEW_ON_CLICK:
                trackViewOnClick(msg);
                break;
            case Constants.MESSAGE_TRACK_TAB_LAYOUT_SELECT:
                trackTabLayoutSelected(msg);
                break;
        }
    }

    public void trackTabLayoutSelected(Message msg) {
        try {
            WeakReference<Object> v = (WeakReference<Object>) msg.obj;
            Object tab = v.get();
            String hint = ViewMsgUtil.getTabMsg(tab);
            if (GLAutoTrackManager.instance().isDebug()) {
                Log.i(TAG, "trackTabLayoutSelected: " + Thread.currentThread() + " " + tab + " " + hint);
            }
            if (GLAutoTrackManager.instance().getListener() != null) {
                GLAutoTrackManager.instance().getListener().trackTabLayoutSelected(tab, hint);
            }
        } catch (Exception e) {
            //
        }
    }

    public void trackViewOnClick(Message msg) {
        try {
            WeakReference<View> v = (WeakReference<View>) msg.obj;
            View view = v.get();
            String code = ViewMsgUtil.getViewMsg(view);
            if (GLAutoTrackManager.instance().isDebug()) {
                Log.i(TAG, "trackViewOnClick: " + Thread.currentThread() + " " + view + " " + code);
            }
            if (GLAutoTrackManager.instance().getListener() != null) {
                GLAutoTrackManager.instance().getListener().trackViewOnClick(view, code);
            }
        } catch (Exception e) {
            //
        }
    }
}
