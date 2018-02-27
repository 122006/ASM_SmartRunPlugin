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
public @interface ThreadStyle {
    public Style style() default Style.Default;

    public enum Style {
        UI, BG,Default
    }
}