package junit;

import junit.framework.TestCase;
import pass.BitwiseOr;

public class BitwiseOrTest extends TestCase {
    private BitwiseOr bitOr;

    protected void setUp() throws Exception {
        super.setUp();
        bitOr = new BitwiseOr();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testBitwiseOr() {
        this.assertEquals(bitOr.bitwiseOr(5, 7), 7);
        this.assertEquals(bitOr.bitwiseOr(3, 2), 3);
        this.assertEquals(bitOr.bitwiseOr(20, 39), 55);
        this.assertEquals(bitOr.bitwiseOr(55, 13), 63);
    }
}
