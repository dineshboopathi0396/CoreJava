/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inheritancesample;

class Animal {

    public void run() {
        System.out.println("Running...");
    }
    public void run1() {
        System.out.println("Running1...");
    }
}

class Dog extends Animal {

    public void run() {
        System.out.println("Dog is Running...");
    }
     public void bark() {
        System.out.println("Dog is Barking...");
    }
}

/**
 *
 * @author Administrator
 */
public class SingleInheritance {

    public static void main(String[] args) {
        Animal d = new Dog();
        Dog dog = new Dog();
        Animal d1 = new Animal();

        d.run();
        d.run1();
        dog.run();
        dog.run1();
        d1.run();
        dog.bark();
    }

}
