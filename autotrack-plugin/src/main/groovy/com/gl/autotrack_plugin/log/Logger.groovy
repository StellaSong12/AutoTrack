package com.gl.autotrack_plugin.log;

import org.objectweb.asm.Opcodes

import java.lang.reflect.Array
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

class Logger {
    private static final String TAG = "[GLAutoTrack]: "
    private static boolean debug = false
    public static ConcurrentHashMap<Integer, String> accCodeMap = new ConcurrentHashMap<>()
    public static ConcurrentHashMap<Integer, String> opCodeMap = new ConcurrentHashMap<>()

    static void printPluginConfig(boolean disableGlAutoTrackMultiThread, boolean disableGlAutoTrackIncremental,
                                  boolean isIncremental, boolean isHookOnMethodEnter) {
        println("${TAG}正在执行...")
        println("${TAG}是否开启多线程编译:${!disableGlAutoTrackMultiThread}")
        println("${TAG}是否开启增量编译:${!disableGlAutoTrackIncremental}")
        println("${TAG}此次是否增量编译:$isIncremental")
        println("${TAG}是否在方法进入时插入代码:$isHookOnMethodEnter")
    }

    /**
     * 设置是否打印日志
     */
    static void setDebug(boolean isDebug) {
        debug = isDebug
    }

    static boolean isDebug() {
        return debug
    }

    def static error(Object msg) {
        try {
            println("${LogUI.C_ERROR.value}${TAG}${msg}${LogUI.E_NORMAL.value}")
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    def static warn(Object msg) {
        try {
            println("${LogUI.C_WARN.value}${TAG}${msg}${LogUI.E_NORMAL.value}")
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    /**
     * 打印日志
     */
    def static info(Object msg) {
        if (debug)
            try {
                println "${TAG}${msg}"
            } catch (Exception e) {
                e.printStackTrace()
            }
    }

    def static logForEach(Object... msg) {
        if (!debug) {
            return
        }
        msg.each {
            Object m ->
                try {
                    if (m != null) {
                        if (m.class.isArray()) {
                            print "["
                            def length = Array.getLength(m);
                            if (length > 0) {
                                for (int i = 0; i < length; i++) {
                                    def get = Array.get(m, i);
                                    if (get != null) {
                                        print "${get}\t"
                                    } else {
                                        print "null\t"
                                    }
                                }
                            }
                            print "]\t"
                        } else {
                            print "${m}\t"
                        }
                    } else {
                        print "null\t"
                    }
                } catch (Exception e) {
                    e.printStackTrace()
                }
        }
        println ""
    }

    static String accCode2String(int access) {
        def builder = new StringBuilder()
        def map = getAccCodeMap()
        map.each { key, value ->
            if ((key.intValue() & access) > 0) {
                builder.append(value + ' ')
            }
        }
        return builder.toString()
    }

    private static Map<Integer, String> getAccCodeMap() {
        if (accCodeMap.isEmpty()) {
            Field[] fields = Opcodes.class.getDeclaredFields()
            HashMap<Integer, String> tmpMap = [:]
            fields.each {
                if (it.name.startsWith("ACC_")) {
                    if (it.type == Integer.class) {
                        tmpMap[it.get(null) as Integer] = it.name
                    } else {
                        tmpMap[it.getInt(null)] = it.name
                    }
                }
            }
            accCodeMap.putAll(tmpMap)
        }
        return accCodeMap
    }

    static Map<Integer, String> getOpMap() {
        if (opCodeMap.size() == 0) {
            HashMap<String, Integer> map = [:]
            Field[] fields = Opcodes.class.getDeclaredFields()
            fields.each {
                if (it.type == Integer.class) {
                    map[it.get(null) as Integer] = it.name
                } else {
                    map[it.getInt(null)] = it.name
                }
            }
            opCodeMap.putAll(map)
        }
        return opCodeMap
    }
}