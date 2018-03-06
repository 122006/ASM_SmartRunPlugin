package com.by122006.asm;

/**
 * Created by 122006 on 2018/3/6.
 */

public class AnnotationData {

    public String getOutAnnotation() {
        return outAnnotation;
    }

    public AnnotationData setOutAnnotation(String outAnnotation) {
        this.outAnnotation = outAnnotation;
        return this;
    }

    public boolean isNewThread() {
        return NewThread;
    }

    public AnnotationData setNewThread(boolean newThread) {
        NewThread = newThread;
        return this;
    }

    public long getOutTime() {
        return OutTime;
    }

    public AnnotationData setOutTime(long outTime) {
        OutTime = outTime;
        return this;
    }

    public boolean isResult() {
        return Result;
    }

    public AnnotationData setResult(boolean result) {
        Result = result;
        return this;
    }

    String outAnnotation;


    @Override
    public String toString() {
        return "AnnotationData{" +
                "outAnnotation='" + outAnnotation + '\'' +
                ", NewThread=" + NewThread +
                ", OutTime=" + OutTime +
                ", Result=" + Result +
                ", readFromAnnotation=" + readFromAnnotation +
                '}';
    }

    /**
     * Style Async/Sync
     */
    boolean NewThread = false;
    /**
     * OutTime long
     */
    long OutTime = 2000l;
    /**
     * Result Skip/Wait
     */
    boolean Result = true;
    public boolean isUsed(){
        return outAnnotation!=null&&outAnnotation.length()>0;
    }

    public boolean isReadFromAnnotation() {
        return readFromAnnotation;
    }

    public AnnotationData setReadFromAnnotation(boolean readFromAnnotation) {
        this.readFromAnnotation = readFromAnnotation;
        return this;
    }

    boolean readFromAnnotation=false;
}
