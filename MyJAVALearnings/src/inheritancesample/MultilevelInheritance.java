/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inheritancesample;

class Parent {

    void eat() {
        System.out.println("eating...");
    }
}

class Son extends Parent {

    public void run() {
        System.out.println("running...");
    }
}

class GrandSon extends Son {

    void weep() {
        System.out.println("weeping...");
    }
}

/**
 *
 * @author Administrator
 */
public class MultilevelInheritance {

    public static void main(String[] args) {
        GrandSon GS = new GrandSon();

        GS.eat();
        GS.run();
        GS.weep();
    }

}
