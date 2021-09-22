package com.by122006.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ALOAD;

/**
 * Created by 122006 on 2018/2/28.
 */

public class Utils {
    /**
     * 判断返回值是否有指定类型之一
     *
     * @param returnStyle
     * @param matchs
     * @return
     */
    @Deprecated
    public static boolean checkReturnStyle(String returnStyle, BiPredicate<String, String> predicate, String... matchs) {
        for (String match : matchs) {
            if (predicate.test(returnStyle, match)) return true;
        }
        return false;
    }

    /**
     * visitVarInsn
     *
     * @return
     */
    public static int visitVarInsn(MethodVisitor mv, String argStyle, int firstIndex) {
        int load = ALOAD;
        int nextArgsSpaceIndex = firstIndex;
        if (argStyle.startsWith("[")) {
            load = ALOAD;
            firstIndex += 1;
        } else if ("Z".equals(argStyle)) {
            load = ILOAD;
            firstIndex += 1;
        } else if (Arrays.asList("C", "S", "I", "B").contains(argStyle)) {
            load = ILOAD;
            firstIndex += 1;
        } else if ("J".equals(argStyle)) {
            load = LLOAD;
            firstIndex += 2;
        } else if ("F".equals(argStyle)) {
            load = FLOAD;
            firstIndex += 1;
        } else if ("D".equals(argStyle)) {
            load = DLOAD;
            firstIndex += 2;
        } else {
            load = ALOAD;
            firstIndex += 1;
        }
        mv.visitVarInsn(load, nextArgsSpaceIndex);
        return firstIndex;
    }


    /**
     * 判断file在文件夹中序号最大衍生类的序号
     *
     * @param file E:\android\...\MainActivity.class
     * @return -1:不存在
     */
    public static int getClassHaveIndexInDir(File file) {
        String oName = file.getPath().replace(".class", "");
        File parent = file.getParentFile();
        File[] files = parent.listFiles();
        int i = -1;
        for (File f : files) {
            String name = f.getPath().replace(".class", "");
            if (name.startsWith(oName + "$")) {
                if (name.contains("_SmartRun_")) {
                    try {
                        int index = Integer.parseInt(name.substring(name.lastIndexOf("_")));
                        if (index > i) i = index;
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }
        }
        return i;
    }

    public static String getExceptionDetails(Exception e) {
        StringBuilder s = new StringBuilder();
        Throwable ourCause = e.getCause();
        if (ourCause != null)
            s.append(ourCause);

        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement traceElement : trace)
            s.append("\nat " + traceElement);

        return s.toString();
    }

    /**
     * 检查方法access
     *
     * @param access
     * @param acc
     * @return
     */
    public static boolean checkAccess(int access, int acc) {
        return (access & acc) != 0;
    }

    public static void main(String[] str) {
        LogUtil.println(splitObjsArg("[[I[[[[I").length == 2);
        LogUtil.println(splitObjsArg("[Ljava.lang.String;[[[[I").length == 2);
        LogUtil.println(splitObjsArg("Ljava.lang.String;[[[[I").length == 2);
        LogUtil.println(splitObjsArg("Ljava.lang.String;[[I[[I").length == 3);
        LogUtil.println(splitObjsArg("Ljava.lang.String;[[ILjava.lang.String;[[I").length == 4);
        LogUtil.println(splitObjsArg("[[[Ljava.lang.String;[[ILjava.lang.String;[[I").length == 4);
        LogUtil.println(splitObjsArg("[[[Ljava.lang.String;[[IILjava.lang.String;[[I").length == 5);
        LogUtil.println(splitObjsArg("[[[Ljava.lang.String;Ljava.lang.String;[[I").length == 3);
        LogUtil.println(splitObjsArg("I[[[Ljava.lang.String;Ljava.lang.String;[[I").length == 4);
        LogUtil.println(splitObjsArg("II[[[Ljava.lang.String;Ljava.lang.String;[[I").length == 5);
    }


    public static String[] splitObjsArg(String str) {
        ArrayList<String> types = new ArrayList<>();
        String thisStr = "";
        while (str.length() > 0) {
            String first = str.substring(0, 1);
            if (first.equals("L")) {
                int q = str.indexOf(";");
                if (q <= -1) {
                    thisStr += str + ";";
                    str = "";
                } else {
                    thisStr += str.substring(0, q + 1);
                    str = str.substring(q+1);
                }
                types.add(thisStr);
                thisStr = "";
            } else if (Arrays.asList("B", "C", "D", "F", "I", "J", "S", "Z", "V").contains(first)) {
                thisStr += first;
                types.add(thisStr);
                thisStr = "";
                str = str.substring(1);
            } else if (first.equals("[")) {
                thisStr += first;
                str = str.substring(1);
            } else throw new RuntimeException("未知前缀" + first);
        }
        String collect = types.stream().collect(Collectors.joining("    "));
        LogUtil.println(collect);
        return types.toArray(new String[0]);
    }

    public static int OP_LOAD(String TypeString) {
        if (Objects.equals("I", TypeString)) {
            return Opcodes.ILOAD;
        } else if (Objects.equals("B", TypeString)) {
            return Opcodes.BALOAD;
        } else if (Objects.equals("F", TypeString)) {
            return Opcodes.FLOAD;
        } else if (Objects.equals("L", TypeString)) {
            return Opcodes.LLOAD;
        } else if (Objects.equals("D", TypeString)) {
            return Opcodes.DLOAD;
        } else return Opcodes.ALOAD;
    }

}
