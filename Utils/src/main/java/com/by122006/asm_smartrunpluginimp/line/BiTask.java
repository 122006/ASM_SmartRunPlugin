package com.by122006.asm_smartrunpluginimp.line;

/**
 * Created by admin on 2020/4/10.
 */
public interface BiTask<T,U >{
    Object apply(T data, U ex) throws Exception;
}
