/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StreamAPIs;

import java.util.stream.Stream;

/**
 *
 * @author Administrator
 */
public class StreamIterating {

    public static void main(String args[]) {
        Stream.iterate(1, element -> element + 1)
                .filter(e -> e % 5 == 0)
                .limit(3)
                .forEach(System.out::println);
    }

}
