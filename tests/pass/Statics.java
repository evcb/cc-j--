package pass;

public class Statics {

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

    public static void main(String[] args) {
        System.out.println(x);
    }
}
