package com.by122006.asm_smartrunpluginimp.Utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import com.by122006.asm_smartrunpluginimp.Interface.ThreadStyle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by 122006 on 2017/2/27.
 */

public class ThreadUtils {

    public static void runOnUiThread(Runnable runnable){
//        uiThreadAct.runUITask(runnable);
        UiHandle.run(runnable);
    }
    protected static class UiHandle extends Handler {
        static UiHandle handle;
        UiHandle() {
            super(Looper.getMainLooper());
        }
        protected static UiHandle getHandler(){
            if (handle == null) {
                synchronized (UiHandle.class) {
                    if (handle == null) {
                        handle = new UiHandle();
                    }
                }
            }
            return handle;
        }
        @Override
        public void handleMessage(Message msg) {
            ((Runnable)msg.obj).run();
        }
        public static void run(Runnable runnable){
            Message message=new Message();
            message.obj=runnable;
            getHandler().sendMessage(message);
        }
    }

    public static boolean isUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();

    }

    public static boolean isBGThread() {
        return Looper.myLooper() != Looper.getMainLooper();
    }

    /**
     * 该方法用于标记调用方法运行于主线程中<p>
     * 没有实际调用意义，无论是否会调用均会生效<p>
     *
     */
    public static void toUiThread(){

    }

    /**
     * 该方法用于标记调用方法运行于后台线程中<p>
     * 没有实际调用意义，无论是否会调用均会生效<p>
     *
     */
    public static void toBgThread(){

    }

    public static ThreadStyle.Style getThreadStytle() {
        return isUIThread() ? ThreadStyle.Style.UI : ThreadStyle.Style.BG;
    }

    public static Thread getCurrentActivityThread() {
        Class<?> activityThread = null;
        try {
            activityThread = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            //获取主线程对象
            Object activityThreadObject = currentActivityThread.invoke(null);
            return (Thread) activityThreadObject;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
