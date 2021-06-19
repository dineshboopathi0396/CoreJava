/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Polymorphism;

class Addition {

    public int add(int a, int b) {
        return a + b;
    }

    public int add(int a, int b, int c) {
        return a + b + c;
    }

    public double add(int a, double b) {
        return a + b;
    }
}

/**
 *
 * @author Administrator
 */
public class MethodOverloading {

    static int q;

    static {
        q = 10;
    }

    public static void main(String[] args) {
        Addition a = new Addition();
        System.out.println("Addition : " + a.add(1, 1));
        System.out.println("Addition : " + a.add(1, 1, 4));
        System.out.println("Addition : " + a.add(1, 1.3));
        System.out.println("Q : " + q);
        q = 9;
        System.out.println("Q : " + q);
    }
}
