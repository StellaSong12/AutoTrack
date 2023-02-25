package com.gl.autotrack_plugin.util

import com.gl.autotrack_plugin.GLAutoTrackPlugin
import com.gl.autotrack_plugin.log.Logger

class GitUtil {

    static boolean isSwitchOn() {
        if (isGitExist()) {
            return getGitBranch().matches("(^develop\$)|(^release/(.)*)")
        } else {
            return true
        }
    }

    static boolean isGitExist() {
        return !shell("git --help").contains("git command not found")
    }

    static String getGitBranch() {
        return shell("git symbolic-ref --short -q HEAD")
    }

    static String shell(String cmd) {
        try {
            def out = new ByteArrayOutputStream()
            GLAutoTrackPlugin.rootProject.exec {
                executable 'bash'
                args '-c', cmd
                standardOutput = out
            }
            return out.toString().trim()
        } catch (Exception e) {
            Logger.error("bash error: ${e.getMessage()}")
            return cmd.execute()
        }
    }
}