package junit;

import junit.framework.Assert;
import junit.framework.TestCase;
import pass.ExclusiveOr;

public class ExclusiveOrTest extends TestCase{
    private ExclusiveOr exclusiveOr;
    
    protected void setUp() throws Exception {
        super.setUp();
        exclusiveOr = new ExclusiveOr();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testExclusiveOr() {
        Assert.assertEquals(exclusiveOr.exclusiveOr(10, 8), 2);
        Assert.assertEquals(exclusiveOr.exclusiveOr(15, 8), 7);
        Assert.assertEquals(exclusiveOr.exclusiveOr(5, 25), 28);
    }
}