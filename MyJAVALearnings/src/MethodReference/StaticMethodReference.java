/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MethodReference;

interface Sayable {

    void Say();
}

/**
 *
 * @author Administrator
 */
public class StaticMethodReference {

    public static void SaySomething() {
        System.out.println("Saying Something..........");
    }

    public static void main(String[] args) {

        Sayable s = StaticMethodReference::SaySomething;

        s.Say();

    }

}
