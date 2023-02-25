package com.gl.autotrack_plugin.config

import com.gl.autotrack_plugin.bean.GLAutoTrackMethodCell
import org.objectweb.asm.Opcodes

class GLAutoTrackHookConfig {
    public static final String GL_AutoTrack_API = "com/gl/autotrack/GLDataAutoTrackHelper"
    public final static HashMap<String, GLAutoTrackMethodCell> INTERFACE_METHODS = new HashMap<>()
    public final static HashMap<String, GLAutoTrackMethodCell> CLASS_METHODS = new HashMap<>()

    static {
        addInterfaceMethod(new GLAutoTrackMethodCell(
                'onCheckedChanged',
                '(Landroid/widget/CompoundButton;Z)V',
                'android/widget/CompoundButton$OnCheckedChangeListener',
                'trackViewOnClick',
                '(Landroid/view/View;)V',
                1, 1,
                [Opcodes.ALOAD]))
        addInterfaceMethod(new GLAutoTrackMethodCell(
                'onRatingChanged',
                '(Landroid/widget/RatingBar;FZ)V',
                'android/widget/RatingBar$OnRatingBarChangeListener',
                'trackViewOnClick',
                '(Landroid/view/View;)V',
                1, 1,
                [Opcodes.ALOAD]))
        addInterfaceMethod(new GLAutoTrackMethodCell(
                'onStopTrackingTouch',
                '(Landroid/widget/SeekBar;)V',
                'android/widget/SeekBar$OnSeekBarChangeListener',
                'trackViewOnClick',
                '(Landroid/view/View;)V',
                1, 1,
                [Opcodes.ALOAD]))
        addInterfaceMethod(new GLAutoTrackMethodCell(
                'onTabSelected',
                '(Landroid/support/design/widget/TabLayout$Tab;)V',
                'android/support/design/widget/TabLayout$OnTabSelectedListener',
                'trackTabLayoutSelected',
                '(Ljava/lang/Object;Ljava/lang/Object;)V',
                0, 2,
                [Opcodes.ALOAD, Opcodes.ALOAD]))
        addInterfaceMethod(new GLAutoTrackMethodCell(
                'onTabSelected',
                '(Lcom/google/android/material/tabs/TabLayout$Tab;)V',
                'com/google/android/material/tabs/TabLayout$OnTabSelectedListener',
                'trackTabLayoutSelected',
                '(Ljava/lang/Object;Ljava/lang/Object;)V',
                0, 2,
                [Opcodes.ALOAD, Opcodes.ALOAD]))
    }

    static {
        addClassMethod(new GLAutoTrackMethodCell(
                'performClick',
                '()Z',
                'androidx/appcompat/widget/ActionMenuPresenter$OverflowMenuButton',
                'trackViewOnClick',
                '(Landroid/view/View;)V',
                0, 1,
                [Opcodes.ALOAD]))

        addClassMethod(new GLAutoTrackMethodCell(
                'performClick',
                '()Z',
                'android/support/v7/widget/ActionMenuPresenter$OverflowMenuButton',
                'trackViewOnClick',
                '(Landroid/view/View;)V',
                0, 1,
                [Opcodes.ALOAD]))

        addClassMethod(new GLAutoTrackMethodCell(
                'performClick',
                '()Z',
                'android/widget/ActionMenuPresenter$OverflowMenuButton',
                'trackViewOnClick',
                '(Landroid/view/View;)V',
                0, 1,
                [Opcodes.ALOAD]))
    }

    static void addInterfaceMethod(GLAutoTrackMethodCell AutoTrackMethodCell) {
        if (AutoTrackMethodCell != null) {
            INTERFACE_METHODS.put(AutoTrackMethodCell.parent + AutoTrackMethodCell.name + AutoTrackMethodCell.desc, AutoTrackMethodCell)
        }
    }

    static void addClassMethod(GLAutoTrackMethodCell AutoTrackMethodCell) {
        if (AutoTrackMethodCell != null) {
            CLASS_METHODS.put(AutoTrackMethodCell.parent + AutoTrackMethodCell.name + AutoTrackMethodCell.desc, AutoTrackMethodCell)
        }
    }

    /**
     * android.gradle 3.2.1 版本中，针对 Lambda 表达式处理
     */

    public final static HashMap<String, GLAutoTrackMethodCell> LAMBDA_METHODS = new HashMap<>()
    //lambda 参数优化取样
    public final static ArrayList<GLAutoTrackMethodCell> SAMPLING_LAMBDA_METHODS = new ArrayList<>()
    static {
        addLambdaMethod(new GLAutoTrackMethodCell(
                'onClick',
                '(Landroid/view/View;)V',
                'Landroid/view/View$OnClickListener;',
                'trackViewOnClick',
                '(Landroid/view/View;)V',
                1, 1,
                [Opcodes.ALOAD]))
        SAMPLING_LAMBDA_METHODS.add(new GLAutoTrackMethodCell(
                'onClick',
                '(Landroid/view/View;)V',
                'Landroid/view/View$OnClickListener;',
                'trackViewOnClick',
                '(Landroid/view/View;)V',
                1, 1,
                [Opcodes.ALOAD]))
        addLambdaMethod(new GLAutoTrackMethodCell(
                'onCheckedChanged',
                '(Landroid/widget/CompoundButton;Z)V',
                'Landroid/widget/CompoundButton$OnCheckedChangeListener;',
                'trackViewOnClick',
                '(Landroid/view/View;)V',
                1, 1,
                [Opcodes.ALOAD]))
        addLambdaMethod(new GLAutoTrackMethodCell(
                'onRatingChanged',
                '(Landroid/widget/RatingBar;FZ)V',
                'Landroid/widget/RatingBar$OnRatingBarChangeListener;',
                'trackViewOnClick',
                '(Landroid/view/View;)V',
                1, 1,
                [Opcodes.ALOAD]))

        // Todo: 扩展
    }

    static void addLambdaMethod(GLAutoTrackMethodCell AutoTrackMethodCell) {
        if (AutoTrackMethodCell != null) {
            LAMBDA_METHODS.put(AutoTrackMethodCell.parent + AutoTrackMethodCell.name + AutoTrackMethodCell.desc, AutoTrackMethodCell)
        }
    }
}