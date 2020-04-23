package com.by122006.asm_smartrunpluginimp.line;


/**
 * Created by admin on 2020/4/10.
 */
public interface Task<T>{
    Object apply(T data) throws Exception;
}
