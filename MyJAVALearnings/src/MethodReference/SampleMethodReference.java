/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MethodReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Product {

    private int id;
    private String name;

    public Product(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

/**
 *
 * @author Administrator
 */
public class SampleMethodReference {

    public int compareByName(Product a, Product b) {
        return a.getName().compareTo(b.getName());

    }

    public static void main(String args[]) {

        SampleMethodReference sm = new SampleMethodReference();

        List<String> ListNames = new ArrayList<>();
        List<Product> ProductList = new ArrayList<>();
        ListNames.add("Dinesh");
        ListNames.add("Manoj");
        ListNames.add("Sandy");
        ListNames.add("Vinith");
        ListNames.add("Divya");

        Collections.sort(ListNames, String::compareToIgnoreCase);
        ListNames.forEach(System.out::println);

        ProductList.add(new Product(1, "OnePlus"));
        ProductList.add(new Product(2, "Iphone"));
        ProductList.add(new Product(3, "Samsung"));
        ProductList.add(new Product(4, "HTC"));
        ProductList.add(new Product(5, "Nokia"));
        ProductList.add(new Product(6, "Remi"));
        ProductList.add(new Product(7, "Oppo"));
        ProductList.add(new Product(8, "Realme"));

//        ProductList.sort((a, b) -> a.getName().compareTo(b.getName()));
//        ProductList.sort(Comparator.comparing(Product::getName));
        ProductList.sort(sm::compareByName);

        ProductList.stream().map(pl -> pl.getName()).forEach(System.out::println);
    }

}
