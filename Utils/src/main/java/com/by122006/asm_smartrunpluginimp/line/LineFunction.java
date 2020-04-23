package com.by122006.asm_smartrunpluginimp.line;

import java.util.function.Function;

/**
 * Created by admin on 2020/4/13.
 */
public interface LineFunction<T,R> extends EndNextActions {
    R apply(T t) throws Exception;
}
