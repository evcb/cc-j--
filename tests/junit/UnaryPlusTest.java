package junit;

import junit.framework.TestCase;
import junit.framework.Assert;
import pass.UnaryPlus;

public class UnaryPlusTest extends TestCase {
	private UnaryPlus unaryPlus;

	protected void setUp() throws Exception {
		super.setUp();
		unaryPlus = new UnaryPlus();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUnaryPlus() {
		int i0 = 20;
		char c0 = 20;
		Assert.assertEquals(i0, unaryPlus.promote(c0));

		int i1 = 0;
		char c1 = 0;
		Assert.assertEquals(i1, unaryPlus.promote(c1));

		int i2 = 5;
		char c2 = 5;
		Assert.assertEquals(i2, unaryPlus.promote(c2));
	}
}
