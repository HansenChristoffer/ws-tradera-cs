package se.sogeti.app.tasks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadExecutor extends ThreadPoolExecutor {

    private static final Set<Runnable> RUN = new HashSet<>();

    public ThreadExecutor(int poolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> workQueue) {
        super(poolSize, maxPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        RUN.add(r);
    }

    @Override
    public void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        RUN.remove(r);
    }

    public static boolean contains(String s) {
        Iterator<Runnable> iter = RUN.iterator();

        while (iter.hasNext()) {
            Runnable i = iter.next();
            if (i.toString().contains(s)) {
                return true;
            }
        }

        return false;
    }

    public static Set<Runnable> getRunnable() {
        return RUN;
    }

}