package pass;

import java.lang.System;

//just methods or fields
interface Calculation {
    public int calc(int n);
}

class Calculate implements Calculation {
    public int calc(int n) {
        int val = 2;
        return val*n;
    }
}


public class Interfaces {
    public static void main(String[] args) {
        int n = 3;
        Calculate calculate = new Calculate();
        System.out.println("value: " + calculate.calc(n));
    }
}

/*
public interface Interfaces {
    int aMethod();
}

public class ATest implements Interfaces {
    int aMethod(){
        return 1;
    }
}*/

//OK

/*
public interface Interfaces {
    int aMethod();
}

public interface AClass extends Interfaces{
    void anotherMethod();
}

*/



/*
interface InterfaceDeclaration {
}*/
/*

    public int methodDeclaration();
    abstract int abstractMethodDeclaration(int value);

    class InterfaceImplementation{

        public int attributeDeclaration;
        public int methodDeclaration(){
            return 1;
        }

        public InterfaceImplementation(int value) {
            attributeDeclaration = value;
        }

    }

    interface innerInterface {
        public int innerMethod();

    }

}*/


//-- Interface implementation

/*class InterfaceImplementationTwo implements InterfaceDeclaration {

    public int attributeDeclaration;
    public int methodDeclaration(){
        return 1;
    }

    //@override?
    public int abstractMethodDeclaration(int value) {
        return 0;
    }

    public InterfaceImplementationTwo(int value) {
        attributeDeclaration = value;
    }

}

class ClassExtension extends InterfaceImplementationTwo {

    public ClassExtension(){
        super(3);
    }


}*/