package com.by122006.asm.Scanners;

import com.by122006.asm.AnnotationData;
import com.by122006.asm.MethodInfo;
import com.by122006.asm.MyAnnotationVisitor;
import com.by122006.asm.Utils;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * Created by 122006 on 2018/2/28.
 */

public class OverAllClassVisitor extends ClassVisitor {

    /**
     * 所有修改的原方法数据
     */
    public static HashMap<String, ArrayList<MethodInfo>> methodInfos = new HashMap<>();
    /**
     * 所有类继承数据 "L"+visitName+";"
     */
    public static HashMap<String, ArrayList<String>> classExtends = new HashMap<>();
    /**
     * 所有接口
     */
    public static ArrayList<String> interfaceFilePaths = new ArrayList<>();
    /**
     * 所有普通类
     */
    public static ArrayList<String> classFilePaths = new ArrayList<>();
    String visitName;
    private String packageClassName;
    private File file;

    public OverAllClassVisitor(String packageClassName, File file, ClassVisitor classVisitor) {
        super(ASM5, classVisitor);
        this.file = file;
        this.packageClassName = packageClassName;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//        super.visit(version, access, name, signature, superName, interfaces);

        System.out.println("filename : " + name + " access : " + Integer.toBinaryString(access));

        visitName = name;
        ArrayList<String> list = new ArrayList<>();
        if (interfaces == null) {
            interfaces = new String[]{};
        }
        visitName = name;
        if (!superName.contains("java/lang/Object")) {
//            System.out.println("=========="+superName+"");
            list.add(0,"L"+ superName+";");
        }
        for(String str:interfaces){
            list.add("L"+ str+";");
        }
        classExtends.put("L"+visitName+";", list);

        if (Utils.checkAccess(access, ACC_INTERFACE)) {
            interfaceFilePaths.add(file.getPath());
            return;
        }
        classFilePaths.add(file.getPath());
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, final String desc, String signature,
                                     String[] exceptions) {
        try {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            MyAdviceAdapter av;
            av = new MyAdviceAdapter(ASM5, mv, access, name, desc, signature, exceptions);
            return av;
        } catch (Exception e) {
            System.out.println("OverAllClassVisitor visitMethod");
            e.printStackTrace();
        }
        return super.visitMethod(access, name, desc, signature, exceptions);

    }

    public class MyAdviceAdapter extends MethodVisitor {
        String annotation = "";
        int access;
        String name;
        String desc;
        String signature;
        String[] exceptions;
        MethodVisitor ov;
        boolean Flag_Static = false;
        boolean ifsave = false;
        AnnotationData annotationData;

        protected MyAdviceAdapter(int i, MethodVisitor methodVisitor, int access, String name, String desc, String
                signature,
                                  String[] exceptions) {
            super(i);
            this.name = name;
            this.desc = desc;
            this.access = access;
            this.signature = signature;
            this.exceptions = exceptions;
            Flag_Static = (access & ACC_STATIC) != 0;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String s, boolean b) {
            annotation = s == null ? "" : s;
            if (name.equals("doCycleAction")) {
                return super.visitAnnotation(s, b);
            }
            boolean g = false;

            if (!name.contains("$SmartRun_") && (annotation.toLowerCase().contains("uithread") || annotation
                    .toLowerCase().contains("bgthread"))) {
                g = true;
            }
            annotationData = new AnnotationData();
            annotationData.setOutAnnotation(annotation);
            annotationData.setReadFromAnnotation(true);
            if (g) {
                System.out.println("!!!!" + annotation);
                System.out.println(String.format("access=%d,name=%s,desc=%s,signature=%s", access, name, desc,
                        signature));
                ifsave = true;
            }
            return new MyAnnotationVisitor(ASM5, super.visitAnnotation(s, b), annotationData);

        }

        /**
         * 存档方法数据
         */
        public void save(int access, String name, String desc, String signature, String[] exceptions, AnnotationData
                annotations) {
            String className = "L" + packageClassName + ";";
            MethodInfo methodInfo = new MethodInfo(access, name, desc, signature, exceptions, annotations);
            ArrayList<MethodInfo> list = OverAllClassVisitor.methodInfos.get(className);
            if (list == null) {
                list = new ArrayList<MethodInfo>();
                OverAllClassVisitor.methodInfos.put(className, list);
            }
            list.add(methodInfo);
//            InterfaceClassVisitor.interfaceClassName.add(className);
        }

        @Override
        public void visitMethodInsn(int i, String s, String s1, String s2, boolean b) {
            if (!ifsave)
                if (!name.contains("$SmartRun_") && i == INVOKESTATIC && s.equals
                        ("com/by122006/asm_smartrunpluginimp/Utils/ThreadUtils") && (s1.equals("toUiThread") ||
                        s1.equals
                                ("toBgThread"))) {

                    if (annotationData == null) annotationData = new AnnotationData();
                    if (!annotationData.isUsed()) {
                        if (s1.equals("toBgThread"))
                            annotationData.setOutAnnotation("Lcom/by122006/asm_smartrunpluginimp/Interface/BGThread;");
                        else if (s1.equals("toUiThread"))
                            annotationData.setOutAnnotation(":com/by122006/asm_smartrunpluginimp/Interface/UIThread;");
                    }
                    annotationData.setReadFromAnnotation(false);
                }
            super.visitMethodInsn(i, s, s1, s2, b);
        }


        @Override
        public void visitEnd() {
            save(access, name, desc, signature, exceptions, annotationData);
            if (annotationData!=null){
                System.out.println(annotationData.toString());
            }

            super.visitEnd();
        }
    }

}
