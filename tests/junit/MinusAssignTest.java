package junit;

import junit.framework.Assert;
import junit.framework.TestCase;
import pass.MinusAssign;

public class MinusAssignTest extends TestCase {
    private MinusAssign assignments;

    protected void setUp() throws Exception {
        super.setUp();
        assignments = new MinusAssign();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testMinusAssign() {
        Assert.assertEquals(-8, assignments.minus_assign(2, 10));
    }
}
