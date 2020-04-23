package com.by122006.asm_smartrunpluginimp.line;

/**
 * Created by admin on 2020/4/10.
 */
@Deprecated
public  class LineData<T> {
    public static <T> LineData build(T t){
        LineData data=new LineData(t);
        return data;
    }
    public T getData() {
        return data;
    }

    public LineData setData(T data) {
        this.data = data;
        return this;
    }

    T data;

    public LineData(T data) {
        this.data = data;
    }

}
