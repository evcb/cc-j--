package fail;

import java.lang.System;


interface Calculation {
    int NUMBER = 45;
    public int calc(int n);
}

class Calculate implements Calculation {
    int val = 4;
    public int calc2(int n) {
        return n*val;
    }
}


public class Interfaces {
    public static void main(String[] args) {
        int n = 3;
        Calculate calculate = new Calculate();
    }
}
