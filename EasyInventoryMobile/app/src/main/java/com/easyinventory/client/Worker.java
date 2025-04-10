package com.easyinventory.client;

import java.util.concurrent.LinkedBlockingQueue;

public class Worker extends Thread {

    public static final Worker GLOBAL=new Worker();

    private final LinkedBlockingQueue<Runnable> tasks=new LinkedBlockingQueue<>();

    /** @noinspection InfiniteLoopStatement*/
    @Override
    public void run() {
        try{
        try{
            while(true) {
                System.out.println("WORKER waiting");
                Runnable r=tasks.take();
                System.out.println("WORKER executing");
                r.run();
                System.out.println("WORKER finished");
            }
        }catch (InterruptedException e) {e.printStackTrace();}
        }catch (Exception e) {e.printStackTrace();}
        System.out.println("WORKER exited");
    }

    public void addTask(Runnable task) {
        tasks.add(task);
    }

}
