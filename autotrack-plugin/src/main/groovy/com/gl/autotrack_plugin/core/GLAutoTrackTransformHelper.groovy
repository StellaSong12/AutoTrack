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
    HashSet<String> exclude = new HashSet<>(['android.support',
                                             'androidx',
                                             'com.qiyukf',
                                             'android.arch',
                                             'com.google.android',
                                             "com.umeng.message",
                                             "com.xiaomi",
                                             "com.huawei",
                                             "cn.jpush.android",
                                             "cn.jiguang",
                                             "com.meizu",
                                             "com.vivo",
                                             "com.igexin",
                                             "com.getui",
                                             "com.xiaomi",
                                             'com.tencent',
                                             "kotlin.",
                                             "kotlinx.",
                                             'com.haima.',
                                             'com.alipay',
                                             'com.mob',
                                             'com.huawei',
                                             'com.sina.weibo'])

    HashSet<String> include = new HashSet<>(['b.a.a',
                                             'butterknife.internal.DebouncingOnClickListener',
                                             'cn.sharesdk',
                                             'com.blankj.utilcode',
                                             'com.cocosw.bottomsheet',
                                             'com.effective.android',
                                             'com.netease',
                                             'com.unionpay',
                                             'com.zhy.adapter',
                                             'compfeed.feed.imagerviewer',
                                             'group.pals.android',
                                             'im.yixin.sdk',
                                             'me.kareluo.imaging',
                                             'net.simonvt.messagebar'])

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

    ClassNameAutoTrack autoTrack(String className) {
        ClassNameAutoTrack classNameAutoTrack = new ClassNameAutoTrack(className)
        if (!classNameAutoTrack.isAndroidGenerated()) {
            classNameAutoTrack.isShouldModify = true
            for (pkgName in exclude) {
                if (className.startsWith(pkgName)) {
                    classNameAutoTrack.isShouldModify = false
                    break
                }
            }
        }
        return classNameAutoTrack
    }

//    ClassNameAutoTrack autoTrack(String className) {
//        ClassNameAutoTrack classNameAutoTrack = new ClassNameAutoTrack(className)
//        if (!classNameAutoTrack.isAndroidGenerated()) {
//            for (pkgName in include) {
//                if (className.startsWith(pkgName)) {
//                    classNameAutoTrack.isShouldModify = true
//                    return classNameAutoTrack
//                }
//            }
//        }
//        return classNameAutoTrack
//    }
}
