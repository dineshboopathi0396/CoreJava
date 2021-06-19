/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inheritancesample;

class FinalTest {

    public void function1() {
        System.out.println("Function1 From Final Test");
    }
}

class FinalTest2 extends FinalTest {

    public void function1() {
        System.out.println("Function1 From Final Test2");
    }

    public void function2() {
        System.out.println("Function2");
    }
}

/**
 *
 * @author Administrator
 */
public class SampleInterview {

    public static void main(String[] args) {
        // TODO code application logic here

        FinalTest finalTest = new FinalTest();
        finalTest.function1();
//        finalTest.function2();

        FinalTest finalTest1 = new FinalTest2();
//        finalTest1.function2();
        finalTest1.function1();

        /**
         * FinalTest type do not have a method called function2()
         */
    }

}
