package com.gl.autotrack;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ViewTrackUtil {

    /**
     * 获取可见view及其可见子view的文本
     * @param view
     * @return dfs的第一个 textview 的文本
     */
    @Nullable
    public static String getViewHint(View view) {
        try {
            if (view == null || view.getVisibility() != View.VISIBLE) {
                return null;
            }
            if (view instanceof TextView) {
                CharSequence sequence = ((TextView) view).getText();
                if (sequence != null) {
                    return sequence.toString();
                }
                return null;
            }
            if (view instanceof ViewGroup) {
                int count = ((ViewGroup) view).getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = ((ViewGroup) view).getChildAt(i);
                    String s = getViewHint(child);
                    if (!TextUtils.isEmpty(s)) {
                        return s;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            if (GLAutoTrackManager.instance().isDebug()) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
