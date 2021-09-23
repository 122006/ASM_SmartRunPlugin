package com.by122006.asm.Scanners;

import com.android.build.api.transform.DirectoryInput;
import com.by122006.asm.LogUtil;
import com.by122006.asm.NClassWriter;
import com.by122006.asm.Utils;

import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

            String name = file.getName();
//            LogUtil.println("## scan "+name);
            if (name.endsWith(".class") && !name.startsWith("R$") &&
                    !"R.class".equals(name) && !"BuildConfig.class".equals(name) && !name.contains
                    ("R$SmartRun_")) {
                ClassReader classReader = null;
                try {
                    classReader = new ClassReader(IOGroovyMethods.getBytes(new FileInputStream(file)));
                } catch (FileNotFoundException e) {
                    LogUtil.println("file not found :"+file);
                    continue;
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    NClassWriter classWriter = new NClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
                    String className = name.split(".class")[0];
                    String packageClassName=classReader.getClassName();
                    if (packageClassName==null)return;
                    LogUtil.println("packageClassName : "+packageClassName);
                    ClassVisitor cv = defineClassVisitor(packageClassName, file, classWriter);
                    classReader.accept(cv, EXPAND_FRAMES);
                    if (needWrite()) {
                        byte[] code = classWriter.toByteArray();
                        try {
                            FileOutputStream fos = new FileOutputStream(
                                    file.getParentFile().getPath() + File.separator + name);
                            LogUtil.println("save in "+file.getParentFile().getPath() + File.separator + name);
                            fos.write(code);
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.err.println("!!!Error "+ Utils.getExceptionDetails(e));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("!!!Error "+ Utils.getExceptionDetails(e));
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
