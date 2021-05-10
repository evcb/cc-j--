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
		char c = 20;
		// Assert.assertEquals(20, unaryPlus.promoteChar(c));

		Assert.assertEquals(0, unaryPlus.promoteInteger(Integer.valueOf(0)));

		char c1 = 5;
		Assert.assertEquals(5, unaryPlus.promoteCharacter(Character.valueOf(c1)));
	}
}
