/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inheritancesample;

class ParentClass {

    float value = 25;

    public float Square() {
        return value * value;
    }
}

/**
 *
 * @author Administrator
 */
public class InheritanceSample extends ParentClass {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        InheritanceSample Obj = new InheritanceSample();
        
        System.out.println(Obj.value);
        System.out.println(Obj.Square());
    }

}
