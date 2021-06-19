/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LambdaExpression;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 *
 * @author Administrator
 */
public class LambdaEventListener {
    
    public static void main(String[] args){
        JTextField jt = new JTextField();
        jt.setBounds(50, 50, 150, 20);
        JButton jB = new JButton("click");
        jB.setBounds(80, 100, 70, 30);
        
        jB.addActionListener(e -> {jt.setText("Hello Swing");});
        
        JFrame f = new JFrame();
        f.add(jt);
        f.add(jB);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        f.setLayout(null);
        f.setSize(300, 300);
        f.setVisible(true);
        
    }
    
}
