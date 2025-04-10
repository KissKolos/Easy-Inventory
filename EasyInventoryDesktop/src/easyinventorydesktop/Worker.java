/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyinventorydesktop;

import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author 3041TAN-06
 */
public class Worker extends Thread {
    
    public static final Worker GLOBAL=new Worker();
    
    private final LinkedBlockingQueue<Runnable> tasks=new LinkedBlockingQueue<>();
    
    public Worker() {}

    @Override
    public void run() {
        while(true) {
            try {
                tasks.take().run();
            }
            catch (InterruptedException _e) {
                break;
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }
    
    public void addTask(Runnable t) {
        tasks.add(t);
    }
    
}
