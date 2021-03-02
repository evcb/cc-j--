package junit;

import junit.framework.Assert;
import junit.framework.TestCase;
import pass.Try;

public class TryTest extends TestCase {
	private Try _try;

	protected void setUp() throws Exception {
		super.setUp();
		_try = new Try();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testTry() {
		Assert.assertTrue(_try.try_throw_exception());
		Assert.assertTrue(_try.try_throw_error());
		Assert.assertTrue(_try.try_nothrow());
	}
}