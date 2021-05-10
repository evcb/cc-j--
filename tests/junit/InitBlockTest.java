package junit;

import junit.framework.Assert;
import junit.framework.TestCase;
import pass.InitBlocks;

public class InitBlockTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        new InitBlocks();
    }

    public void testInitBlocks() {
        Assert.assertEquals(55, InitBlocks.tryBlocks());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}