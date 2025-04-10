public class Worker extends Thread {
    public static final Worker GLOBAL=new Worker();
    @Override
    public void run() { ... }
    public void addTask(Runnable task) { ... }
}
