/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myworkspace;

/**
 *
 * @author Administrator
 */
public class TimeConversion {

    public static void main(String args[]) {
        String s = "12:40:22AM";
        String arrStr[] = s.split(":");
        if (arrStr[2].contains("AM")) {
            arrStr[2] = arrStr[2].replace("AM", "");
            if (arrStr[0].equalsIgnoreCase("12")) {
                arrStr[0] = "00";
            }
        }
        if (arrStr[2].contains("PM")) {
            arrStr[2] = arrStr[2].replace("PM", "");
            int sample = Integer.parseInt(arrStr[0]) + 12;
//        sample = sample % 24;
            if (sample != 24) {

                arrStr[0] = Integer.toString(sample);
            }
        }
        System.out.println(arrStr[0] + ":" + arrStr[1] + ":" + arrStr[2]);
    }
}
