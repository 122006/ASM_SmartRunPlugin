package com.by122006.asm;

import java.io.File;

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
    public static int getClassHaveIndexInDir(File file){
        String oName= file.getAbsolutePath().replace(".class", "");
        File parent=file.getParentFile();
        File[] files=parent.listFiles();
        int i=-1;
        for(File f:files){
            String name= f.getAbsolutePath().replace(".class", "");
            if (name.startsWith(oName+"$")){
                if (name.contains("_SmartRun_")){
                    try {
                        int index= Integer.parseInt(name.substring(name.lastIndexOf("_")));
                        if (index>i) i=index;
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
    public static boolean checkAccess(int access,int acc) {
        return (access & acc) != 0;
    }



}
