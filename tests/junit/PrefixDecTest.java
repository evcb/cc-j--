package junit;

import pass.PrefixDec;
import junit.framework.TestCase;

public class PrefixDecTest extends TestCase {
    private PrefixDec prefixDec;

    protected void setUp() throws Exception {
	super.setUp();
	prefixDec = new PrefixDec();
    }

    protected void tearDown() throws Exception {
	super.tearDown();
    }

    public void testPrefixDec() {
	this.assertEquals(prefixDec.prefixDecrement(1), 0);
	this.assertEquals(prefixDec.prefixDecrement(10), 9);
	this.assertEquals(prefixDec.prefixDecrement(-1), -2);
	this.assertEquals(prefixDec.prefixDecrement(0xFFFF), 0xFFFE);
    }
}
