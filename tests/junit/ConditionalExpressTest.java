package junit;

import junit.framework.TestCase;
import pass.ConditionalExpression;

public class ConditionalExpressTest extends TestCase{
    private ConditionalExpression c;

    protected void setUp() throws Exception{
        super.setUp();
        c = new ConditionalExpression();
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }

    public void testConditionalExpress() {
        this.assertEquals(c.express(true), true);
        this.assertEquals(c.express(false), false);
    }
}
