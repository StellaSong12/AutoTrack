package com.gl.autotrack;

import android.os.HandlerThread;

public class AutoTrackThread extends HandlerThread {

    private static volatile AutoTrackThread sThread;

    public AutoTrackThread(String name) {
        super(name);
    }

    public static AutoTrackThread getInstance() {
        if (sThread == null) {
            synchronized (AutoTrackThread.class) {
                if (sThread == null) {
                    sThread = new AutoTrackThread("web-socket-handler");
                    sThread.start();
                }
            }
        }
        return sThread;
    }
}