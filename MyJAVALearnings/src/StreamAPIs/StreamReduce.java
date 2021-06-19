/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StreamAPIs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Administrator
 */
public class StreamReduce {

    public static void main(String args[]) {
        List<StreamProduct> Lp = new ArrayList<StreamProduct>();

        Lp.add(new StreamProduct(1, "Iphone", 13000f));
        Lp.add(new StreamProduct(2, "Redmi", 23000f));
        Lp.add(new StreamProduct(3, "RealMe", 19000f));
        Lp.add(new StreamProduct(4, "OnePlus", 9000f));
        Lp.add(new StreamProduct(5, "ROG", 9000f));
        Lp.add(new StreamProduct(6, "Nokia", 3000f));

        float TotalPrice = Lp.stream()
                .map(product -> product.Price)
                .reduce(0.0f, (sum, price) -> sum + price);
        System.out.println("TotalPrice : " + TotalPrice);

        float TotalPrice1 = Lp.stream()
                .map(product -> product.Price)
                .reduce(0.0f, Float::sum);

        System.out.println("TotalPrice1 : " + TotalPrice1);

        // Using Collectors's method to sum the prices.  
        double totalPrice3 = Lp.stream()
                .collect(Collectors.summingDouble(product -> product.Price));
        System.out.println(totalPrice3);

        //Max
        StreamProduct Min = Lp.stream()
                .min((p1, p2) -> p1.Price > p2.Price ? 1 : -1).get();
        System.out.println("Min : " + Min.Price);

        //Min
        StreamProduct Max = Lp.stream()
                .max((p1, p2) -> p1.Price > p2.Price ? 1 : -1).get();
        System.out.println("Max : " + Max.Price);

        //count() Method in Collection
        long Count = Lp.stream()
                .filter(p -> p.Price < 10000)
                .count();
        System.out.println("Count: " + Count);

        // Converting product List into Set  
        Set<Float> productPriceList = Lp.stream()
                .filter(p -> p.Price < 10000)
                .map(p -> p.Price)
                .collect(Collectors.toSet());
        System.out.println("productPriceSet: " + productPriceList);

        // Converting Product List into a Map  
        Map<Integer, String> productPriceMap = Lp.stream()
                .collect(Collectors.toMap(p -> p.id, p -> p.Name));
        System.out.println("productPriceMap: " + productPriceMap);
    }
}
