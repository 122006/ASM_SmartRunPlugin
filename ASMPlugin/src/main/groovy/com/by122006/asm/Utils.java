package com.by122006.asm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

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
    public static boolean checkReturnStyle(String returnStyle, String... matchs) {
        for (String match : matchs) {
            if (returnStyle.endsWith(match)) return true;
        }
        return false;
    }

    /**
     * 判断file在文件夹中序号最大衍生类的序号
     *
     * @param file E:\android\...\MainActivity.class
     * @return -1:不存在
     */
    public static int getClassHaveIndexInDir(File file) {
        String oName = file.getAbsolutePath().replace(".class", "");
        File parent = file.getParentFile();
        File[] files = parent.listFiles();
        int i = -1;
        for (File f : files) {
            String name = f.getAbsolutePath().replace(".class", "");
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


    public static String[] splitObjsArg(String str) {
        String[] args = str.split(";");
        ArrayList<String> ls = new ArrayList<>();
        Collections.addAll(ls, args);
        ArrayList<String> ls2 = new ArrayList<>();
        for (String s : ls) {
            if (s.length() == 0) continue;
            String rest=s;
            while (!rest.startsWith("L")&&rest.length()>0) {
                ls2.add(String.valueOf(rest.charAt(0)));
                rest=rest.substring(1);
            }
            if (rest.length()>0) ls2.add(rest);
        }
        args = ls2.toArray(new String[]{});
        if (args.length == 1 && args[0].length() == 0) args = new String[]{};
        for (int i = 0; i < args.length; i++) {
            args[i] += args[i].length() == 1 ? "" : ";";
            System.out.println(i + " : " + args[i]);
        }
        return args;
    }

}
