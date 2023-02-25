package com.gl.autotrack_plugin.bean

import com.gl.autotrack_plugin.util.GitUtil;
import org.gradle.internal.reflect.Instantiator


class GLAutoTrackExtension {
    public boolean debug = false
    public boolean disableJar = false
    public boolean useInclude = false
    public boolean lambdaEnabled = true
    public ArrayList<String> exclude = []
    public ArrayList<String> include = []

    GLAutoTrackExtension(Instantiator ins) {

    }


    @Override
    String toString() {
        StringBuilder excludeBuilder = new StringBuilder()
        int length = exclude.size()
        for (int i = 0; i < length; i++) {
            excludeBuilder.append("'").append(exclude.get(i)).append("'")
            if (i != length - 1) {
                excludeBuilder.append(",")
            }
        }

        StringBuilder includeBuilder = new StringBuilder()
        length = include.size()
        for (int i = 0; i < length; i++) {
            includeBuilder.append("'").append(include.get(i)).append("'")
            if (i != length - 1) {
                includeBuilder.append(",")
            }
        }
        return "\tdebug=" + debug + "\n" +
                "\tdisableJar=" + disableJar + "\n" +
                "\tuseInclude=" + useInclude + "\n" +
                "\tlambdaEnabled=" + lambdaEnabled + "\n" +
                "\texclude=[" + excludeBuilder.toString() + "]" + "\n" +
                "\tinclude=[" + includeBuilder.toString() + "]" + "\n" +
                "\t}"
    }
}

