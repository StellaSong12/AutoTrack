package com.gl.autotrack_plugin.core

import com.gl.autotrack_plugin.bean.ClassNameAutoTrack
import com.gl.autotrack_plugin.bean.GLAutoTrackMethodCell
import com.gl.autotrack_plugin.util.ModuleUtils
import com.gl.autotrack_plugin.log.Logger
import com.gl.autotrack_plugin.util.GLAutoTrackUtil;
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class GLAutoTrackClassVisitor extends ClassVisitor {
    private String mClassName
    private List<String> mInterfaces
    private ClassVisitor classVisitor
    private GLAutoTrackTransformHelper transformHelper
    private ClassNameAutoTrack classNameAutoTrack
    private HashMap<String, GLAutoTrackMethodCell> mLambdaMethodCells = new HashMap<>()
    private int version

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone()
    }

    GLAutoTrackClassVisitor(final ClassVisitor classVisitor, ClassNameAutoTrack classNameAutoTrack, GLAutoTrackTransformHelper transformHelper) {
        super(GLAutoTrackUtil.ASM_VERSION, classVisitor)
        this.classVisitor = classVisitor
        this.classNameAutoTrack = classNameAutoTrack
        this.transformHelper = transformHelper
    }

    /**
     * 该方法是当扫描类时第一个拜访的方法，主要用于类声明使用
     * @param version 表示类版本：51，表示 “.class” 文件的版本是 JDK 1.7
     * @param access 类的修饰符：修饰符在 ASM 中是以 “ACC_” 开头的常量进行定义。
     *                          可以作用到类级别上的修饰符有：ACC_PUBLIC（public）、ACC_PRIVATE（private）、ACC_PROTECTED（protected）、
     *                          ACC_FINAL（final）、ACC_SUPER（extends）、ACC_INTERFACE（接口）、ACC_ABSTRACT（抽象类）、
     *                          ACC_ANNOTATION（注解类型）、ACC_ENUM（枚举类型）、ACC_DEPRECATED（标记了@Deprecated注解的类）、ACC_SYNTHETIC
     * @param name 类的名称：通常我们的类完整类名使用 “org.test.mypackage.MyClass” 来表示，但是到了字节码中会以路径形式表示它们 “org/test/mypackage/MyClass” 。
     *                      值得注意的是虽然是路径表示法但是不需要写明类的 “.class” 扩展名。
     * @param signature 表示泛型信息，如果类并未定义任何泛型该参数为空
     * @param superName 表示所继承的父类：由于 Java 的类是单根结构，即所有类都继承自 java.lang.Object。 因此可以简单的理解为任何类都会具有一个父类。
     *                  虽然在编写 Java 程序时我们没有去写 extends 关键字去明确继承的父类，但是 JDK在编译时 总会为我们加上 “ extends Object”。
     * @param interfaces 表示类实现的接口，在 Java 中类是可以实现多个不同的接口因此此处是一个数组。
     */
    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        mClassName = name
        mInterfaces = Arrays.asList(interfaces)
        this.version = version
        super.visit(version, access, name, signature, superName, interfaces)
        if (Logger.debug) {
            Logger.info("开始扫描类：${mClassName}")
            Logger.info("类详情：version=${version};\taccess=${Logger.accCode2String(access)};\tname=${name};\tsignature=${signature};\tsuperName=${superName};\tinterfaces=${interfaces.toArrayString()}\n")
        }
    }

    /**
     * 该方法是当扫描器完成类扫描时才会调用，如果想在类中追加某些方法，可以在该方法中实现。
     */
    @Override
    void visitEnd() {
        super.visitEnd()
        if (Logger.debug) {
            Logger.info("结束扫描类：${mClassName}\n")
        }
    }

    /**
     * 该方法是当扫描器扫描到类的方法时进行调用
     * @param access 表示方法的修饰符
     * @param name 表示方法名，在 ASM 中 “visitMethod” 方法会处理（构造方法、静态代码块、私有方法、受保护的方法、共有方法、native类型方法）。
     *                  在这些范畴中构造方法的方法名为 “<init>”，静态代码块的方法名为 “<clinit>”。
     * @param desc 表示方法签名，方法签名的格式如下：“(参数列表)返回值类型”
     * @param signature 凡是具有泛型信息的方法，该参数都会有值。并且该值的内容信息基本等于第三个参数的拷贝，只不过不同的是泛型参数被特殊标记出来
     * @param exceptions 用来表示将会抛出的异常，如果方法不会抛出异常，则该参数为空
     * @return
     */
    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)

        if (ModuleUtils.isAutoTrackInstall) {
            methodVisitor = new GLAutoTrackMethodVisitor(methodVisitor, access, name, desc,
                    transformHelper, mClassName, mInterfaces, mLambdaMethodCells)
        }

        methodVisitor = new DefaultMethodVisitor(methodVisitor, access, name, desc, classNameAutoTrack)

        return methodVisitor
    }
}