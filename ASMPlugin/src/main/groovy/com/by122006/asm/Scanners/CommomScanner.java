package com.by122006.asm.Scanners;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;

/**
 * Created by 122006 on 2018/2/28.
 */

public class CommomScanner extends OScanner {

    @Override
    public boolean needWrite() {
        return true;
    }

    @Override
    public ClassVisitor defineClassVisitor(String packageClassName,File file, ClassWriter classWriter) {
        return new CommomClassVisitor(packageClassName,file,classWriter);
    }

    /**
     * 开始扫描
     */
    public void scan() {
        File[] files=new File[OverAllClassVisitor.classFilePaths.size()];
        for (int i=0;i<OverAllClassVisitor.classFilePaths.size();i++){
            files[i]=new File(OverAllClassVisitor.classFilePaths.get(i));
        }
        scan(files);
    }

    public static void init() {
        CommomClassVisitor.init();
    }
}
