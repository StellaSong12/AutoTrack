package com.gl.autotrack_plugin.bean

import org.gradle.internal.reflect.Instantiator


class GLAutoTrackExtension {
    public boolean debug = false
    public boolean disableJar = false
    public boolean useInclude = false
    public boolean lambdaEnabled = true

    GLAutoTrackExtension(Instantiator ins) {

    }


    @Override
    String toString() {
        return "\tdebug=" + debug + "\n" +
                "\tdisableJar=" + disableJar + "\n" +
                "\tuseInclude=" + useInclude + "\n" +
                "\tlambdaEnabled=" + lambdaEnabled + "\n" +
                "\t}"
    }
}
