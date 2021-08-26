package com.by122006.asm_smartrunpluginimp;

public interface RunnableThrowable<T extends Throwable> {
    public void run() throws T;
}
