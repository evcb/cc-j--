package junit;

import junit.framework.Assert;
import junit.framework.TestCase;
import pass.InitBlocks;

public class InitBlockTest extends TestCase {
    private InitBlocks blk;

    protected void setUp() throws Exception {
        super.setUp();
        blk = new InitBlocks();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testInitBlocks() {
        // TODO: Change after implementation of static and inst. blocks
        Assert.assertEquals(blk.tryBlocks(), 0);
    }
}