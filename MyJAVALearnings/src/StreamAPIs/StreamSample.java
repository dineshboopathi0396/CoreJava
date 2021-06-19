/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StreamAPIs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class StreamProduct {

    int id;
    String Name;
    float Price;

    public StreamProduct(int id, String Name, float Price) {
        this.id = id;
        this.Name = Name;
        this.Price = Price;
    }

    public int getId(Integer id) {
        return this.id;
    }
}

/**
 *
 * @author Administrator
 */
public class StreamSample {

    public static void main(String args[]) {
        List<StreamProduct> Lp = new ArrayList<StreamProduct>();

        Lp.add(new StreamProduct(1, "Iphone", 13000f));
        Lp.add(new StreamProduct(2, "Redmi", 23000f));
        Lp.add(new StreamProduct(3, "RealMe", 19000f));
        Lp.add(new StreamProduct(4, "OnePlus", 9000f));
        Lp.add(new StreamProduct(5, "Nokia", 3000f));

        Lp.stream().filter(p -> p.Price > 4000)
                .forEach(p -> System.out.println(p.Name + " : " + p.Price));

        List<Float> productPriceList = Lp.stream()
                .filter(p -> p.Price > 13000)// filtering data  
                .map(p -> p.Price) // fetching price  
                .collect(Collectors.toList()); // collecting as list  
        System.out.println("productPriceList : " + productPriceList);
        System.out.println("productPriceList : " + Lp.removeIf(lp -> lp.id == 9));

        Lp.removeIf(lp -> lp.id == 5);
        
        Lp.stream().forEach(p -> System.out.println(p.Name + " : " + p.Price));
    }
}
