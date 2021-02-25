package junit;

import pass.PostfixInc;
import junit.framework.TestCase;

public class PostfixIncTest extends TestCase {
    private PostfixInc postfixInc;

    protected void setUp() throws Exception {
	super.setUp();
	postfixInc = new PostfixInc();
    }

    protected void tearDown() throws Exception {
	super.tearDown();
    }

    public void testPostfixInc() {
	this.assertEquals(postfixInc.postfixIncrement(1), 2);
	this.assertEquals(postfixInc.postfixIncrement(10), 11);
	this.assertEquals(postfixInc.postfixIncrement(-1), 0);
	this.assertEquals(postfixInc.postfixIncrement(0xFFFE), 0xFFFF);
    }
}
