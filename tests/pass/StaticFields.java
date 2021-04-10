package pass;

import java.lang.System;

class AField {
    static int two =2;
}

public class StaticFields {
    public static void main(String[] args){
       // AField aField = new AField();
        int result = AField.two*2;
        System.out.println("val : " + result);
    }
}