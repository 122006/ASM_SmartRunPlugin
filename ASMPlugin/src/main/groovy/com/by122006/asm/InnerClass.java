package com.by122006.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static com.by122006.asm.Utils.checkReturnStyle;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DCONST_0;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DRETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.FCONST_0;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FRETURN;
import static org.objectweb.asm.Opcodes.F_SAME;
import static org.objectweb.asm.Opcodes.F_SAME1;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LRETURN;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_7;

/**
 * Created by 122006 on 2018/2/28.
 */

public class InnerClass {

    /**
     * @param oPackageClassName
     * @param desc
     * @param threadStyle
     * @param isStatic
     * @param oObjType
     * @param doMethodName
     * @param newThread
     * @param outTime
     * @param result            强制返回
     * @return
     */
    public static String create(File file, String oPackageClassName, String desc, String threadStyle, boolean isStatic,
                                String oObjType, String doMethodName, boolean newThread, long outTime, boolean
                                        result) {
        int index=Utils.getClassHaveIndexInDir(file);
        index++;
        System.out.println(file.getAbsolutePath());
        String allClassPath = file.getAbsolutePath().replace(".class", "") + "$" + doMethodName.replace("$",
                "_") + "_" + index + ".class";
        String className = allClassPath.substring(allClassPath.lastIndexOf("\\") + 1, allClassPath.lastIndexOf("."));
        String returnStyle = desc.substring(desc.lastIndexOf(")") + 1);
        boolean needReturn = !returnStyle.equals("V");
        String oObjClassName = oObjType.substring(1, oObjType.length() - 1);
        String packageClassName = oPackageClassName.substring(0, oPackageClassName.lastIndexOf("/") + 1) + className;

        System.out.println("returnStyle : " + returnStyle);
        //System.out.println("packageClassName : " + packageClassName);
        String arg = desc.substring(1, desc.lastIndexOf(")"));
        if (arg.endsWith(";")) arg = arg.substring(0, arg.length());
        String[] args = arg.split(";");
        ArrayList<String> ls= new ArrayList<>();
        Collections.addAll(ls,args);
        ArrayList<String> ls2=new ArrayList<>();
        for(String s :ls){
            if(s.startsWith("L")&&s.length()>1){
                ls2.add(s);
            }else{
                ls2.add(String.valueOf(s.charAt(0)));
                ls2.add(s.substring(1));
            }
        }
        if (args.length == 1 && args[0].length() == 0) args = new String[]{};
        for (int i = 0; i < args.length; i++) {
            args[i] += args[i].length() == 1 ? "" : ";";
            System.out.println(i + " : " + args[i]);
        }
        System.out.println("args.length : " + args.length);

        NClassWriter cw = new NClassWriter(ClassWriter.COMPUTE_FRAMES);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(V1_7, ACC_PROTECTED + ACC_SUPER, packageClassName, null, "java/lang/Object", new
                String[]{"java/lang/Runnable"});

        cw.visitOuterClass(oPackageClassName, doMethodName, desc);

        if (result) {
            fv = cw.visitField(ACC_PRIVATE, "countDownLatch", "Ljava/util/concurrent/CountDownLatch;", null, null);
            fv.visitEnd();
        }
        if (needReturn) {
            fv = cw.visitField(ACC_PRIVATE, "result", returnStyle, null, null);
            fv.visitEnd();
        }

        fv = cw.visitField(ACC_PRIVATE, "obj", oObjType, null, null);
        fv.visitEnd();

        for (int i = 0; i < args.length; i++) {
            fv = cw.visitField(ACC_PRIVATE, "o" + i, args[i], null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
            mv.visitCode();
            if (needReturn) {
                mv.visitVarInsn(ALOAD, 0);
            }
            if (isStatic) {
                for (int i = 0; i < args.length; i++) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, packageClassName, "o" + i, args[i]);
                }
                mv.visitMethodInsn(INVOKESTATIC, oObjClassName, doMethodName, desc, false);
            } else {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, packageClassName, "obj", oObjType);
                for (int i = 0; i < args.length; i++) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, packageClassName, "o" + i, args[i]);
                }
                mv.visitMethodInsn(INVOKEVIRTUAL, oObjClassName, doMethodName, desc, false);
            }
            if (needReturn) {
                mv.visitFieldInsn(PUTFIELD, packageClassName, "result", returnStyle);
            }
            if (result) {
                Label l0 = new Label();
                if (threadStyle.equals("BG")) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, packageClassName, "countDownLatch",
                            "Ljava/util/concurrent/CountDownLatch;");
                    mv.visitJumpInsn(IFNULL, l0);
                }
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, packageClassName, "countDownLatch",
                        "Ljava/util/concurrent/CountDownLatch;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/CountDownLatch", "countDown", "()V", false);

                mv.visitLabel(l0);
            }
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "action", "(" + oObjType + desc.substring(1), null, null);
            mv.visitCode();

            //设置对象
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, packageClassName, "obj", oObjType);

            for (int i = 0; i < args.length; i++) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, i + 2);
                mv.visitFieldInsn(PUTFIELD, packageClassName, "o" + i, args[i]);
            }

            if (result) {
                Label l0 = new Label();
                if (threadStyle.equals("BG")) {
                    mv.visitMethodInsn(INVOKESTATIC, "com/by122006/asm_smartrunpluginimp/Utils/ThreadUtils",
                            "isBGThread", "()Z", false);
                    mv.visitJumpInsn(IFEQ, l0);
                }
                mv.visitVarInsn(ALOAD, 0);
                mv.visitTypeInsn(NEW, "java/util/concurrent/CountDownLatch");
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_1);
                mv.visitMethodInsn(INVOKESPECIAL, "java/util/concurrent/CountDownLatch", "<init>", "(I)V", false);
                mv.visitFieldInsn(PUTFIELD, packageClassName, "countDownLatch",
                        "Ljava/util/concurrent/CountDownLatch;");
                if (threadStyle.equals("BG")) {
                    mv.visitLabel(l0);
                    mv.visitFrame(F_SAME, 0, null, 0, null);
                }
            }
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "com/by122006/asm_smartrunpluginimp/ThreadManager", "post" + threadStyle
                    .toUpperCase() + "Thread", "(Ljava/lang/Runnable;)V", false);

            if (result) {
                Label start = new Label();
                Label end = new Label();
                Label throw0 = new Label();
                Label l2 = new Label();

                Label l3 = new Label();
                if (threadStyle.equals("BG")) {
                    mv.visitMethodInsn(INVOKESTATIC, "com/by122006/asm_smartrunpluginimp/Utils/ThreadUtils",
                            "isBGThread", "()Z", false);
                    mv.visitJumpInsn(IFEQ, l3);
                }

                mv.visitTryCatchBlock(start, end, throw0, "java/lang/InterruptedException");
                mv.visitLabel(start);
                {
                    mv.visitVarInsn(ALOAD, 0);
                    System.out.println("packageClassName : " + packageClassName);
                    mv.visitFieldInsn(GETFIELD, packageClassName, "countDownLatch",
                            "Ljava/util/concurrent/CountDownLatch;");
                    mv.visitLdcInsn(outTime);
                    mv.visitFieldInsn(GETSTATIC, "java/util/concurrent/TimeUnit", "MILLISECONDS",
                            "Ljava/util/concurrent/TimeUnit;");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/CountDownLatch", "await", "" +
                            "(JLjava/util/concurrent/TimeUnit;)Z", false);
                    mv.visitInsn(POP);
                }
                mv.visitLabel(end);
                mv.visitJumpInsn(GOTO, l2);

                mv.visitLabel(throw0);
                {
                    mv.visitFrame(F_SAME1, 0, null, 1, new Object[]{"java/lang/InterruptedException"});
                    mv.visitVarInsn(ASTORE, args.length + 2);
                    mv.visitVarInsn(ALOAD, args.length + 2);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/InterruptedException", "printStackTrace", "()V",
                            false);
                }
                mv.visitLabel(l2);
