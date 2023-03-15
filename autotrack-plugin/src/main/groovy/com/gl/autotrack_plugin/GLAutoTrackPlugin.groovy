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
        // 开关。默认release打包开
        boolean disableGlAutoTrackPlugin = Boolean.parseBoolean(properties.getOrDefault("glAutoTrack.disablePlugin", String.valueOf(!isSwitchOn())))
        // debug
        boolean debugPlugin = Boolean.parseBoolean(properties.getOrDefault("glAutoTrack.debugPlugin", "false"))
        // 是否开启多线程编译，默认开
        boolean disableGlAutoTrackMultiThreadBuild = Boolean.parseBoolean(properties.getOrDefault("glAutoTrack.disableMultiThreadBuild", "false"))
        // 是否开启增量编译，默认开
        boolean disableGlAutoTrackIncrementalBuild = Boolean.parseBoolean(properties.getOrDefault("glAutoTrack.disableIncrementalBuild", "false"))
        // 是否在方法进入时插入代码，默认关
        boolean isHookOnMethodEnter = Boolean.parseBoolean(properties.getOrDefault("glAutoTrack.isHookOnMethodEnter", "false"))
        // 指定asm版本
        String asmVersion = properties.getOrDefault("glAutoTrack.asmVersion", "ASM7")
        GLAutoTrackUtil.updateASMVersion(asmVersion)

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

    static boolean isSwitchOn() {
        if (!rootProject) {
            return false
        }
        List<String> taskNames = rootProject.gradle.startParameter.taskNames
        for (int index = 0; index < taskNames.size(); ++index) {
            String taskName = taskNames[index]
            Logger.info("input start parameter task is ${taskName}")
            // assembleRelease下屏蔽Prepare，这里因为还没有执行Task，没法直接通过当前的BuildType来判断，所以直接分析当前的startParameter中的taskname，
            // 另外这里有一个小坑task的名字不能是缩写必须是全称 例如assembleDebug不能是任何形式的缩写输入
            if (taskName.endsWith("Debug") && taskName.contains("Debug")) {
                return false
            }
        }
        return true
    }
}
