package com.gl.autotrack_plugin.core;

import com.gl.autotrack_plugin.bean.ClassNameAutoTrack;
import com.gl.autotrack_plugin.util.GLAutoTrackUtil;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;


public class DefaultMethodVisitor extends AdviceAdapter {
    private final ClassNameAutoTrack mClassNameAutoTrack;
    private final MethodVisitor mMethodVisitor;

    public DefaultMethodVisitor(MethodVisitor mv, int access, String name, String desc, ClassNameAutoTrack classNameAutoTrack) {
        super(GLAutoTrackUtil.ASM_VERSION, mv, access, name, desc);
        this.mClassNameAutoTrack = classNameAutoTrack;
        this.mMethodVisitor = mv;
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String fieldName, String descriptor) {
        super.visitFieldInsn(opcode, owner, fieldName, descriptor);
    }
}
