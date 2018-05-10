package com.by122006.asm.Scanners;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 122006 on 2018/2/28.
 */

public class OverAllScanner extends OScanner {



    @Override
    public boolean needWrite() {
        return false;
    }

    @Override
    public ClassVisitor defineClassVisitor(String packageClassName,File file, ClassWriter classWriter) {
        return new OverAllClassVisitor(packageClassName,file,classWriter);
    }
    public static void init() {
        OverAllClassVisitor.init();
    }

}
