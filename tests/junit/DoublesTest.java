package junit;

import junit.framework.TestCase;
import pass.Doubles;

public class DoublesTest extends TestCase {
    private Doubles doubles;

    protected void setUp() throws Exception {
        super.setUp();
        doubles = new Doubles();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDoubles() {
        assertEquals(0.25, doubles.doublesWithoutPromotion(3.0));
        assertEquals(0.25, doubles.doublesWithPromotion(3));

    }
}