//                mv.visitFrame(F_SAME, 0, null, 0, null);

                if (threadStyle.equals("BG")) {
                    mv.visitLabel(l3);
                    mv.visitFrame(F_SAME, 0, null, 0, null);
                }
            }
            {

                if (returnStyle.equals("V")) {
                    mv.visitInsn(RETURN);
                } else {
                    if (result) {
                        {
                            int load = ALOAD;
                            if (checkReturnStyle(returnStyle, "Z")) load = ILOAD;
                            else if (checkReturnStyle(returnStyle, "C", "S", "I", "B"))
                                load = ILOAD;
                            else if (checkReturnStyle(returnStyle, "J"))
                                load = LLOAD;
                            else if (checkReturnStyle(returnStyle, "F"))
                                load = FLOAD;
                            else if (checkReturnStyle(returnStyle, "D"))
                                load = DLOAD;
                            else load = ALOAD;
                            mv.visitVarInsn(load, 0);
                            mv.visitFieldInsn(GETFIELD, packageClassName, "result", returnStyle);
                            if (checkReturnStyle(returnStyle, "Z")) mv.visitInsn(IRETURN);
                            else if (checkReturnStyle(returnStyle, "C", "S", "I", "B"))
                                mv.visitInsn(IRETURN);
                            else if (checkReturnStyle(returnStyle, "J"))
                                mv.visitInsn(LRETURN);
                            else if (checkReturnStyle(returnStyle, "F"))
                                mv.visitInsn(FRETURN);
                            else if (checkReturnStyle(returnStyle, "D"))
                                mv.visitInsn(DRETURN);
                            else mv.visitInsn(ARETURN);
                        }
                    } else {
                        if (checkReturnStyle(returnStyle, "Z")) {
                            mv.visitInsn(ICONST_0);
                            mv.visitInsn(IRETURN);
                        } else if (checkReturnStyle(returnStyle, "C", "S", "I", "B")) {
                            mv.visitInsn(ICONST_0);
                            mv.visitInsn(IRETURN);
                        } else if (checkReturnStyle(returnStyle, "J")) {
                            mv.visitInsn(LCONST_0);
                            mv.visitInsn(LRETURN);
                        } else if (checkReturnStyle(returnStyle, "D")) {
                            mv.visitInsn(DCONST_0);
                            mv.visitInsn(DRETURN);
                        } else if (checkReturnStyle(returnStyle, "F")) {
                            mv.visitInsn(FCONST_0);
                            mv.visitInsn(FRETURN);
                        } else {
                            mv.visitInsn(ACONST_NULL);
                            mv.visitInsn(ARETURN);
                        }
                    }
                }
            }
            mv.visitMaxs(4, 2);
            mv.visitEnd();
        }
        cw.visitEnd();
        try {
            FileOutputStream fos = new FileOutputStream(allClassPath);
            fos.write(cw.toByteArray());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return className;
    }
}
