package junit;

import junit.framework.Assert;
import junit.framework.TestCase;
import pass.StarAssign;

public class StarAssignTest extends TestCase {
	private StarAssign assignments;

	protected void setUp() throws Exception {
		super.setUp();
		assignments = new StarAssign();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testStarAssign() {
		Assert.assertEquals(9, assignments.mul_assign(3, 3));
	}
}
