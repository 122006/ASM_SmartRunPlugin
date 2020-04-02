package com.by122006.asm_smartrunpluginimp.Interface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by 122006 on 2017/3/13.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BGThread {

    /**
     * 一定会新建后台线程运行修饰方法
     */
    String Async ="Async";
    /**
     * 同为后台线程，将会直接执行修饰方法(会卡住原有后台线程)（优化用）
     */
    String Sync ="Sync";
    /**
     * 堵塞原方法，等待修饰方法的运行结果，直到运行结束或超时返回默认值
     */
    String Wait ="Wait";
    /**
     * 跳过原有方法，将立即返回默认值
     */
    String Skip ="Skip";

    /**
     * 新后台方法的运行模式 <p>一定会新建：`Async` 同后台直接执行：`Sync`
     */
    String Style() default Async;

    /**
     * Async + Wait 情况下限定超时时间 <p>
     */
    long OutTime() default 2000L ;

    /**
     * 是否等待返回 （等待：`Wait` 跳过：`Skip`）<p>
     * 只适用于`@BGThread`
     */
    String Result() default Skip;

}