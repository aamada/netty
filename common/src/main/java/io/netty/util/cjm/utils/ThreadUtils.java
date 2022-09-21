package io.netty.util.cjm.utils;

public class ThreadUtils {
    public static String getThreadInfo() {
        Thread thread = Thread.currentThread();
        long id = thread.getId();
        String name = thread.getName();
//        ThreadGroup threadGroup = thread.getThreadGroup();
//        String name1 = threadGroup.getName();
        return "id = " + id + ";name = " + name + ";======";
    }
}
