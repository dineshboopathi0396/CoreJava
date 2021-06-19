/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MethodReference;

interface Messageable {

    Message getMessage();

}

class Message {

    Message() {
        System.out.println("Hi Message from Me........");
    }

}

/**
 *
 * @author Administrator
 */
public class ConstructorMethodReference {

    public static void main(String args[]) {
        Messageable m = Message::new;
        m.getMessage();
    }

}
