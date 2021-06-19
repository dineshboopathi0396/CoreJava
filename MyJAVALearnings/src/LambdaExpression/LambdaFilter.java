/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LambdaExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class LambdaProduct {

    int id;
    String Name;
    float Price;

    public LambdaProduct(int id, String Name, float Price) {
        this.id = id;
        this.Name = Name;
        this.Price = Price;
    }
}

/**
 *
 * @author Administrator
 */
public class LambdaFilter {

    public static void main(String args[]) {
        List<LambdaProduct> Lp = new ArrayList<LambdaProduct>();

        Lp.add(new LambdaProduct(1, "Iphone", 13000f));
        Lp.add(new LambdaProduct(2, "Redmi", 23000f));
        Lp.add(new LambdaProduct(3, "RealMe", 19000f));
        Lp.add(new LambdaProduct(4, "OnePlus", 9000f));
        Lp.add(new LambdaProduct(5, "Nokia", 3000f));

        Collections.sort(Lp, (p1, p2) -> {
            return p1.Name.compareTo(p2.Name);
        });

        Stream<LambdaProduct> filterData = Lp.stream().filter(p -> p.Price > 4000);

        filterData.forEach(
                product -> System.out.println(product.Name + " : " + product.Price)
        );
    }

}
