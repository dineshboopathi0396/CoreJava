/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Aggregation;

class Address {

    String city;
    String state;
    String country;

    public Address(String city, String state, String country) {
        this.city = city;
        this.state = state;
        this.country = country;
    }
}

class Employee {

    int id;
    String name;
    Address address;

    public Employee(int id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public void display() {
        System.out.println("Employee Id : " + id);
        System.out.println("Employee name : " + name);
        System.out.println("Employee address : " + address.city + " " + address.state + " " + address.country);
    }
}

/**
 *
 * @author Administrator
 */
public class AggrigationExample {

    public static void main(String args[]) {
        Address address1 = new Address("Chennai", "TamilNadu", "India");
        Address address2 = new Address("Hosur", "TamilNadu", "India");

        Employee emp1 = new Employee(1, "Dinesh", address1);
        Employee emp2 = new Employee(2, "Vinith", address2);

        emp1.display();
        emp2.display();
    }

}
