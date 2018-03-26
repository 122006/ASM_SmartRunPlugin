package com.by122006.asm.Scanners;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * Created by admin on 2018/3/23.
 */

public class NClassWriter extends ClassWriter {
    public NClassWriter(int i) {
        super(i);
    }

    public NClassWriter(ClassReader classReader, int i) {
        super(classReader, i);
    }
    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        return "java/lang/Object";
    }

}
