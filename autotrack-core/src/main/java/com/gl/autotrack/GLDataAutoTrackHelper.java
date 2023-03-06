package com.gl.autotrack;

import android.view.View;

import com.gl.autotrack.constant.Constants;

import java.lang.ref.WeakReference;

@SuppressWarnings("unused")
public class GLDataAutoTrackHelper {

    private static final String TAG = GLDataAutoTrackHelper.class.getSimpleName();

    public static void trackTabLayoutSelected(Object object, Object tab) {
        GLAutoTrackManager.instance().post(new WeakReference<>(tab), Constants.MESSAGE_TRACK_TAB_LAYOUT_SELECT);
    }

    public static void trackViewOnClick(View view) {
        GLAutoTrackManager.instance().post(new WeakReference<>(view), Constants.MESSAGE_TRACK_VIEW_ON_CLICK);
    }
}
