package pass;

import java.lang.Exception;
import java.lang.Error;

public class Try {
	private void throwing() throws Exception, Error {
		throw new Exception();
	}

	public int try_throw_exception() {
		try {
			throwing();
		} catch (Exception e) {
		} finally {
		}

		return 1;
	}

	public int try_throw_error() {
		try {
			throw new Error();

		} catch (Error e) {
		} catch (Exception e) {
		}

		return 1;
	}

	public int try_nothrow() {
		try {
			throw new Exception();
		} catch (Error | Exception e) {
		}

		return 1;
	}
}
