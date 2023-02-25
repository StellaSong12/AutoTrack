package com.gl.autotrack;


import android.util.Log;
import android.view.View;

@SuppressWarnings("unused")
public class GLDataAutoTrackHelper {

    private static final String TAG = GLDataAutoTrackHelper.class.getSimpleName();

    public static void trackTabLayoutSelected(Object object, Object tab) {
        String hint = ViewMsgUtil.getTabMsg(tab);
        if (GLAutoTrackManager.instance().isDebug()) {
            Log.i(TAG, "trackTabLayoutSelected: " + object + " " + tab + " " + hint);
        }
        if (GLAutoTrackManager.instance().getListener() != null) {
            GLAutoTrackManager.instance().getListener().trackTabLayoutSelected(object, tab, hint);
        }
    }

    public static void trackViewOnClick(View view) {
        String code = ViewMsgUtil.getViewMsg(view);
        if (GLAutoTrackManager.instance().isDebug()) {
            Log.i(TAG, "trackViewOnClick: " + view + " " + code);
        }
        if (GLAutoTrackManager.instance().getListener() != null) {
            GLAutoTrackManager.instance().getListener().trackViewOnClick(view, code);
        }
    }
}
