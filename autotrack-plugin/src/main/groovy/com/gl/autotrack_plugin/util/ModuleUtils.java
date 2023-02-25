package com.gl.autotrack_plugin.util;

import com.gl.autotrack_plugin.config.GLAutoTrackHookConfig;
import com.gl.autotrack_plugin.log.Logger;

import java.net.URLClassLoader;

public class ModuleUtils {
    public static boolean isAutoTrackInstall = false;

    public static void checkModuleStatus(URLClassLoader classLoader) {
        try {
            Logger.info("全埋点模块集成 = " + GLAutoTrackHookConfig.GL_AutoTrack_API);
            isAutoTrackInstall = classLoader.loadClass(GLAutoTrackHookConfig.GL_AutoTrack_API.replace("/", ".")) != null;
            Logger.info("全埋点模块集成状态 = " + isAutoTrackInstall);
        } catch (Exception e) {
            Logger.warn("全埋点模块未集成 " + e);
            isAutoTrackInstall = false;
        }
    }
}
