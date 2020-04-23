package com.by122006.asm_smartrunpluginimp.line;

/**
 * Created by zwh on 2020/4/23.
 */

public interface EndNextActions {
    default void stopNextActions() throws EndException {
        throw new EndException(null);
    }
}
