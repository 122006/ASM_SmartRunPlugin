package com.by122006.asm_smartrunpluginimp;

import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.by122006.asm_smartrunpluginimp.Interface.BGThread;
import com.by122006.asm_smartrunpluginimp.Utils.ThreadUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @BGThread
    public int[] testIntArrayReturn() {
        return new int[]{1, 2};
    }

    @BGThread
    public String[] testObjectArrayReturn() {
        return new String[]{"12", "34"};
    }

    @BGThread
    public void testVoidReturn() {
        return;
    }

    @BGThread
    public long testLongReturn() {
        return 1000L;
    }

    @BGThread
    public Double testDoubleReturn() {
        return 1000D;
    }

    @BGThread
    public boolean testBooleanReturn() {
        return true;
    }

    @BGThread
    public byte testByteReturn() {
        return (byte) 10;
    }

    @BGThread
    public short testShotReturn() {
        return (short) 10;
    }

    @BGThread
    public String[][] testStringArrayDupReturn() {
        return new String[][]{new String[0], new String[0]};
    }

    @BGThread
    public int[][] testIntArrayDupReturn() {
        return new int[][]{new int[0], new int[0]};
    }

    @BGThread
    public void testArgs_singleInt(int a) {
        return;
    }

    @BGThread
    public void testArgs_singleShort(short a) {
        return;
    }

    @BGThread
    public void testArgs_singleByte(byte a) {
        return;
    }

    @BGThread
    public void testArgs_singleLong(long a) {
//        a = a + 1;
        return;
    }

    @BGThread
    public void testArgs_singleLong(long a, double b, int c) {
        a = a + 3;
        return;
    }

    @BGThread
    public void testArgs_singleLong2(long a, int b) {
        a = a + 2;
        return;
    }

    @BGThread
    public void testArgs_singleLong3(Long a, int b) {
        a = a + 1;
        return;
    }

    @BGThread
    public void testArgs_mulArgs(int... args) {
        return;
    }

    @BGThread
    public void testArgs_mulArgs2(Long a, int b, Object... args) {
        a = a + 1;
        return;
    }

    @BGThread
    public void testArgs_mulArgs2(int[] a, int[][] b, int[] c, int[][][] d, int... args) {
        return;
    }

    @BGThread
    public void testArgs_singleDouble(double a) {
//        a = a + 1;
        return;
    }

    @BGThread
    public void testArgs_singleBoolean(boolean a) {
        return;
    }

    @BGThread
    public void testArgs_singleString(String a) {
        return;
    }


    @BGThread(Style = BGThread.Sync)
    public void testBGThread_StyleSync(String a) {
        return;
    }

    @BGThread(Style = BGThread.Sync)
    public String testBGThread_StyleSync_Return(String a) {
        return "";
    }

    @BGThread(Style = BGThread.Async)
    public void testBGThread_StyleAsync(String a) {
        return;
    }

    @BGThread(Style = BGThread.Async)
    public String testBGThread_StyleAsync_Return(String a) {
        return "";
    }


    @BGThread(Style = BGThread.Sync, Result = BGThread.Wait)
    public void testBGThread_StyleSync_Wait(String a) {
        return;
    }

    @BGThread(Style = BGThread.Sync, Result = BGThread.Skip)
    public String testBGThread_StyleSync_Wait_Return(String a) {
        return "";
    }

    @BGThread(BGThread.Assert)
    public String testBGThread_Assert(String a) {
        return "";
    }

    @BGThread(BGThread.NewTask)
    public String testBGThread_NewTask(String a) {
        return "";
    }

    @BGThread(BGThread.WaitResult)
    public String testBGThread_WaitResult(String a) {
        return "";
    }

    @BGThread(5000)
    public String testBGThread_WaitResult_5000(String a) {
        return "";
    }
}
