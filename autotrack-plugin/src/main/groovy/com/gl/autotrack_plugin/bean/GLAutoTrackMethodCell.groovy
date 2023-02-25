package com.gl.autotrack_plugin.bean

import org.objectweb.asm.MethodVisitor

class GLAutoTrackMethodCell {
    /**
     * 原方法名
     */
    String name
    /**
     * 原方法描述
     */
    String desc
    /**
     * 方法所在的接口或类
     */
    String parent
    /**
     * 采集数据的方法名
     */
    String agentName
    /**
     * 采集数据的方法描述
     */
    String agentDesc
    /**
     * 采集数据的方法参数起始索引（ 0：this，1+：普通参数 ）
     */
    int paramsStart
    /**
     * 采集数据的方法参数个数
     */
    int paramsCount
    /**
     * 参数类型对应的ASM指令，加载不同类型的参数需要不同的指令
     */
    List<Integer> opcodes

    GLAutoTrackMethodCell(String name, String desc, String agentName) {
        this.name = name
        this.desc = desc
        this.agentName = agentName
    }

    GLAutoTrackMethodCell(String name, String desc, String parent, String agentName, String agentDesc, int paramsStart, int paramsCount, List<Integer> opcodes) {
        this.name = name
        this.desc = desc
        this.parent = parent
        this.agentName = agentName
        this.agentDesc = agentDesc
        this.paramsStart = paramsStart
        this.paramsCount = paramsCount
        this.opcodes = opcodes
    }

    @Override
    boolean equals(Object cell) {
        return this.name == cell.name && this.desc == cell.desc && this.parent == cell.parent
    }

    /*
     * 插入对应的原方法
     */
    void visitMethod(MethodVisitor methodVisitor, int opcode, String owner) {
        for (int i = paramsStart; i < paramsStart + paramsCount; i++) {
            methodVisitor.visitVarInsn(opcodes.get(i - paramsStart), i)
        }
        methodVisitor.visitMethodInsn(opcode, owner, name, desc, false)
    }

    /*
     插入 Hook 的方法
     */
    void visitHookMethod(MethodVisitor methodVisitor, int opcode, String owner) {
        for (int i = paramsStart; i < paramsStart + paramsCount; i++) {
            methodVisitor.visitVarInsn(opcodes.get(i - paramsStart), i)
        }
        methodVisitor.visitMethodInsn(opcode, owner, agentName, agentDesc, false)
    }
}