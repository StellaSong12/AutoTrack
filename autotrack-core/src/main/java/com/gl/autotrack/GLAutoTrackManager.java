package com.gl.autotrack;

import android.content.Context;
import android.view.View;

import java.io.File;

public class GLAutoTrackManager {

    private static GLAutoTrackManager sInstance;
    private boolean debug = false;

    private TrackListener listener;

    public static GLAutoTrackManager instance() {
        if (sInstance == null) {
            synchronized (GLAutoTrackManager.class) {
                if (sInstance == null) {
                    sInstance = new GLAutoTrackManager();
                }
            }
        }
        return sInstance;
    }

    private GLAutoTrackManager() {

    }

    public TrackListener getListener() {
        return listener;
    }

    public void setListener(TrackListener listener) {
        this.listener = listener;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    public File getSpFilePath(Context context) {
        return ViewMsgUtil.getSpFilePath(context);
    }

    public interface TrackListener {

        void trackTabLayoutSelected(Object object, Object tab, String hint);

        void trackViewOnClick(View view, String text);
    }
}
