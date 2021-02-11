package junit;

import junit.framework.TestCase;
import pass.Remainder;

public class RemainderTest extends TestCase {
    private Remainder remainder;

    protected void setUp() throws Exception{
        super.setUp();
        remainder = new Remainder();
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }

    public void testRemainder(){
        this.assertEquals(remainder.remainder(0, 42),0);
        this.assertEquals(remainder.remainder(42, 1),0);
        this.assertEquals(remainder.remainder(127, 3),1);
    }
}