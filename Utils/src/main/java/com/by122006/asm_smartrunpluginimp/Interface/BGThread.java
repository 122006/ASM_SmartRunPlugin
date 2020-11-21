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
     * 不使用新定义进行逻辑定义。请参照原有定义项
     */
    long Undefined=-1;
    /**
     * 保证该方法运行于后台线程<br>
     *
     * 原线程为UI线程时: 创建一个新的后台线程以执行该方法。该方法返回值会被忽略<br>
     *
     * 原线程为BG线程时: 直接执行该方法。因此返回值一定生效
     */
    long Assert=-2;

    /**
     * 无论原线程为UI或者BG线程，均会创建一个新的后台线程以执行该方法
     * 。<br>该方法返回值会被忽略
     */
    long NewTask=0;

    /**
     * 原线程为UI线程时: 创建一个新的后台线程以执行该方法。该方法返回值会被忽略<br>
     * 原线程为BG线程时: 创建一个新的后台线程以执行该方法，原线程等待(OutTime)毫秒，直到该方法返回或者超时返回默认值 <br>
     *     超时时间：由OutTime方法指定,你也可以指定该value>0为毫秒数，两者取最大
     */
    long WaitResult=1;
    public long value() default Undefined;

    /**
     * 一定会新建后台线程运行修饰方法
     */
    @Deprecated
    String Async ="Async";
    /**
     * 同步标识
     * 同为后台线程，将会直接执行修饰方法(会卡住原有后台线程)（优化用）
     */
    @Deprecated
    String Sync ="Sync";
    /**
     * 堵塞原方法，等待修饰方法的运行结果，直到运行结束或超时返回默认值
     */
    @Deprecated
    String Wait ="Wait";
    /**
     * 跳过原有方法，将立即返回默认值
     */
    @Deprecated
    String Skip ="Skip";

    /**
     * 新后台方法的运行模式 <p>一定会新建：`Async` 同后台直接执行：`Sync`
     */
    @Deprecated
    String Style() default Async;

    /**
     * 限定超时时间 <p>
     */
    long OutTime() default 2000L ;

    /**
     * 是否等待返回 （等待：`Wait` 跳过：`Skip`[默认]）<p>
     * 只适用于`@BGThread` 的Async模式
     */
    @Deprecated
    String Result() default Skip;

}