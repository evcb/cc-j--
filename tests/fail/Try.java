package fail;

import java.lang.System;

public class Try {
	public static void main(String[] args) {
		throws_incorrect_class();
		only_try();
		multiple_finally();
		mix_clauses();
	}

	private static void throws_incorrect_class() throws Integer {
		throw Integer.valueOf(0);
	}

	private static void only_try() {
		try {
			return;
		}
	}

	private static void multiple_finally() {
		try {
			return;
		}
		finally {
			return;
		}
		finally {
			return;
		}
	}

	private static void mix_clauses() {
		try {
			return;
		}
		finally {
			return;
		}
		catch(Exception e) {
			return;
		}
	}
}
