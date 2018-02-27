package com.by122006.asm_smartrunpluginimp;

import com.by122006.asm_smartrunpluginimp.Utils.ThreadUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by admin on 2017/7/7.
 */

public class ThreadManager {
    public static ThreadManager instance = null;

    private ThreadManager() {

    }

    public static ThreadManager getInstance() {
        if (instance == null) {
            synchronized (ThreadManager.class) {
                if (instance == null) {
                    instance = new ThreadManager();
                }
            }
        }
        return instance;
    }


    public boolean check(String str) {
        if (ThreadUtils.isBGThread() && str.toLowerCase().contains("bg")) return true;
        if (ThreadUtils.isUIThread() && str.toLowerCase().contains("ui")) return true;
        if (ThreadUtils.isBGThread() && str.toLowerCase().contains("async")) return true;

        FutureTask<String> Futask = new FutureTask<String>(new Callable<String>(){
            @Override
            public String call() throws Exception {
                return null;
            }
        });
        Thread t = new Thread(Futask);


        return false;
    }

    public static void postUIThread(Runnable runnable) {
        try {
            ThreadUtils.runOnUiThread(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postBGThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    public void postUIThread(final Object obj, final String methodName, final Object... objects) {
        try {
            Class clazz = (obj instanceof Class) ? ((Class) obj) : obj.getClass();
            final Method[] methods = clazz.getMethods();
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        for (Method method : methods) {
                            if (!method.getName().equals(methodName)) continue;
                            try {
                                method.invoke(obj, objects);
                                break;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postBGThread(final Object obj, final String methodName, final Object... objects) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Class clazz = (obj instanceof Class) ? ((Class) obj) : obj.getClass();
                    Method[] methods = clazz.getMethods();
                    for (Method method : methods) {
                        if (!method.getName().equals(methodName)) continue;
                        try {
                            method.invoke(obj, objects);
                            break;
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
