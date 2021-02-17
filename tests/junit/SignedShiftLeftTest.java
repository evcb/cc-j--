package junit;

import junit.framework.TestCase;
import pass.SignedShiftLeft;

public class SignedShiftLeftTest extends TestCase{
    private SignedShiftLeft signedShiftLeft;

    protected void setUp() throws Exception {
        super.setUp();
        signedShiftLeft = new SignedShiftLeft();
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }

    public void testSignedShiftLeft(){
        this.assertEquals(signedShiftLeft.shiftLeft(0,4), 0);
        this.assertEquals(signedShiftLeft.shiftLeft(-4,1), -8);
        this.assertEquals(signedShiftLeft.shiftLeft(127,5), 4064);

    }
}