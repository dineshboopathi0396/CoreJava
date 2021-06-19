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

class Product {

    int id;
    String name;
    float price;

    public Product(int id, String name, float price) {
        super();
        this.id = id;
        this.name = name;
        this.price = price;
    }
}

/**
 *
 * @author Administrator
 */
public class LambdaComparator {

    public static void main(String[] args) {
        List<Product> Lp = new ArrayList<Product>();

        Lp.add(new Product(1, "Dinesh", 13000f));
        Lp.add(new Product(1, "Sandhiya", 23000f));
        Lp.add(new Product(1, "Vinith", 19000f));
        Lp.add(new Product(1, "Manoj", 9000f));

        Collections.sort(Lp, (p1, p2) -> {
            return p1.name.compareTo(p2.name);
        });

        System.out.println("Lp : " + Lp);
        for (Product p : Lp) {
            System.out.println("Id : " + p.id + " Name : " + p.name + " Salary : " + p.price);
        }
        
    }

}
