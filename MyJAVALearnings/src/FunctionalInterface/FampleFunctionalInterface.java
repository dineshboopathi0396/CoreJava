/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FunctionalInterface;

@FunctionalInterface
interface Sampleinterface {

    void printString();

    default void printInt() {
        System.out.println("1234..........");
    }
}

/**
 *
 * @author Administrator
 */
public class FampleFunctionalInterface implements Sampleinterface {

    @Override
    public void printString() {
        System.out.println("Print test data...........");
    }

    public static void main(String[] args) {
        FampleFunctionalInterface sfi = new FampleFunctionalInterface();

        sfi.printString();
        sfi.printInt();
    }

}
