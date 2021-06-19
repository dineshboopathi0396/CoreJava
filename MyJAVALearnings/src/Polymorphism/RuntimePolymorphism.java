/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Polymorphism;

class Shape {

    int length = 5;

    void draw() {
        System.out.println("drawing...");
    }
}

class Rectangle extends Shape {

    int length = 10;

    void draw() {
        System.out.println("drawing rectangle...");
    }
}

class Circle extends Shape {

    int length = 20;

    void draw() {
        System.out.println("drawing circle...");
    }
}

class Triangle extends Shape {

    void draw() {
        System.out.println("drawing triangle...");
    }
}

/**
 *
 * @author Administrator
 */
public class RuntimePolymorphism {

    public static void main(String args[]) {
        Shape s;
        s = new Rectangle();
        System.out.println("Rectangle length : " + s.length);
        s.draw();
        s = new Circle();
        s.draw();
        s = new Triangle();
        s.draw();
    }
}
