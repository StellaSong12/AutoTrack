package com.gl.autotrack.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.gl.autotrack.GLAutoTrackManager;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ViewMsgUtil {
    private static final String PREF_FILE_NAME = "81afee685d46ac1d605785ba265767d8";
    private static SharedPreferences sharedPreferences;
    private static Map<String, String> classNameMap = new HashMap<>();

    /**
     * 用实现id和view映射，压缩记录内容
     * @param v
     * @returnc 映射code + viewId + 可见文本
     */
    @Nullable
    public static String getViewMsg(View v) {
        try {
            if (v != null) {
                String className = v.getClass().getName();
                if (!TextUtils.isEmpty(className)) {
                    String encode = "";
                    if (classNameMap.containsKey(className)) {
                        encode = classNameMap.get(className);
                    } else {
                        encode = hash(className);
                        if (!TextUtils.isEmpty(encode)) {
                            classNameMap.put(className, encode);
                            if (sharedPreferences == null) {
                                sharedPreferences = v.getContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
                            }
                            sharedPreferences.edit().putString(className, encode).apply();
                        }
                    }
                    return encode + " " + getViewId(v) + " " + ViewTrackUtil.getViewHint(v);
                }
            }
        } catch (Exception e) {
            if (GLAutoTrackManager.instance().isDebug()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    public static String getTabMsg(Object obj) {
        try {
            if (obj instanceof TabLayout.Tab) {
                View customView = ((TabLayout.Tab) obj).getCustomView();
                if (customView != null && customView.getVisibility() == View.VISIBLE) {
                    String hint = getViewMsg(customView);
                    if (!TextUtils.isEmpty(hint)) {
                        return hint;
                    }
                }
                View tabView = ((TabLayout.Tab) obj).view;
                if (tabView.getVisibility() == View.VISIBLE) {
                    String hint = getViewMsg(tabView);
                    if (!TextUtils.isEmpty(hint)) {
                        return hint;
                    }
                }
            }
        } catch (Exception e) {
            if (GLAutoTrackManager.instance().isDebug()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    public static File getSpFilePath(Context context) {
        try {
            return new File(context.getApplicationInfo().dataDir + "/shared_prefs/" + PREF_FILE_NAME + ".xml");
        } catch (Exception e) {
            if (GLAutoTrackManager.instance().isDebug()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 简单的散列算法
     * @param s
     * @return
     */
    @Nullable
    private static String hash(String s) {
        try {
            int hash = 0;
            for (int i = 0; i < s.length(); i++) {
                hash = (hash << 5) - hash + s.charAt(i);
            }
            return String.format("%08d", Math.abs(hash) % 100000000);
        } catch (Exception e) {
            if (GLAutoTrackManager.instance().isDebug()) {
                e.printStackTrace();
            }
        }
        return s;
    }

    private static String getViewId(View v) {
        StringBuilder out = new StringBuilder(128);
        try {
            final int id = v.getId();
            if (id != -1) {
                final Resources r = v.getResources();
                String pkgname;
                switch (id&0xff000000) {
                    case 0x7f000000:
                        pkgname="app";
                        break;
                    case 0x01000000:
                        pkgname="android";
                        break;
                    default:
                        pkgname = r.getResourcePackageName(id);
                        break;
                }
                String typename = r.getResourceTypeName(id);
                String entryname = r.getResourceEntryName(id);
                out.append(pkgname);
                out.append(":");
                out.append(typename);
                out.append("/");
                out.append(entryname);
            }
        } catch (Resources.NotFoundException e) {
            if (GLAutoTrackManager.instance().isDebug()) {
                e.printStackTrace();
            }
        }

        return out.toString();
    }
}
