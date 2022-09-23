/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.concurrent;

import io.netty.util.cjm.utils.PrintUitls;

import static io.netty.util.internal.ObjectUtil.checkPositive;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract base class for {@link EventExecutorGroup} implementations that handles their tasks with multiple threads at
 * the same time.
 */
public abstract class MultithreadEventExecutorGroup extends AbstractEventExecutorGroup {
    // 固定长度的线程池
    private final EventExecutor[] children;
    // 线程池的索引
    private final Set<EventExecutor> readonlyChildren;
    // 终止的线程个数
    private final AtomicInteger terminatedChildren = new AtomicInteger();
    // 线程终止时的异步结果
    private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
    // 线程选择器
    private final EventExecutorChooserFactory.EventExecutorChooser chooser;

    /**
     * Create a new instance.
     *
     * @param nThreads          the number of threads that will be used by this instance.
     * @param threadFactory     the ThreadFactory to use, or {@code null} if the default should be used.
     * @param args              arguments which will passed to each {@link #newChild(Executor, Object...)} call
     */
    protected MultithreadEventExecutorGroup(int nThreads, ThreadFactory threadFactory, Object... args) {
        this(nThreads, threadFactory == null ? null : new ThreadPerTaskExecutor(threadFactory), args);
    }

    /**
     * Create a new instance.
     *
     * @param nThreads          the number of threads that will be used by this instance.
     * @param executor          the Executor to use, or {@code null} if the default should be used.
     * @param args              arguments which will passed to each {@link #newChild(Executor, Object...)} call
     */
    protected MultithreadEventExecutorGroup(int nThreads, Executor executor, Object... args) {
        // 一个默认的选择策略
        // 线程数量为前面的核心数*2
        this(nThreads, executor, DefaultEventExecutorChooserFactory.INSTANCE, args);
    }

