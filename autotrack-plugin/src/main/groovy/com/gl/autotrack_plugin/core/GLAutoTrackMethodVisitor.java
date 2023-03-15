package com.gl.autotrack_plugin.core;


import com.gl.autotrack_plugin.bean.GLAutoTrackMethodCell;
import com.gl.autotrack_plugin.config.GLAutoTrackHookConfig;
import com.gl.autotrack_plugin.log.Logger;
import com.gl.autotrack_plugin.util.GLAutoTrackUtil;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GLAutoTrackMethodVisitor extends AutoTrackMethodVisitor {
    boolean isHasTracked = false;
    int variableID = 0;
    //nameDesc是'onClick(Landroid/view/View;)V'字符串
    boolean isOnClickMethod = false;
    //name + desc
    String nameDesc;
    String name, desc;
    int access;
    //访问权限是public并且非静态
    boolean pubAndNoStaticAccess;
    boolean protectedAndNotStaticAccess;
    ArrayList<Integer> localIds;
    private final GLAutoTrackTransformHelper transformHelper;
    private final MethodVisitor mMethodVisitor;
    private final String mClassName;
    private final List<String> mInterfaces;
    private final HashMap<String, GLAutoTrackMethodCell> mLambdaMethodCells;

    public GLAutoTrackMethodVisitor(MethodVisitor mv, int access, String name, String desc, GLAutoTrackTransformHelper transformHelper,
                                    String className, List<String> interfaces, HashMap<String, GLAutoTrackMethodCell> lambdaMethodCells) {
        super(mv, access, name, desc);
        this.name = name;
        this.desc = desc;
        this.access = access;
        this.nameDesc = name + desc;
        this.mMethodVisitor = mv;
        this.mClassName = className;
        this.transformHelper = transformHelper;
        this.mInterfaces = interfaces;
        this.mLambdaMethodCells = lambdaMethodCells;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        if (isHasTracked) {
            if (transformHelper.getExtension().lambdaEnabled) {
                mLambdaMethodCells.remove(nameDesc);
            }
            Logger.info("Hooked method: " + name + desc + " " + mClassName + "\n");
        }
    }

    @Override
    public void visitInvokeDynamicInsn(String name1, String desc1, Handle bsm, Object... bsmArgs) {
        super.visitInvokeDynamicInsn(name1, desc1, bsm, bsmArgs);
        if (!transformHelper.getExtension().lambdaEnabled) {
            return;
        }
        try {
            String owner = bsm.getOwner();
            if (!"java/lang/invoke/LambdaMetafactory".equals(owner)) {
                return;
            }
            String desc2 = ((Type) bsmArgs[0]).getDescriptor();
            GLAutoTrackMethodCell AutoTrackMethodCell = GLAutoTrackHookConfig.LAMBDA_METHODS.get(Type.getReturnType(desc1).getDescriptor() + name1 + desc2);
            if (AutoTrackMethodCell != null) {
                Handle it = (Handle) bsmArgs[1];
                mLambdaMethodCells.put(it.getName() + it.getDesc(), AutoTrackMethodCell);
            }
        } catch (Exception e) {
            Logger.warn("Some exception happened when call visitInvokeDynamicInsn: className: " + mClassName + ", error message: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onMethodEnter() {
        super.onMethodEnter();
        pubAndNoStaticAccess = GLAutoTrackUtil.isPublic(access) && !GLAutoTrackUtil.isStatic(access);
        protectedAndNotStaticAccess = GLAutoTrackUtil.isProtected(access) && !GLAutoTrackUtil.isStatic(access);
        if (pubAndNoStaticAccess) {
            if ((nameDesc.equals("onClick(Landroid/view/View;)V"))) {
                isOnClickMethod = true;
                variableID = newLocal(Type.getObjectType("java/lang/Integer"));
                mMethodVisitor.visitVarInsn(ALOAD, 1);
                mMethodVisitor.visitVarInsn(ASTORE, variableID);
            } else if (nameDesc.equals("onCheckedChanged(Landroid/widget/CompoundButton;Z)V")) {
                localIds = new ArrayList<>();
                int firstLocalId = newLocal(Type.getObjectType("android/widget/CompoundButton"));
                mMethodVisitor.visitVarInsn(ALOAD, 1);
                mMethodVisitor.visitVarInsn(ASTORE, firstLocalId);
                localIds.add(firstLocalId);
            } else if (nameDesc.equals("onStopTrackingTouch(Landroid/widget/SeekBar;)V")) {
                localIds = new ArrayList<>();
                int firstLocalId = newLocal(Type.getObjectType("android/widget/SeekBar"));
                mMethodVisitor.visitVarInsn(ALOAD, 1);
                mMethodVisitor.visitVarInsn(ASTORE, firstLocalId);
                localIds.add(firstLocalId);
            }
        }

        // Lambda 参数优化部分，对现有参数进行复制
        if (transformHelper.getExtension().lambdaEnabled) {
            GLAutoTrackMethodCell lambdaMethodCell = mLambdaMethodCells.get(nameDesc);
            if (lambdaMethodCell != null) {
                //判断是否是在采样中，在采样中才会处理或者开关打开也统一处理
                if (GLAutoTrackHookConfig.SAMPLING_LAMBDA_METHODS.contains(lambdaMethodCell)) {
                    Type[] types = Type.getArgumentTypes(lambdaMethodCell.getDesc());
                    int length = types.length;
                    Type[] lambdaTypes = Type.getArgumentTypes(desc);
                    // paramStart 为访问的方法参数的下标，从 0 开始
                    int paramStart = lambdaTypes.length - length;
                    if (paramStart < 0) {
                        return;
                    } else {
                        for (int i = 0; i < length; i++) {
                            if (!lambdaTypes[paramStart + i].getDescriptor().equals(types[i].getDescriptor())) {
                                return;
                            }
                        }
                    }
                    boolean isStaticMethod = GLAutoTrackUtil.isStatic(access);
                    localIds = new ArrayList<>();
                    for (int i = paramStart; i < paramStart + lambdaMethodCell.getParamsCount(); i++) {
                        int localId = newLocal(types[i - paramStart]);
                        mMethodVisitor.visitVarInsn(lambdaMethodCell.getOpcodes().get(i - paramStart), getVisitPosition(lambdaTypes, i, isStaticMethod));
                        mMethodVisitor.visitVarInsn(GLAutoTrackUtil.convertOpcodes(lambdaMethodCell.getOpcodes().get(i - paramStart)), localId);
                        localIds.add(localId);
                    }
                }
            }
        }

        if (transformHelper.isHookOnMethodEnter) {
            handleCode();
        }
    }

    @Override
    public void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        if (!transformHelper.isHookOnMethodEnter) {
            handleCode();
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String fieldName, String fieldDesc) {
        super.visitFieldInsn(opcode, owner, fieldName, fieldDesc);
    }

    void handleCode() {
        /*
         * 在 android.gradle 的 3.2.1 版本中，针对 view 的 setOnClickListener 方法 的 lambda 表达式做特殊处理。
         */
        if (transformHelper.getExtension().lambdaEnabled) {
            GLAutoTrackMethodCell lambdaMethodCell = mLambdaMethodCells.get(nameDesc);
            if (lambdaMethodCell != null) {
                Type[] types = Type.getArgumentTypes(lambdaMethodCell.getDesc());
                int length = types.length;
                Type[] lambdaTypes = Type.getArgumentTypes(desc);
                // paramStart 为访问的方法参数的下标，从 0 开始
                int paramStart = lambdaTypes.length - length;
                if (paramStart < 0) {
                    return;
                } else {
                    for (int i = 0; i < length; i++) {
                        if (!lambdaTypes[paramStart + i].getDescriptor().equals(types[i].getDescriptor())) {
                            return;
                        }
                    }
                }
                boolean isStaticMethod = GLAutoTrackUtil.isStatic(access);
                //如果在采样中，就按照最新的处理流程来操作
                if (GLAutoTrackHookConfig.SAMPLING_LAMBDA_METHODS.contains(lambdaMethodCell)) {
                    for (int i = paramStart; i < paramStart + lambdaMethodCell.getParamsCount(); i++) {
                        mMethodVisitor.visitVarInsn(lambdaMethodCell.getOpcodes().get(i - paramStart), localIds.get(i - paramStart));
                    }
                } else {
                    for (int i = paramStart; i < paramStart + lambdaMethodCell.getParamsCount(); i++) {
                        mMethodVisitor.visitVarInsn(lambdaMethodCell.getOpcodes().get(i - paramStart), getVisitPosition(lambdaTypes, i, isStaticMethod));
                    }
                }
                mMethodVisitor.visitMethodInsn(INVOKESTATIC, GLAutoTrackHookConfig.GL_AutoTrack_API, lambdaMethodCell.getAgentName(), lambdaMethodCell.getAgentDesc(), false);
                isHasTracked = true;
                return;
            }
        }

        if (!pubAndNoStaticAccess) {
            return;
        }

        if (isOnClickMethod && mClassName.equals("android/databinding/generated/callback/OnClickListener")) {
            trackViewOnClick(mMethodVisitor, 1);
            isHasTracked = true;
            return;
        }

        if (!GLAutoTrackUtil.isTargetClassInSpecial(mClassName)) {
            if ((mClassName.startsWith("android/") || mClassName.startsWith("androidx/"))) {
                return;
            }
        }

        if (mInterfaces != null && mInterfaces.size() > 0) {
            if (mInterfaces.contains("android/widget/CompoundButton$OnCheckedChangeListener") && nameDesc.equals("onCheckedChanged(Landroid/widget/CompoundButton;Z)V")) {
                GLAutoTrackMethodCell AutoTrackMethodCell = GLAutoTrackHookConfig.INTERFACE_METHODS.get("android/widget/CompoundButton$OnCheckedChangeListeneronCheckedChanged(Landroid/widget/CompoundButton;Z)V");
                if (AutoTrackMethodCell != null) {
                    mMethodVisitor.visitVarInsn(ALOAD, localIds.get(0));
                    mMethodVisitor.visitMethodInsn(INVOKESTATIC, GLAutoTrackHookConfig.GL_AutoTrack_API, AutoTrackMethodCell.getAgentName(), AutoTrackMethodCell.getAgentDesc(), false);
                    isHasTracked = true;
                    return;
                }
            } else if (mInterfaces.contains("android/widget/SeekBar$OnSeekBarChangeListener") && nameDesc.equals("onStopTrackingTouch(Landroid/widget/SeekBar;)V")) {
                GLAutoTrackMethodCell AutoTrackMethodCell = GLAutoTrackHookConfig.INTERFACE_METHODS.get("android/widget/SeekBar$OnSeekBarChangeListeneronStopTrackingTouch(Landroid/widget/SeekBar;)V");
                if (AutoTrackMethodCell != null) {
                    mMethodVisitor.visitVarInsn(ALOAD, localIds.get(0));
                    mMethodVisitor.visitMethodInsn(INVOKESTATIC, GLAutoTrackHookConfig.GL_AutoTrack_API, AutoTrackMethodCell.getAgentName(), AutoTrackMethodCell.getAgentDesc(), false);
                    isHasTracked = true;
                    return;
                }
            } else {
                for (String interfaceName : mInterfaces) {
                    GLAutoTrackMethodCell AutoTrackMethodCell = GLAutoTrackHookConfig.INTERFACE_METHODS.get(interfaceName + nameDesc);
                    if (AutoTrackMethodCell != null) {
                        AutoTrackMethodCell.visitHookMethod(mMethodVisitor, INVOKESTATIC, GLAutoTrackHookConfig.GL_AutoTrack_API);
                        isHasTracked = true;
                        return;
                    }
                }
            }
        }
        handleClassMethod(mClassName, nameDesc);
        if (isOnClickMethod) {
            trackViewOnClick(mMethodVisitor, variableID);
            isHasTracked = true;
        }
    }

    void handleClassMethod(String className, String nameDesc) {
        GLAutoTrackMethodCell AutoTrackMethodCell = GLAutoTrackHookConfig.CLASS_METHODS.get(className + nameDesc);
        if (AutoTrackMethodCell != null) {
            AutoTrackMethodCell.visitHookMethod(mMethodVisitor, INVOKESTATIC, GLAutoTrackHookConfig.GL_AutoTrack_API);
            isHasTracked = true;
        }
    }

    void trackViewOnClick(MethodVisitor mv, int index) {
        mv.visitVarInsn(ALOAD, index);
        mv.visitMethodInsn(INVOKESTATIC, GLAutoTrackHookConfig.GL_AutoTrack_API, "trackViewOnClick", "(Landroid/view/View;)V", false);
    }

    /**
     * 该方法是当扫描器扫描到类注解声明时进行调用
     *
     * @param s 注解的类型。它使用的是（“L” + “类型路径” + “;”）形式表述
     * @param b 表示的是，该注解是否在 JVM 中可见
     *          1.RetentionPolicy.SOURCE：声明注解只保留在 Java 源程序中，在编译 Java 类时注解信息不会被写入到 Class。如果使用的是这个配置 ASM 也将无法探测到这个注解。
     *          2.RetentionPolicy.CLASS：声明注解仅保留在 Class 文件中，JVM 运行时并不会处理它，这意味着 ASM 可以在 visitAnnotation 时候探测到它，但是通过Class 反射无法获取到注解信息。
     *          3.RetentionPolicy.RUNTIME：这是最常用的一种声明，ASM 可以探测到这个注解，同时 Java 反射也可以取得注解的信息。所有用到反射获取的注解都会用到这个配置，就是这个原因。
     * @return AnnotationVisitor
     */
    @Override
    public AnnotationVisitor visitAnnotation(String s, boolean b) {
        return super.visitAnnotation(s, b);
    }

    /**
     * 获取方法参数下标为 index 的对应 ASM index
     *
     * @param types          方法参数类型数组
     * @param index          方法中参数下标，从 0 开始
     * @param isStaticMethod 该方法是否为静态方法
     * @return 访问该方法的 index 位参数的 ASM index
     */
    int getVisitPosition(Type[] types, int index, boolean isStaticMethod) {
        if (types == null || index < 0 || index >= types.length) {
            throw new Error("getVisitPosition error");
        }
        if (index == 0) {
            return isStaticMethod ? 0 : 1;
        } else {
            return getVisitPosition(types, index - 1, isStaticMethod) + types[index - 1].getSize();
        }
    }
}
