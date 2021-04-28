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

			return 0;
		} catch (Exception e) {
			return 1;
		} finally {
		}
	}

	public int try_throw_error() {
		try {
			throw new Error();

			return 0;
		} catch (Error e) {
			return 1;
		} catch (Exception e) {
			return 0;
		}

		return 0;
	}

	public int try_nothrow() {
		try {
			throw new Exception();

			return 0;
		} catch (Error | Exception e) {
			return 1;
		}

		return 0;
	}
}
