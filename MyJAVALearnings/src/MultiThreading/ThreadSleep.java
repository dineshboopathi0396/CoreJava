/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MultiThreading;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class ThreadSleep extends Thread {

    public void run() {
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(500);
            } catch (Exception Ex) {
                System.out.println("Exception in run() : " + Ex);
            }
            System.out.println(i);
        }
    }

    public static void main(String args[]) {
        ThreadSleep ts1 = new ThreadSleep();
        ThreadSleep ts2 = new ThreadSleep();
        ThreadSleep ts3 = new ThreadSleep();

        ts1.start();
        try {
            ts1.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadSleep.class.getName()).log(Level.SEVERE, null, ex);
        }
        ts2.start();
        ts3.start();
    }
}
