package com.gl.autotrack_plugin.core

import com.android.build.gradle.BaseExtension
import com.gl.autotrack_plugin.bean.ClassNameAutoTrack
import com.gl.autotrack_plugin.bean.GLAutoTrackExtension
import com.gl.autotrack_plugin.log.Logger


class GLAutoTrackTransformHelper {
    GLAutoTrackExtension extension
    BaseExtension android
    boolean disableGlAutoTrackMultiThread
    boolean disableGlAutoTrackIncremental
    public boolean isHookOnMethodEnter
    HashSet<String> ignoreClass = new HashSet<>(['keyboard'])
    HashSet<String> exclude = new HashSet<>(['android.support',
                                             'androidx',
                                             'com.qiyukf',
                                             'android.arch',
                                             'com.google.android',
                                             "com.tencent.smtt",
                                             "com.umeng.message",
                                             "com.xiaomi.push",
                                             "com.huawei.hms",
                                             "cn.jpush.android",
                                             "cn.jiguang",
                                             "com.meizu.cloud.pushsdk",
                                             "com.vivo.push",
                                             "com.igexin",
                                             "com.getui",
                                             "com.xiaomi.mipush.sdk",
                                             "com.heytap.msp.push",
                                             'com.bumptech.glide',
                                             'com.tencent.tinker'])
    HashSet<String> include = new HashSet<>(['butterknife.internal.DebouncingOnClickListener',
                                             'com.jakewharton.rxbinding.view.ViewClickOnSubscribe',
                                             'com.facebook.react.uimanager.NativeViewHierarchyManager'])
    /** 将一些特例需要排除在外 */
    public static final HashSet<String> special = ['android.support.design.widget.TabLayout$ViewPagerOnTabSelectedListener',
                                                   'com.google.android.material.tabs.TabLayout$ViewPagerOnTabSelectedListener',
                                                   'android.support.v7.app.ActionBarDrawerToggle',
                                                   'androidx.appcompat.app.ActionBarDrawerToggle',
                                                   'androidx.fragment.app.FragmentActivity',
                                                   'androidx.core.app.NotificationManagerCompat',
                                                   'androidx.core.app.ComponentActivity',
                                                   'android.support.v4.app.NotificationManagerCompat',
                                                   'android.support.v4.app.SupportActivity',
                                                   'cn.jpush.android.service.PluginMeizuPlatformsReceiver']
    URLClassLoader urlClassLoader

    GLAutoTrackTransformHelper(GLAutoTrackExtension extension, BaseExtension android) {
        this.extension = extension
        this.android = android
    }

    File androidJar() throws FileNotFoundException {
        String path = getSdkJarDir()
        File jar = new File(path, "android.jar")
        if (!jar.exists()) {
            throw new FileNotFoundException("Android jar not found!\r\n 请确定路径 " + path + " 下是否存在 android.jar 文件")
        }
        return jar
    }

    private String getSdkJarDir() {
        String compileSdkVersion = android.getCompileSdkVersion()
        return String.join(File.separator, android.getSdkDirectory().getAbsolutePath(), "platforms", compileSdkVersion)
    }

    void onTransform() {
        Logger.info("onTransform {\n" + extension + "\n}")
        ArrayList<String> excludePackages = extension.exclude
        if (excludePackages != null) {
            exclude.addAll(excludePackages)
        }
        ArrayList<String> includePackages = extension.include
        if (includePackages != null) {
            include.addAll(includePackages)
        }
    }

    ClassNameAutoTrack AutoTrack(String className) {
        ClassNameAutoTrack classNameAutoTrack = new ClassNameAutoTrack(className)
        if (!classNameAutoTrack.isAndroidGenerated()) {
            for (pkgName in special) {
                if (className.startsWith(pkgName)) {
                    classNameAutoTrack.isShouldModify = true
                    return classNameAutoTrack
                }
            }
            if (extension.useInclude) {
                for (pkgName in include) {
                    if (className.startsWith(pkgName)) {
                        classNameAutoTrack.isShouldModify = true
                        break
                    }
                }
            } else {
                classNameAutoTrack.isShouldModify = true
                for (pkgName in exclude) {
                    if (className.startsWith(pkgName)) {
                        classNameAutoTrack.isShouldModify = false
                        break
                    }
                }
                if (classNameAutoTrack.isShouldModify) {
                    for (String ignore : ignoreClass) {
                        if (className.toLowerCase().contains(ignore)) {
                            classNameAutoTrack.isShouldModify = false
                            break
                        }
                    }
                }
            }
        }
        return classNameAutoTrack
    }
}
