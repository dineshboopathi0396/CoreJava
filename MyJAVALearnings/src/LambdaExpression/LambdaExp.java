/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LambdaExpression;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
interface LambdaDraw {

    public void Draw();
}

@FunctionalInterface
interface LambdaAdd {

    public int add(int a, int b);
}

/**
 *
 * @author Administrator
 */
public class LambdaExp {

    public static void main(String args[]) {
        LambdaDraw D1 = () -> {
            System.out.println("Test Lambda......");
        };
        D1.Draw();

        LambdaAdd A1 = (a, b) -> (a + b);

        System.out.println(A1.add(12, 20));

        List<String> list = new ArrayList<String>();
        list.add("ankit");
        list.add("mayank");
        list.add("irfan");
        list.add("jai");

        list.forEach(
                (n) -> {
                    System.out.println(n);
                }
        );
    }

}
