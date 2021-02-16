package junit;

import junit.framework.TestCase;
import pass.Division;

public class USignedRightShiftTest extends TestCase {
    private USignedRightShift rightShift;

    protected void setUp() throws Exception {
	super.setUp();
	rightShift = new USignedRightShift();
    }

    protected void tearDown() throws Exception {
	super.tearDown();
    }

    public void testUSignedRightShift() {
	this.assertEquals(rightShift.uRightShift(1,0), 1);
	this.assertEquals(rightShift.uRightShift(1,1), 0);
	this.assertEquals(rightShift.uRightShift(-1,0xF), 131071);
	this.assertEquals(rightShift.uRightShift(-30,-5), 31);
	this.assertEquals(rightShift.uRightShift(0xFF,4), 15);
	this.assertEquals(rightShift.uRightShift(-15,3), 536870910);
    }
}
