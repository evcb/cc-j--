/*package pass;

import java.lang.System;

//just methods or fields
public interface Interfaces {
}

public class ATest implements Interfaces{
}
*/
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