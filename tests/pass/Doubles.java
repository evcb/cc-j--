package pass;

public class Doubles {

    double dbl = 6.1;
    static double sdbl = 2.2;

    public double doublesWithoutPromotion(double t) {

        // assignment
        double value = 0.0;

        // binary operations
        // +
        value = value + dbl;
        // -
        value = value - sdbl;
        // divide
        value = value / 2.0;
        // *
        value = value * 3.0;
        // %
        value = value % 3.0;

        // assignment operations
        // +=
        value += 6.0;
        // -=
        value -= 2.0;
        // /=
        value /= 2.0;
        // *=
        value *= 10.0;
        // %=
        value %= 2.0;

        return value;

    }

    public double doublesWithPromotion(int x) {

        // assignment with promotion
        double value = 0;

        // binary operations with promotion
        // +
        value = value + dbl;
        // -
        value = value - sdbl;
        // divide
        value = value / 2;
        // *
        value = value * 3;
        // %
        value = value % x;

        // assignment operations with promotion
        // +=
        value += 6;
        // -=
        value -= 2;
        // /=
        value /= 2;
        // *=
        value *= 10;
        // %=
        value %= 2;

        return value;

    }

}
