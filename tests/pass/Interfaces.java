package pass;

import java.lang.System;

//just methods or fields
interface Calculation {
    int NUMBER = 13242;
    public int calc(int n);
}

class Calculate implements Calculation {
    int val = 4;
    public int calc(int n) {
        return n*10;
    }
}


public class Interfaces {
    public static void main(String[] args) {
        int n = 3;
        Calculate calculate = new Calculate();
        System.out.println("value: " + calculate.calc(n));
    }
}