    /**
     * Create a new instance.
     *
     *
     * 设置线程工厂
     * 设置线程选择器
     * 实例化线程
     * 设置线程终止异步结果
     *
     * @param nThreads          the number of threads that will be used by this instance.
     * @param executor          the Executor to use, or {@code null} if the default should be used.
     * @param chooserFactory    the {@link EventExecutorChooserFactory} to use.
     * @param args              arguments which will passed to each {@link #newChild(Executor, Object...)} call
     */
    protected MultithreadEventExecutorGroup(int nThreads, Executor executor,
                                            EventExecutorChooserFactory chooserFactory, Object... args) {
        PrintUitls.printToConsole("新建线程组， nThreads=DEFAULT_EVENT_LOOP_THREADS(两倍cpu数量)， executor=null, SelectorProvider=SelectorProvider.provider(), \r\nSelectStrategyFactory=DefaultSelectStrategyFactory.INSTANCE, RejectedExecutionHandlers.reject(), EventExecutorChooserFactory=DefaultEventExecutorChooserFactory.INSTANCE");
        PrintUitls.printToConsole("新建线程组， 这里的EventExecutorChooserFactory, 是为了去创建一个选择的, 而前面创建的DefaultSelectStrategyFactory, 是给到子线程去使用的");
        // 确保这个数量为正的
        checkPositive(nThreads, "nThreads");

        if (executor == null) {
            // 如果这个线程池为null， 那么在这里新建一个
            // DefaultThreadFactory默认的线程创建策略
            PrintUitls.printToConsole("新建线程组，executor=ThreadPerTaskExecutor(DefaultThreadFactory线程工厂)");
            executor = new ThreadPerTaskExecutor(newDefaultThreadFactory());
        }

        PrintUitls.printToConsole("新建线程组，新建一个空的EventExecutor数组");
        // 事件执行器， 新建一个数组
        children = new EventExecutor[nThreads];

        for (int i = 0; i < nThreads; i ++) {
            boolean success = false;
            try {
                // 给每一个执行器赋值
                // 留给子类去实现， 并且调用子类的方法
                PrintUitls.printToConsole("新建线程组，循环去实例化每一个EventExecutor");
                children[i] = newChild(executor, args);
                success = true;
            } catch (Exception e) {
                // TODO: Think about if this is a good exception type
                // 如果失败了， 那么抛出一个异常
                throw new IllegalStateException("failed to create a child event loop", e);
            } finally {
                // 如果不成功的话， 那么把之前， 已经实例化好的线程池， 那么直接关闭掉线程池
                PrintUitls.printToConsole("如果不成功的话");
                if (!success) {
                    for (int j = 0; j < i; j ++) {
                        // 关闭每一个线程池
                        PrintUitls.printToConsole("新建线程组，循环调用shutdownGracefully");
                        children[j].shutdownGracefully();
                    }

                    for (int j = 0; j < i; j ++) {
                        // 再次拿到线程池
                        EventExecutor e = children[j];
                        try {
                            // 如果这个线程池没有关闭的话， 那么就等待这个线程池终止
                            // 这里不会去执行， 返回true， 则为false
                            while (!e.isTerminated()) {
                                PrintUitls.printToConsole("新建线程组，循环调用awaitTermination");
                                e.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                            }
                        } catch (InterruptedException interrupted) {
                            // Let the caller handle the interruption.
                            // 如果还有异常， 那么我们去打断当前线程
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        }

        // 选择策略
        // 新建一个选择策略
        PrintUitls.printToConsole("新建线程组， 新建一个选择器， 这个选择器里持有刚才新建的那些个线程, chooserFactory.newChooser(children)");
        chooser = chooserFactory.newChooser(children);

        PrintUitls.printToConsole("新建线程组， 新建一个FutureListener=terminationListener");
        // 搞一个终止监听器
        final FutureListener<Object> terminationListener = new FutureListener<Object>() {
            @Override
            public void operationComplete(Future<Object> future) throws Exception {
                PrintUitls.printToConsole("监听器执行， terminationListener");
                if (terminatedChildren.incrementAndGet() == children.length) {
                    terminationFuture.setSuccess(null);
                }
            }
        };

        for (EventExecutor e: children) {
            // 这是属于这个线程的一个DefaultPromise
            // 给它新增一个监听器
            PrintUitls.printToConsole("新建线程组， 循环的给这个线程EventLoop的promise添加一个监听器terminationListener");
            e.terminationFuture().addListener(terminationListener);
        }

        // 给其设置为不可变的集合
        Set<EventExecutor> childrenSet = new LinkedHashSet<EventExecutor>(children.length);
        Collections.addAll(childrenSet, children);
        PrintUitls.printToConsole("新建线程组， 给这些children给修改成readonlyChildren");
        readonlyChildren = Collections.unmodifiableSet(childrenSet);
    }

    protected ThreadFactory newDefaultThreadFactory() {
        return new DefaultThreadFactory(getClass());
    }

    @Override
    public EventExecutor next() {
        return chooser.next();
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return readonlyChildren.iterator();
    }

    /**
     * Return the number of {@link EventExecutor} this implementation uses. This number is the maps
     * 1:1 to the threads it use.
     */
    public final int executorCount() {
        return children.length;
    }

    /**
     * Create a new EventExecutor which will later then accessible via the {@link #next()}  method. This method will be
     * called for each thread that will serve this {@link MultithreadEventExecutorGroup}.
     *
     */
    protected abstract EventExecutor newChild(Executor executor, Object... args) throws Exception;

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        for (EventExecutor l: children) {
            l.shutdownGracefully(quietPeriod, timeout, unit);
        }
        return terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return terminationFuture;
    }

    @Override
    @Deprecated
    public void shutdown() {
        for (EventExecutor l: children) {
            l.shutdown();
        }
    }

    @Override
    public boolean isShuttingDown() {
        for (EventExecutor l: children) {
            if (!l.isShuttingDown()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isShutdown() {
        for (EventExecutor l: children) {
            if (!l.isShutdown()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isTerminated() {
        for (EventExecutor l: children) {
            if (!l.isTerminated()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        loop: for (EventExecutor l: children) {
            for (;;) {
                long timeLeft = deadline - System.nanoTime();
                if (timeLeft <= 0) {
                    break loop;
                }
                if (l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS)) {
                    break;
                }
            }
        }
        return isTerminated();
    }
}
