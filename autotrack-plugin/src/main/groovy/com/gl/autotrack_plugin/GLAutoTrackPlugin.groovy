package com.gl.autotrack_plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.ddmlib.Log
import com.gl.autotrack_plugin.bean.GLAutoTrackExtension
import com.gl.autotrack_plugin.core.GLAutoTrackTransform
import com.gl.autotrack_plugin.core.GLAutoTrackTransformHelper
import com.gl.autotrack_plugin.log.Logger
import com.gl.autotrack_plugin.util.GLAutoTrackUtil
import com.gl.autotrack_plugin.util.GitUtil
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.gradle.invocation.DefaultGradle

class GLAutoTrackPlugin implements Plugin<Project> {

    static Project rootProject

    @Override
    void apply(Project project) {
        rootProject = project.rootProject
        Map<String, ?> properties = project.getProperties()
        // 开关
        boolean disableGlAutoTrackPlugin = Boolean.parseBoolean(properties.getOrDefault("glAutoTrack.disablePlugin", String.valueOf(!GitUtil.switchOn)))
        // debug
        boolean debugPlugin = Boolean.parseBoolean(properties.getOrDefault("glAutoTrack.debugPlugin", "false"))
        // 是否开启多线程编译
        boolean disableGlAutoTrackMultiThreadBuild = Boolean.parseBoolean(properties.getOrDefault("glAutoTrack.disableMultiThreadBuild", "false"))
        // 是否开启增量编译
        boolean disableGlAutoTrackIncrementalBuild = Boolean.parseBoolean(properties.getOrDefault("glAutoTrack.disableIncrementalBuild", "false"))
        // 是否在方法进入时插入代码
        boolean isHookOnMethodEnter = Boolean.parseBoolean(properties.getOrDefault("glAutoTrack.isHookOnMethodEnter", "false"))
        // 指定asm版本
        String asmVersion = properties.getOrDefault("glAutoTrack.asmVersion", "ASM7")
        GLAutoTrackUtil.updateASMVersion(asmVersion)

        Logger.error(disableGlAutoTrackPlugin)
        Logger.error(debugPlugin)

        if (!disableGlAutoTrackPlugin) {
            BaseExtension baseExtension
            if (project.getPlugins().hasPlugin("com.android.application")) {
                baseExtension = project.extensions.findByType(AppExtension.class)
            } else if (project.getPlugins().hasPlugin("com.android.library")) {
                baseExtension = project.extensions.findByType(LibraryExtension.class)
            }

            if (null != baseExtension) {
                GLAutoTrackExtension extension = project.extensions.create("glAnalytics", GLAutoTrackExtension)
                extension.debug = debugPlugin
                GLAutoTrackTransformHelper transformHelper = new GLAutoTrackTransformHelper(extension, baseExtension)
                transformHelper.disableGlAutoTrackMultiThread = disableGlAutoTrackMultiThreadBuild
                transformHelper.disableGlAutoTrackIncremental = disableGlAutoTrackIncrementalBuild
                transformHelper.isHookOnMethodEnter = isHookOnMethodEnter

                baseExtension.registerTransform(new GLAutoTrackTransform(transformHelper, baseExtension instanceof LibraryExtension))
            } else {
                Logger.error("------------Autotrack 插件当前不支持您的项目--------------")
            }
        } else {
            Logger.info("------------Autotrack 未启动--------------")
        }
    }
}
