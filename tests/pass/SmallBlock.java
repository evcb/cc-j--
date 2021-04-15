package pass;
import java.lang.System;

public class SmallBlock {
    static int y;

    public SmallBlock() {
        System.out.println("Constructor!");
    }

    static {
        int x = 1;
        y = x + 1;
    }

    {
        y = y + 2;
    }

    {
        System.out.println("");
    }
}
