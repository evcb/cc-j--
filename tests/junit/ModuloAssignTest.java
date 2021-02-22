package junit;

import junit.framework.Assert;
import junit.framework.TestCase;
import pass.ModuloAssign;

public class ModuloAssignTest extends TestCase {
	private ModuloAssign assignments;

	protected void setUp() throws Exception {
		super.setUp();
		assignments = new ModuloAssign();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testModuloAssign() {
		Assert.assertEquals(1, assignments.mod_assign(5, 2));
	}
}
