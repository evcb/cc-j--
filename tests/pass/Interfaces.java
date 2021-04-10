package pass;

import java.lang.System;

interface Calculation {
    int NUMBER = 45;
    public int calc(int n);
    public int calc2(int n);
}

interface OtherCalculation {
    public int calc3(int n);
}

public class Interfaces implements Calculation, OtherCalculation {

    public int calc(int n) {
        return n;
    }

    public int calc2(int n){
        return n*2;
    }

    public int calc3(int n) {
        return n*3;
    }
}
