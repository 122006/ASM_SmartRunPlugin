package com.by122006.asm_smartrunpluginimp;

import com.by122006.asm_smartrunpluginimp.Utils.ThreadUtils;

/**
 * Created by 122006 on 2017/7/7.
 */

public class ThreadManager {

    static NewBgThreadAction newBgThreadAction;

    private ThreadManager() {

    }

    public static void postUIThread(RunnableThrowable<?> throwable) {
        try {
            ThreadUtils.runOnUiThread(() -> {
                try {
                    throwable.run();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void postBGThread(Runnable runnable) {
//        if (newBgThreadAction == null)
//            new Thread(runnable).start();
//        else newBgThreadAction.post(runnable);
//    }
    public static void postBGThread(RunnableThrowable<?> throwable) {
        Runnable runnable = () -> {
            try {
                throwable.run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        };
        if (newBgThreadAction == null)
            new Thread(runnable).start();
        else newBgThreadAction.post(runnable);
    }



    /**
     * 设置需要使用的线程池方法
     *
     */
    public static void setNewBgThreadAction(NewBgThreadAction fNewBgThreadAction) {
        newBgThreadAction = fNewBgThreadAction;
    }


    public interface NewBgThreadAction {
        void post(Runnable runnable);
    }

}
