package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.thread.ThreadUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 数据队列
 *
 * @param <T>
 */
public class DataQueue<T> {
    public final int maxSize;
    private final LinkedBlockingQueue<T> queue;

    private Throwable exception;

    public DataQueue(final int size) {
        Preconditions.assertTrue(size < 10 || size > 100000, "queue size should > 10 and < 100000");
        maxSize = size;
        this.queue = new LinkedBlockingQueue<>(size);
    }

    public static DataQueue of(int size) {
        return new DataQueue(size);
    }

    public static DataQueue of() {
        return new DataQueue(2000);
    }

    public void put(final T event) throws InterruptedException {
        if (event == null) {
            return;
        }
        queue.put(event);
        if (queue.size() > 2 * maxSize / 3) {
            ThreadUtil.safeSleep(3);
        }
    }

    public void safePut(final T event) {
        if (event == null) {
            return;
        }
        try {
            queue.put(event);
        } catch (Exception e) {

        }
    }


    /**
     * 获取并移除队列头元素。如果在等待时间内有元素入队，返回该元素；如果等待时间过期且队列仍为空，返回 null
     *
     * @return
     * @throws InterruptedException
     */
    public T next() throws InterruptedException {
        return queue.poll(20, TimeUnit.MILLISECONDS);
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    /**
     * 检索并移除队列的头元素。如果队列为空，take() 会阻塞当前线程，直到有元素可以返回
     *
     * @return
     * @throws InterruptedException
     */
    public T nextUntil() throws InterruptedException {
        return queue.take();
    }

    public List<T> drain(int maxElements) {
        final List<T> list = new ArrayList<>();
        queue.drainTo(list, maxElements);
        return list;
    }

    public List<T> drain() {
        return drain(2000);
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }
}