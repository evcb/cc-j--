package pass;

import java.lang.System;

public class InitBlocks {

    static int x;
    static int c;
    int y;

    // Static initialization block
    
    static {
        x = 24;
        c = 31;
    }

    // Instance initialization block

    {
        y = 7;
        System.out.println(y);
    }

    public static int tryBlocks() {
        System.out.println(x);
        // return x + c;
        return x;
    }

    public static void main(String[] args) {
        System.out.println(x);
    }
}
