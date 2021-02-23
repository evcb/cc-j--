package junit;

import junit.framework.Assert;
import junit.framework.TestCase;
import pass.SlashAssign;

public class SlashAssignTest extends TestCase {
	private SlashAssign assignments;

	protected void setUp() throws Exception {
		super.setUp();
		assignments = new SlashAssign();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSlashAssign() {
		Assert.assertEquals(1, assignments.div_assign(4, 4));
	}
}
