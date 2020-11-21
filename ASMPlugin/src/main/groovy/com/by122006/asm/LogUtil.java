package com.by122006.asm;

public class LogUtil {
    public static boolean outputLog=false;

    public static void println(Object x){
        if (!outputLog) return;
        System.out.println(x);
    }
    public static void print(Object x){
        if (!outputLog) return;
        System.out.print(x);
    }
}
