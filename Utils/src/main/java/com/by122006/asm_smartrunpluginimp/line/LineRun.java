package com.by122006.asm_smartrunpluginimp.line;


import com.by122006.asm_smartrunpluginimp.Interface.ThreadStyle.Style;
import com.by122006.asm_smartrunpluginimp.ThreadManager;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by admin on 2020/4/10.
 */
public class LineRun {
    ArrayList<LineTask> lineTasks = new ArrayList<>();
    boolean assertAllNotNull = false;
    Object data = null;
    boolean needBreak = false;
    private Exception exception;
    private long TimeOut=10;
    private static LineBiFunction<LineRun, LineFunction, Object> bgTaskFunction = new LineBiFunction<LineRun, LineFunction,
            Object>() {
        ExecutorService executorService = new ThreadPoolExecutor(
                2, 8,
                0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(512), // 使用有界队列，避免OOM
                new ThreadPoolExecutor.DiscardPolicy());

        @Override
        public Object apply(LineRun lineRun,LineFunction acc) throws Exception {
            Future<Object> future = executorService.submit(() -> acc.apply(lineRun.data));
            return future.get(lineRun.TimeOut, TimeUnit.SECONDS);
        }
    };
    private static LineBiFunction<LineRun,LineFunction,  Object> uiTaskFunction = (lineRun, acc) -> {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Exception> te = new AtomicReference<>();
        ThreadManager.postUIThread(() -> {
            try {
                lineRun.data = acc.apply(lineRun.data);
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                te.set(e);
            }
        });
        latch.await(lineRun.TimeOut, TimeUnit.SECONDS);
        if (te.get() != null) throw te.get();
        return lineRun.data;
    };
    private LinePair<Style, LineConsumer<Exception>> handleException = null;

    public static LineRun create() {

        final LineRun lineRun = new LineRun();
        return lineRun;
    }

    /**
     * 设置超时时间 对该次链所有bg、ui有效
     * @param timeOut
     * @return
     */
    public LineRun setTimeOut(long timeOut) {
        TimeOut = timeOut;
        return this;
    }

    public static void setBgTaskFunction(LineBiFunction<LineRun,LineFunction, Object> bgTaskFunction) {
        LineRun.bgTaskFunction = bgTaskFunction;
    }

    public static void setUiTaskFunction(LineBiFunction<LineRun,LineFunction, Object> uiTaskFunction) {
        LineRun.uiTaskFunction = uiTaskFunction;
    }

    /**
     * 运行一段逻辑
     *
     * @param style
     * @param function 其中调用stopNextActions()方法会结束后续运行
     * @return
     */
    public LineRun run(Style style, Task function) {
        lineTasks.add(new LineTask(function::apply, style, TaskStyle.normal));
        return this;
    }
    /**
     * 运行于新的BG线程
     *
     * @param function 接受参数的处理逻辑。其中调用stopNextActions()方法会结束后续运行
     * @return
     */
    public LineRun runOnBG(Task function) {
        return run(Style.BG, function);
    }

    /**
     * 运行于UI线程
     *
     * @param function 接受参数的处理逻辑。其中调用stopNextActions()方法会结束后续运行
     * @return
     */
    public LineRun runOnUI(Task function) {
        return run(Style.UI, function);
    }
    /**
     * 运行于默认线程（开始创建的）
     *
     * @param function 接受参数的处理逻辑。其中调用stopNextActions()方法会结束后续运行
     * @return
     */
    public LineRun run(Task function) {
        return run(Style.Default, function);
    }

    /**
     * 捕捉上次方法运行结果的错误,你可以对对应数据进行修正,并进行下一轮动作.
     *
     * @param threadStyle     {@see ThreadStyle.Style}
     * @param booleanConsumer 接受参数的处理逻辑。如果其中抛出任何错误会结束后续运行
     * @return
     */
    public LineRun catchException(Style threadStyle, BiTask<Object, Exception> booleanConsumer) {
        lineTasks.add(new LineTask((Object data) -> {
            booleanConsumer.apply(data, exception);
            return data;
        }, threadStyle, TaskStyle.catchEx));
        return this;
    }

    /**
     * 设置后续流程的所有异常捕捉逻辑。如果异常结束后续流程，一定会触发该方法
     * @param style
     * @param consumer
     * @return
     */
    public LineRun setHandleException(Style style, LineConsumer<Exception> consumer) {
        handleException = LinePair.create(style, consumer);
        return this;
    }

    public LineRun removeHandleException() {
        handleException = null;
        return this;
    }

    public LineRun preAssertAllNotNull() {
        assertAllNotNull = true;
        return this;
    }
    /**
     * 断言一段数据，如果返回false结束后续流程
     * @param accept
     * @param threadStyle
     * @param consumer
     * @return
     */
    public LineRun assertData(LinePredicate accept, Style threadStyle, LineConsumer consumer) {

        lineTasks.add(new LineTask((Object data) -> {
            if (!accept.test(data)) {
                consumer.accept(data);
                throw new EndException();
            }
            return data;
        }, threadStyle, TaskStyle.normal));
        return this;

    }

    /**
     * 断言一段数据，如果返回false结束后续流程
     * @param accept
     * @return
     */
    public LineRun assertData(LinePredicate accept) {

        lineTasks.add(new LineTask((Object data) -> {
            if (!accept.test(data)) {
                throw new EndException(null);
            }
            return data;
        }, Style.Default, TaskStyle.assertData));
        return this;

    }

    public void start() {
        new Thread(() -> {
            for (LineTask task : lineTasks) {
                switch (task.taskStyle) {
                    case normal:
                        try {
                            runTask(task);
                        }catch (EndException e){
                            needBreak=true;
                        }catch (Exception e) {
                            e.printStackTrace();
                            exception = e;
                        }
                        if (assertAllNotNull && data == null) break;
                        break;
                    case catchEx:
                        if (exception != null) {
                            try {
                                runTask(task);
                            } catch (EndException e) {
                                needBreak = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                needBreak = true;
                            }
                        }
                        break;
                    case assertData:
                        try {
                            runTask(task);
                        } catch (EndException e) {
                            needBreak = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            needBreak = true;
                        }
                        break;
                }
                if (exception != null && needBreak) {
                    handleException.second.accept(exception);
                }

                if (needBreak) {
                    break;
                }
            }
        }).start();


    }

    private void runTask(LineTask task) throws Exception {
        switch (task.threadStyle) {
            case Default:
                data = task.function.apply(data);
                break;
            case UI:
                data = uiTaskFunction.apply(this,task.function);
                break;
            case BG:
                data = bgTaskFunction.apply(this,task.function);
                break;
        }

    }

    public enum TaskStyle {
        normal, catchEx, assertData
    }

    public static class LineTask  {
        LineFunction function;
        Style threadStyle;
        TaskStyle taskStyle = TaskStyle.normal;

        public LineTask(LineFunction function, Style threadStyle, TaskStyle taskStyle) {
            this.function = function;
            this.threadStyle = threadStyle;
            this.taskStyle = taskStyle;
        }


        public Style getThreadStyle() {
            return threadStyle;
        }

        public TaskStyle getTaskStyle() {
            return taskStyle;
        }
    }


}
