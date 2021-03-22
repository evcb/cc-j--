//ok for parsing and scanning but not the rest, that's why it's commented

package pass;

import java.lang.System;
import java.io.File;

public class Doubles {

    double dblf = 2.1;

    static double staticdblf = 2.2;

    public void testMethod() {

        double[] dbls = { 2.3, 2.4 };
        double dbl = 2.5;

        int one = 1;
        one += 2;
        dbl += 2.6;
        dbls[1] += dbl;
        dblf += dbl + 2.7;
        staticdblf += dbl;
    }

    public static void main(String[] args) {
        new Doubles().testMethod();
    }

}
