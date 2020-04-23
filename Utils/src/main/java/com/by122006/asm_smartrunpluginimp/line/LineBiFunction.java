package com.by122006.asm_smartrunpluginimp.line;

/**
 * Created by admin on 2020/4/13.
 */
public interface LineBiFunction<T, U, R> {

    R apply(T t, U u) throws Exception;
}
