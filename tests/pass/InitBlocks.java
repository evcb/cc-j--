package pass;

import java.lang.System;

public class InitBlocks {
    static int c;
    // Under the hood
    // static int c = 2;
    // static int m = 1;
    
    // static int c;
    // static {
    //     c = 2;
    //     m = 1;
    // }
    int y;

    // Static initialization block

    static {   // class declaration - class context and not method
        int x = 24;
        int z = 0;
        x = z;
        c = x + 31;
    }

    // Instance initialization block

    { // seen as a kinda constructor by compiler
        y = 11;
        System.out.println(y);
    }

    public static int tryBlocks() {
        System.out.println(c);
        return c;
    }

    public static void main(String[] args) {
        System.out.println(c);
        System.out.println(tryBlocks());
        new InitBlocks(); // 7 should be printed right after
    }
}
