/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Abstraction;

//Interface declaration: by first user  
interface Drawable {

    void draw();

    default void create() {
        System.out.println("Creating shape...........");
    }

    static int cube(int x) {
        return x * x * x;
    }
}
//Implementation: by second user  

class box implements Drawable {

    public void draw() {
        System.out.println("drawing rectangle");
    }
}

class round implements Drawable {

    public void draw() {
        System.out.println("drawing circle");
    }
}

/**
 *
 * @author Administrator
 */
public class InterfaceSample {

    public static void main(String[] args) {
        Drawable d = new box();
        d.draw();
        d.create();
        System.out.println("Cube : " + Drawable.cube(3));
    }

}
