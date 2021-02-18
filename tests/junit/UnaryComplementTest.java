package junit;

import junit.framework.TestCase;
import pass.UnaryComplement;

public class UnaryComplementTest extends TestCase {
    private UnaryComplement unaryComplement;

    protected void setUp() throws Exception {
        super.setUp();
        unaryComplement = new UnaryComplement();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    } 

    public void testInvert() {
        this.assertEquals(unaryComplement.invert(2), -3);
        this.assertEquals(unaryComplement.invert(100), -101);
        this.assertEquals(unaryComplement.invert(1000100), -1000101);
    }
}
