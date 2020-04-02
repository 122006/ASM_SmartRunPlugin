package com.by122006.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * Created by admin on 2018/11/2.
 */

public class Configure {
    final static String FileName="SmartRun.Configure";

    private static void getDataFromText(){
        URL url=Configure.class.getResource("/");
        String fileStr=url.getFile();
        File file=new File(fileStr+"/"+FileName);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String text=readToString(file);
        if(text==null){
            System.err.println("未获取到配置文件内容");
            return;
        }
        for (String line:text.split("\n")){
            if (line.startsWith("#")) continue;
            line=line.trim();



        }

    }
    private static String readToString(File file) {
        String encoding = "UTF-8";
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

}
