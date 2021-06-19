/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MultiThreading;

/**
 *
 * @author Administrator
 */
public class Multi1 implements Runnable {

    @Override
    public void run() {
        System.out.println("Running Thread..........");
    }

    public static void main(String[] args) {
        Multi1 m1 = new Multi1();
        Thread t1 = new Thread(m1);
        t1.start();
    }
}
