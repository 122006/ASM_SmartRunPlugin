package com.by122006.asm_smartrunpluginimp;

import com.by122006.asm_smartrunpluginimp.Utils.ThreadUtils;

/**
 * Created by 122006 on 2017/7/7.
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


}
