/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MethodReference;

@FunctionalInterface
interface Sayable1 {

    void say();
}

/**
 *
 * @author Administrator
 */
public class InstanceMethodReference {

    public void SaySomething() {
        System.out.println("Hi instance Reference........");
    }

    public static void main(String[] args) {
        InstanceMethodReference ir = new InstanceMethodReference();

        Sayable1 s = ir::SaySomething;

        s.say();
    }

}
