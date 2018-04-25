package com.by122006.asm.Scanners;


import com.by122006.asm.AnnotationData;
import com.by122006.asm.InnerClass;
import com.by122006.asm.MethodInfo;
import com.by122006.asm.Utils;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.File;
import java.util.ArrayList;

import javax.annotation.Nullable;

import static com.by122006.asm.Utils.checkReturnStyle;
import static org.objectweb.asm.Opcodes.ASM5;

public class CommomClassVisitor extends ClassVisitor {
    private String visitName;
    private int access;
    File file;
    int index = 0;
    ArrayList<String> interfaces = new ArrayList<String>();
    ArrayList<String> methodNames = new ArrayList<>();
    private String packageClassName;


    public CommomClassVisitor(String packageClassName, File file, ClassVisitor classVisitor) {
        super(ASM5, classVisitor);
        this.file = file;
        this.packageClassName = packageClassName;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        visitName = name;
        this.access=access;
//        if (interfaces != null && interfaces.length > 0);
//            for (String str : interfaces) {
//                if(OverAllClassVisitor.interfaceClassName.indexOf(str)>=0){
//
//                }
//            }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, final String desc, String signature,
                                     String[] exceptions) {
        boolean change = false;
        AnnotationData annotationData = null;
        if (methodNames.contains(name + desc)) return null;
        if (name.equals("doCycleAction") || name.contains("$SmartRun_")) change = false;
        else {
            annotationData = getUsedAnnotationData(name, desc, null);
//            if (annotationData == null) {
//                System.out.println(String.format("%s.%s 方法 无签名数据 ", visitName, name));
//            }
            if (annotationData != null && annotationData.isUsed()) {
                change = true;
            }
        }
        methodNames.add(name + desc);
//        System.out.println(String.format("%s.%s 方法%s ", visitName, name, change + ""));
        if (change) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            System.out.println(String.format("%s.%s 方法%s ", visitName, name, annotationData.toString()));
            MyAdviceAdapter av = null;
            av = new MyAdviceAdapter(ASM5, mv, access, name, desc, signature, exceptions, annotationData);
            mv = av;
            return mv;
        } else
            return super.visitMethod(access, name, desc, signature, exceptions);

    }

    @Override
    public void visitEnd() {
//        for(NeedCreateMethod needCreateMethod:needCreateMethods){
//            needCreateMethod.createMethod();
//        }
        super.visitEnd();
    }

    public CommomClassVisitor setPackageClassName(String packageClassName) {
        this.packageClassName = packageClassName;
        return this;
    }

    /**
     * 获得当前方法的可用信息
     *
     * @param visitParentClassName
     * @return
     */
    @Nullable
    private AnnotationData getUsedAnnotationData(String methodName, String methodDesc, String visitParentClassName) {
        ArrayList<String> checkList = new ArrayList<>();
        ArrayList list;
        if (visitParentClassName == null) {
            checkList.add("L" + visitName + ";");
            list = OverAllClassVisitor.classExtends.get("L" + visitName + ";");
        } else {
            list = OverAllClassVisitor.classExtends.get(visitParentClassName);
        }
        if (list != null) checkList.addAll(list);
        for (int i = 0; i < checkList.size(); i++) {
            String otherName = checkList.get(i);
//            if (i>0||visitParentClassName!=null)System.out.println("visitParentClassName "+otherName);
            if (OverAllClassVisitor.methodInfos.get(otherName) != null)
                for (MethodInfo methodInfo : OverAllClassVisitor.methodInfos.get(otherName)) {
                    if (methodInfo.name.equals(methodName) && methodInfo.desc.equals(methodDesc)) {
                        if (methodInfo.annotations != null && methodInfo.annotations.isUsed()) {
                            System.out.println(String.format("use %s.%s()", otherName, methodInfo.name));
                            return methodInfo.annotations;
                        }
                        break;
                    }
                }
            if (visitParentClassName == null && i > 0) return getUsedAnnotationData(methodName, methodDesc, otherName);
        }
        return null;


    }

    public class MyAdviceAdapter extends AdviceAdapter {
        int access;
        String name;
        String desc;
        String signature;
        String[] exceptions;
        MethodVisitor ov;
        boolean Flag_Static = false;
        AnnotationData annotationData;
        //        /**
//         * 存档方法数据
//         */
//        public void save(int access, String name, String desc, String signature, String[] exceptions, String
//                annotation) {
//            String className = "L" + packageClassName + ";";
//            MethodInfo methodInfo = new MethodInfo(access, name, desc, signature, exceptions, annotation);
//            ArrayList<MethodInfo> list = OverAllClassVisitor.methodInfos.get(className);
//            if (list == null) {
//                list = new ArrayList<MethodInfo>();
//                OverAllClassVisitor.methodInfos.put(className, list);
//            }
//            list.add(methodInfo);
//        }
        boolean isAnnotationAble = false;


        protected MyAdviceAdapter(int i, MethodVisitor methodVisitor, int access, String name, String desc, String
                signature,
                                  String[] exceptions, AnnotationData annotationData) {
            super(i, methodVisitor, access, name, desc);
            this.name = name;
            this.desc = desc;
            this.access = access;
            this.signature = signature;
            this.exceptions = exceptions;
            this.annotationData = annotationData;
            Flag_Static = (access & ACC_STATIC) != 0;
            if (!name.contains("$SmartRun_")) {
                String style = annotationData.getOutAnnotation().toLowerCase().contains("uithread") ? "UI" : "BG";
                //重命名并开始注入
                ov = mv;

                if (Utils.checkAccess(CommomClassVisitor.this.access, ACC_INTERFACE)) {
                    System.out.println("access: ACC_INTERFACE");

                } else
                    System.out.println("access: " + Integer.toBinaryString(access) + "  -> " + Integer.toBinaryString
                            (access=(access | ACC_PUBLIC) & ~ACC_PRIVATE & ~ACC_PROTECTED));

                
                mv = cv.visitMethod(access, name + "$SmartRun_" +
                        style, desc, signature, exceptions);
            }
        }


        @Override
        public void visitCode() {
            super.visitCode();
        }

//        @Override
//        public void visitMethodInsn(int i, String s, String s1, String s2, boolean b) {
//            if (!name.contains("$SmartRun_") && i == INVOKESTATIC && s.equals
//                    ("com/by122006/asm_smartrunpluginimp/Utils/ThreadUtils") && (s1.equals("toUiThread") || s1.equals
//                    ("toBgThread")) && !annotation.toLowerCase().contains("thread")) {
//                System.out.println("!!!!method code : " + s1);
//                System.out.println(String.format("access=%d,name=%s,desc=%s,signature=%s", access, name, desc,
//                        signature));
//
//                String style = s1.toLowerCase().contains("ui") ? "UI" : "BG";
//                ov = mv;
//                mv = cv.visitMethod(access, name + "$SmartRun_" + style, desc, signature, exceptions);
//            }
//            super.visitMethodInsn(i, s, s1, s2, b);
//
//
//        }

        @Override
        public void visitEnd() {
            if (ov != null) {
                String style = annotationData.getOutAnnotation().toLowerCase().contains("uithread") ? "UI" : "BG";

                String arg = desc.substring(1, desc.lastIndexOf(")"));
                if (arg.endsWith(";")) arg = arg.substring(0, arg.length());
                String[] args = arg.split(";");
                if (args.length == 1 && args[0].length() == 0) args = new String[]{};
                for (int i = 0; i < args.length; i++) {
                    args[i] += args[i].length() == 1 ? "" : ";";

                }

                int num = args.length;
                System.out.println(packageClassName);
                System.out.println("Flag_Static:" + Flag_Static);
                ov.visitCode();

                Label l1 = new Label();
                if (!annotationData.isNewThread()) {
                    ov.visitMethodInsn(INVOKESTATIC, "com/by122006/asm_smartrunpluginimp/Utils/ThreadUtils", "is" +
                            style + "Thread", "()Z", false);

                    Label l0 = new Label();
                    ov.visitJumpInsn(IFEQ, l0);
                    if (!Flag_Static)
                        ov.visitVarInsn(ALOAD, 0);
                    if (num > 0) {
                        for (int i = 0; i < num; i++) {
                            int pa = ALOAD;
                            if (args[i].length() > 1) {
                                pa = ALOAD;
                            } else {
                                if (args[i].equals("Z")) pa = ILOAD;
                                else if (args[i].equals("B")) pa = ILOAD;
                                else if (args[i].equals("C")) pa = ILOAD;
                                else if (args[i].equals("S")) pa = ILOAD;
                                else if (args[i].equals("I")) pa = ILOAD;
                                else if (args[i].equals("J")) pa = LLOAD;
                                else if (args[i].equals("F")) pa = FLOAD;
                                else if (args[i].equals("D")) pa = DLOAD;
                            }
                            ov.visitVarInsn(pa, Flag_Static ? i : i + 1);
                        }
                    }
                    if (Flag_Static) {
                        ov.visitMethodInsn(INVOKESTATIC, packageClassName, name + "$SmartRun_" + style, desc, false);
                    } else
                        ov.visitMethodInsn(INVOKEVIRTUAL, packageClassName, name + "$SmartRun_" + style, desc, false);
                    ov.visitJumpInsn(GOTO, l1);
                    ov.visitLabel(l0);
                    ov.visitFrame(F_SAME, 0, null, 0, null);
                }
                String newClassName = packageClassName.substring(0, packageClassName.lastIndexOf("/") + 1) +
                        InnerClass.create(file, packageClassName, desc, style, Flag_Static, "L" + packageClassName +
                                        ";",
                                name + "$SmartRun_" + style, annotationData.isNewThread(), annotationData.getOutTime
                                        (), annotationData.isResult());
                visitInnerClass(newClassName, null, null, 0);
                System.out.println("newClassName:" + newClassName);

                ov.visitTypeInsn(NEW, newClassName);
                ov.visitInsn(DUP);
                ov.visitMethodInsn(INVOKESPECIAL, newClassName, "<init>", "()V", false);

                if (!Flag_Static) {
                    ov.visitVarInsn(ALOAD, 0);
                } else {
                    ov.visitInsn(ACONST_NULL);
                }
                if (num > 0) {
                    for (int i = 0; i < num; i++) {
                        int pa = ALOAD;
                        System.out.println(args[i]);
                        if (args[i].length() > 1) {
                            pa = ALOAD;
                        } else {
                            if (args[i].equals("Z")) pa = ILOAD;
                            else if (args[i].equals("B")) pa = ILOAD;
                            else if (args[i].equals("C")) pa = ILOAD;
                            else if (args[i].equals("S")) pa = ILOAD;
                            else if (args[i].equals("I")) pa = ILOAD;
                            else if (args[i].equals("J")) pa = LLOAD;
                            else if (args[i].equals("F")) pa = FLOAD;
                            else if (args[i].equals("D")) pa = DLOAD;
                        }
                        ov.visitVarInsn(pa, Flag_Static ? i : i + 1);
                    }
                }
                ov.visitMethodInsn(INVOKEVIRTUAL, newClassName, "action", "(" + "L" + packageClassName + ";" + desc
                        .substring(1), false);

                if (!annotationData.isNewThread()) {
                    ov.visitLabel(l1);
                    ov.visitFrame(F_SAME, 0, null, 0, null);
                }

                String returnStyle = desc.substring(desc.lastIndexOf(")") + 1);
                if (returnStyle.equals("V")) {
                    ov.visitInsn(RETURN);
                } else {
                    if (checkReturnStyle(returnStyle, "Z")) ov.visitInsn(IRETURN);
                    else if (checkReturnStyle(returnStyle, "C", "S", "I", "B"))
                        ov.visitInsn(IRETURN);
                    else if (checkReturnStyle(returnStyle, "J"))
                        ov.visitInsn(LRETURN);
                    else if (checkReturnStyle(returnStyle, "F"))
                        ov.visitInsn(FRETURN);
                    else if (checkReturnStyle(returnStyle, "D"))
                        ov.visitInsn(DRETURN);
                    else ov.visitInsn(ARETURN);
                }

                ov.visitMaxs(20 + num, 20 + num);
                ov.visitEnd();
                return;
            }
            super.visitEnd();
        }

    }


}