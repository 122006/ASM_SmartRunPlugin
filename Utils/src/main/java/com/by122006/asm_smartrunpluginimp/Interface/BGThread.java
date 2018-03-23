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
    String Async ="Async";
    String Sync ="Sync";
    String Wait ="Wait";
    String Skip ="Skip";
    String Style() default Async;

    long OutTime() default 2000L ;

    String Result() default Skip;

}