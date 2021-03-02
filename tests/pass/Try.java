package pass;

public class Try {
	private void throwing() throws Exception, Error {
		throw new Exception();
	}

	public boolean try_throw_exception() {
		try {
			throwing();
		} catch (Exception e) {
		} finally {
		}

		return true;
	}

	public boolean try_throw_error() {
		try {
			throw new Error();

		} catch (Error e) {
		} catch (Exception e) {
		}

		return true;
	}

	public boolean try_nothrow() {
		try {
			throw new Exception();
		} catch (Error | Exception e) {
		}

		return true;
	}
}
