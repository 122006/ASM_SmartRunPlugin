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
        LogUtil.println("Annotation " + s + " = " + String.valueOf(o));
        if (s.equals("value")) {
            if (((Long) o) == -2) {
                annotationData.NewThread = false;
            }else if (((Long) o) == 0) {
                annotationData.NewThread = true;
                annotationData.Result = false;
            } else if (((Long) o) > 0) {
                annotationData.NewThread = true;
                annotationData.Result = true;
                annotationData.OutTime = Math.max(annotationData.OutTime, (Long) o);
            }
        }
        if (s.equals("Style")) {
            if (String.valueOf(o).equals("Async")) annotationData.NewThread = true;
            if (String.valueOf(o).equals("Sync")) annotationData.NewThread = false;
        }
        if (s.equals("Result")) {
            if (String.valueOf(o).equals("Skip")) {
                annotationData.Result = false;
            }
            if (String.valueOf(o).equals("Wait")) {
                annotationData.Result = true;
            }
        }
        if (s.equals("OutTime")) {
            try {
                annotationData.OutTime = Math.max(annotationData.OutTime, Long.parseLong(String.valueOf(o)));
            } catch (NumberFormatException e) {
                annotationData.OutTime = Math.max(annotationData.OutTime, 2000);
            }
        }
        super.visit(s, o);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String s, String s1) {
        LogUtil.println("s=" + s + ";s1=" + s1);
        return super.visitAnnotation(s, s1);
    }
}
