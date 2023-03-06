package com.gl.autotrack;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.gl.autotrack.util.ViewMsgUtil;

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
                View v = (View) msg.obj;
                trackViewOnClick(v);
                break;
            case Constants.MESSAGE_TRACK_TAB_LAYOUT_SELECT:
                Object tab = msg.obj;
                trackTabLayoutSelected(tab);
                break;
        }
    }

    public void trackTabLayoutSelected(Object tab) {
        String hint = ViewMsgUtil.getTabMsg(tab);
        if (GLAutoTrackManager.instance().isDebug()) {
            Log.i(TAG, "trackTabLayoutSelected: " + tab + " " + hint);
        }
        if (GLAutoTrackManager.instance().getListener() != null) {
            GLAutoTrackManager.instance().getListener().trackTabLayoutSelected(tab, hint);
        }
    }

    public void trackViewOnClick(View view) {
        String code = ViewMsgUtil.getViewMsg(view);
        if (GLAutoTrackManager.instance().isDebug()) {
            Log.i(TAG, "trackViewOnClick: " + view + " " + code);
        }
        if (GLAutoTrackManager.instance().getListener() != null) {
            GLAutoTrackManager.instance().getListener().trackViewOnClick(view, code);
        }
    }
}
