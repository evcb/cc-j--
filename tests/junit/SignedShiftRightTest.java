package junit;

import junit.framework.TestCase;
import pass.SignedShiftRight;

public class SignedShiftRightTest extends TestCase{
    private SignedShiftRight signedShiftRight;

    protected void setUp() throws Exception {
        super.setUp();
        signedShiftRight = new SignedShiftRight();
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }

    public void testSignedShiftRight(){
        this.assertEquals(signedShiftRight.shiftRight(0,4), 0);
        this.assertEquals(signedShiftRight.shiftRight(-4,1), -2);
        this.assertEquals(signedShiftRight.shiftRight(127,3), 15);

    }
}