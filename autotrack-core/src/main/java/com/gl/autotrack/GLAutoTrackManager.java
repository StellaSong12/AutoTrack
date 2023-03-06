package com.gl.autotrack;

import android.content.Context;
import android.os.Message;
import android.view.View;

import com.gl.autotrack.core.AutoTrackHandler;
import com.gl.autotrack.core.AutoTrackThread;
import com.gl.autotrack.util.ViewMsgUtil;

import java.io.File;

public class GLAutoTrackManager {

    private static GLAutoTrackManager sInstance;
    private boolean debug = false;

    private TrackListener listener;
    private AutoTrackHandler handler;

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
        handler = new AutoTrackHandler(AutoTrackThread.getInstance());
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

    public void post(Object o, int type) {
        Message message = handler.obtainMessage();
        message.what = type;
        message.obj = o;
        handler.sendMessage(message);
    }

    public interface TrackListener {

        void trackTabLayoutSelected(Object tab, String hint);

        void trackViewOnClick(View view, String text);
    }
}
