package com.by122006.asm;

import org.objectweb.asm.AnnotationVisitor;

/**
 * Created by 122006 on 2018/3/6.
 */

public class MyAnnotationVisitor extends AnnotationVisitor {
    public MyAnnotationVisitor(int i, AnnotationData annotationData) {
        super(i);
        this.annotationData = annotationData;
    }

    public MyAnnotationVisitor(int i, AnnotationVisitor annotationVisitor, AnnotationData annotationData) {
        super(i, annotationVisitor);
        this.annotationData = annotationData;
    }

    AnnotationData annotationData;


    @Override
    public void visit(String s, Object o) {
        System.out.println("Annotation " + s + " = " + String.valueOf(o));
        if (s.equals("Style")) {
            if (String.valueOf(o).equals("Async")) annotationData.NewThread = true;
            if (String.valueOf(o).equals("Sync")) annotationData.NewThread = false;
        }
        if (s.equals("OutTime")) {
            try {
                annotationData.OutTime = Long.parseLong(String.valueOf(o));
            } catch (NumberFormatException e) {
                annotationData.OutTime = 2000;
            }
        }
        if (s.equals("Result")) {
            if (String.valueOf(o).equals("Skip")) annotationData.Result = false;
            if (String.valueOf(o).equals("Wait")) annotationData.Result = true;
        }
        super.visit(s, o);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String s, String s1) {
        System.out.println("s=" + s + ";s1=" + s1);
        return super.visitAnnotation(s, s1);
    }
}
