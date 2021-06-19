/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Abstraction;

abstract class Bike {

    Bike() {
        System.out.println("Bike Created.....");
    }

    abstract void run();

    void changeGear() {
        System.out.println("Gear Changed.....");
    }
}

class Honda extends Bike {

    @Override
    void run() {
        System.out.println("Bike is running......");
    }

}

/**
 *
 * @author Administrator
 */
public class AbstractionSample {

    public static void main(String[] args) {
        Bike obj = new Honda();
        
        obj.changeGear();
        obj.run();
    }
}
