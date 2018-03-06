package com.by122006.asm.Scanners;

import com.android.build.api.transform.DirectoryInput;

import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;

/**
 * Created by 122006 on 2018/2/28.
 */

public abstract class OScanner {

//    /**
//     * 设置目录
//     * @param directoryInput
//     */
//    public abstract void setDirectoryInput(File directoryInput);

    /**
     * 开始扫描
     */
    public void scan(File[] files) {
        for (File file : files) {
            String filename = file.getName();
            String name = file.getName();
            //System.out.println(name);
            //这里进行我们的处理 TODO
            if (name.endsWith(".class") && !name.startsWith("R$") &&
                    !"R.class".equals(name) && !"BuildConfig.class".equals(name) && !name.contains
                    ("R$SmartRun_")) {
                ClassReader classReader = null;
                try {
                    classReader = new ClassReader(IOGroovyMethods.getBytes(new FileInputStream(file)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
                String className = name.split(".class")[0];
                String packageClassName=classReader.getClassName();
                if (packageClassName==null)return;
                try {
                    //System.out.println("packageClassName : "+packageClassName);
                    ClassVisitor cv = defineClassVisitor(packageClassName, file, classWriter);
                    classReader.accept(cv, EXPAND_FRAMES);
                    if (needWrite()) {
                        byte[] code = classWriter.toByteArray();
                        try {
                            FileOutputStream fos = new FileOutputStream(
                                    file.getParentFile().getAbsolutePath() + File.separator + name);
//                            System.out.println("save in "+file.getParentFile().getAbsolutePath() + File.separator + name);
                            fos.write(code);
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 开始扫描
     */
    public void scan(DirectoryInput directoryInput) {
        ArrayList<File> files = new ArrayList<>();
        traverse(files, directoryInput.getFile());
        scan(files.toArray(new File[]{}));
    }

    void traverse(ArrayList<File> all, File file) {
        if (file.isDirectory() == false) {
            all.add(file);
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                traverse(all, f);
            }
        }
    }

//    /**
//     * 扫描内容
//     * @param file
//     */
//    public abstract void action(File file);


    public abstract boolean needWrite();


    /**
     * 定义需要的ClassVisitor
     *
     * @return
     */
    public abstract ClassVisitor defineClassVisitor(String packageClassName, File file, ClassWriter classWriter);

}
