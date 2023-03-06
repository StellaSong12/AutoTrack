package com.gl.autotrack;

import android.view.View;

@SuppressWarnings("unused")
public class GLDataAutoTrackHelper {

    private static final String TAG = GLDataAutoTrackHelper.class.getSimpleName();

    public static void trackTabLayoutSelected(Object object, Object tab) {
        GLAutoTrackManager.instance().post(tab, Constants.MESSAGE_TRACK_TAB_LAYOUT_SELECT);
    }

    public static void trackViewOnClick(View view) {
        GLAutoTrackManager.instance().post(view, Constants.MESSAGE_TRACK_VIEW_ON_CLICK);
    }
}
