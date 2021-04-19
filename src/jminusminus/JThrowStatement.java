package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a throw-statement. If the enclosing method is non-void, then
 * there is a value to return, so we keep track of the expression denoting that
 * value and its type.
 */
class JThrowStatement extends JStatement {
	/** The throwed expression. */
	private JExpression expr;

	/**
	 * Constructs an AST node for a throw-statement given its line number, and the
	 * expression that is returned.
	 *
	 * @param line line in which the return-statement appears in the source file.
	 * @param expr the throwed expression.
	 */
	public JThrowStatement(int line, JExpression expr) {
		super(line);
		this.expr = expr;
	}

	/**
	 * The type of the Expression is an unchecked exception class (§11.1.1) or the
	 * null type (§4.1).
	 *
	 * @param exceptionType thrown expression type.
	 */
	public boolean isUncheckedOrNull(Type exceptionType) {
		return (expr.type() != Type.NULLTYPE && !Error.class.isAssignableFrom(expr.getClass())
				&& !RuntimeException.class.isAssignableFrom(expr.getClass()));
	}

	/**
	 * The throw statement is contained in the try block of a try statement (§14.20)
	 * and it is not the case that the try statement can throw an exception of the
	 * type of the Expression. (In this case we say the thrown value is caught by
	 * the try statement.)
	 *
	 * @param exceptionType thrown expression type.
	 * @param context       context.
	 */
	private boolean isCatched(Type exceptionType, Context context) {
		return context.catchesException(exceptionType);
	}

	/**
	 * The throw statement is contained in a method or constructor declaration and
	 * the type of the Expression is assignable (§5.2) to at least one type listed
	 * in the throws clause (§8.4.6, §8.8.5) of the declaration.
	 *
	 * @param exceptionType thrown expression type.
	 * @param context       context.
	 */
	private boolean isPresentInThrowsClause(Type exceptionType, Context context) {
		return context.methodContext() != null || context.methodContext().throwsType(exceptionType);
	}

	/**
	 * ...
	 *
	 * @param context context in which names are resolved.
	 * @return the analyzed (and possibly rewritten) AST subtree.
	 * @see https://docs.oracle.com/javase/specs/jls/se7/html/jls-14.html#jls-14.18
	 */
	public JStatement analyze(Context context) {
		/*
		 * FIRST CHECK
		 *
		 * The Expression in a throw statement must denote either 1) a variable or value
		 * of a reference type which is assignable (§5.2) to the type Throwable, or 2)
		 * the null reference, or a compile-time error occurs.
		 */
		if (expr == null) {
			JAST.compilationUnit.reportSemanticError(line(), "must throw an expression");
		} else {
			expr = expr.analyze(context);

			if (expr.type() != Type.NULLTYPE && !Throwable.class.isAssignableFrom(expr.getClass()))
				JAST.compilationUnit.reportSemanticError(line(), "must throw a Throwable or the null reference");
		}

		Type expr_t = expr.type();

		/*
		 * SECOND CHECK
		 *
		 * At least one of the following three conditions must be true, or a
		 * compile-time error occurs:
		 */
		if (!isUncheckedOrNull(expr_t) && !isCatched(expr_t, context) && !isPresentInThrowsClause(expr_t, context))
			JAST.compilationUnit.reportSemanticError(line(),
					"the throwed type must be either unchecked (subtypes of java.lang.RuntimeException and java.lang.Error), catched by an enclosing try statement or listed in the throws clause of the method/constructor declaration");

		/**
		 * THIRD CHECK
		 *
		 * If a throw statement is contained in a static initializer (§8.7), then a
		 * compile-time check (§11.2.3) ensures that either its value is always an
		 * unchecked exception or its value is always caught by some try statement that
		 * contains it. If at run time, despite this check, the value is not caught by
		 * some try statement that contains the throw statement, then the value is
		 * rethrown if it is an instance of class Error or one of its subclasses;
		 * otherwise, it is wrapped in an ExceptionInInitializerError object, which is
		 * then thrown (§12.4.2).
		 *
		 * If a throw statement is contained in an instance initializer (§8.6), then a
		 * compile-time check (§11.2.3) ensures that either its value is always an
		 * unchecked exception or its value is always caught by some try statement that
		 * contains it, or the type of the thrown exception (or one of its superclasses)
		 * occurs in the throws clause of every constructor of the class.
		 */

		return this;
	}

	/**
	 * ...
	 *
	 * @param output the code emitter (basically an abstraction for producing the
	 *               .class file).
	 */
	public void codegen(CLEmitter output) {
		// https://docs.oracle.com/javase/specs/jls/se7/html/jls-11.html#d5e14192
		expr.codegen(output);
		// check if try block in surrounding context
	}

	/** {@inheritDoc} */
	public void writeToStdOut(PrettyPrinter p) {
		p.printf("<JThrowStatement line=\"%d\">\n", line());
		p.indentRight();
		expr.writeToStdOut(p);
		p.indentLeft();
		p.printf("</JThrowStatement>\n");
	}
}
