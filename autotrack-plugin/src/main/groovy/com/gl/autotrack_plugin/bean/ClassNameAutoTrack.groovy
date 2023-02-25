package com.gl.autotrack_plugin.bean;

class ClassNameAutoTrack {
    public String className
    boolean isShouldModify = false

    ClassNameAutoTrack(String className) {
        this.className = className
    }

    boolean isAndroidGenerated() {
        return className.contains('R$') ||
                className.contains('R2$') ||
                className.contains('R.class') ||
                className.contains('R2.class') ||
                className.contains('BuildConfig.class')
    }
}