package com.by122006.asm_smartrunpluginimp.line;

/**
 * Created by zwh on 2020/4/23.
 */

public interface LineConsumer<T> {
    void accept(T t);
}
