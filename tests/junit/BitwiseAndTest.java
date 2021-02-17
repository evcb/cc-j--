package junit;

import junit.framework.Assert;
import junit.framework.TestCase;
import pass.BitwiseAnd;

public class BitwiseAndTest extends TestCase {
    private BitwiseAnd bitwiseAnd;

    protected void setUp() throws Exception {
        super.setUp();
        bitwiseAnd = new BitwiseAnd();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testBitwiseAnd() {
        Assert.assertEquals(bitwiseAnd.bitwiseAnd(5, 7), 5);
        Assert.assertEquals(bitwiseAnd.bitwiseAnd(5, 9), 1);
        Assert.assertEquals(bitwiseAnd.bitwiseAnd(12, 55), 4);
    }
}