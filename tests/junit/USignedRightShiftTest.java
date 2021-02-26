package junit;

import pass.USignedRightShift;
import junit.framework.TestCase;

public class USignedRightShiftTest extends TestCase {
    private USignedRightShift uSignedShiftRight;

    protected void setUp() throws Exception {
	super.setUp();
	uSignedShiftRight = new USignedRightShift();
    }

    protected void tearDown() throws Exception {
	super.tearDown();
    }

    public void testUSignedRightShift() {
	this.assertEquals(uSignedShiftRight.uRightShift(1,0), 1);
	this.assertEquals(uSignedShiftRight.uRightShift(1,1), 0);
	this.assertEquals(uSignedShiftRight.uRightShift(-1,15), 131071);
	this.assertEquals(uSignedShiftRight.uRightShift(-30,-5), 31);
	this.assertEquals(uSignedShiftRight.uRightShift(255,4), 15);
	this.assertEquals(uSignedShiftRight.uRightShift(-15,3), 536870910);
    }
}
