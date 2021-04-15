package junit;

import junit.framework.TestCase;
import pass.Interfaces;

public class InterfacesTest extends TestCase {
    private Interfaces interfaces;

    protected void setUp() throws Exception{
        super.setUp();
        interfaces = new Interfaces();
    }

    protected void tearDown() throws Exception{
        super.tearDown();
    }

    public void testInterfaces(){
        this.assertEquals(interfaces.calc(2),5);
        this.assertEquals(interfaces.calc2(2),4);
        this.assertEquals(interfaces.calc3(2),6);
    }
}
