package com.by122006.asm;

/**
 * Created by 122006 on 2018/2/28.
 */

public class MethodInfo {
    public int access;
    public String name;
    public String desc;
    public String signature;
    public String[] exceptions;
    public AnnotationData annotations;

    public MethodInfo(int access, String name, String desc, String signature, String[] exceptions, AnnotationData annotations) {
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
        this.annotations = annotations;
    }



}
