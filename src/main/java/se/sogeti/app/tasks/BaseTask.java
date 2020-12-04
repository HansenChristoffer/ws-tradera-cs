package se.sogeti.app.tasks;

public abstract class BaseTask implements Runnable {
    long n;
    String id;

    protected BaseTask(long n, String id) {
        this.n = n;
        this.id = id;
    }

    public void run() {
    }

    @Override
    public String toString() {
        return "{" + " n='" + n + "'" + ", id='" + id + "'" + "}";
    }
}