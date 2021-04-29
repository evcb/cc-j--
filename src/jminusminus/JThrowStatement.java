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
	private Type type;

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

	public boolean isUncheckedException() {
		Class<?> _class = expr.getClass();

		return Error.class.isAssignableFrom(_class) || RuntimeException.class.isAssignableFrom(_class);
	}

	/**
	 * The type of the Expression is an unchecked exception class (§11.1.1) or the
	 * null type (§4.1).
	 *
	 * @param exceptionType thrown expression type.
	 */
	public boolean isUncheckedOrNull(Type exceptionType) {
		return expr.type() != Type.NULLTYPE || isUncheckedException();
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
	public boolean isCatched(Type exceptionType, Context context) {
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
	public boolean isPresentInThrowsClause(Type exceptionType, Context context) {
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
		type = expr.type().resolve(context);

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

			if (type != Type.NULLTYPE && !Throwable.class.isAssignableFrom(type.classRep()))
				JAST.compilationUnit.reportSemanticError(line(), "must throw a Throwable or the null reference");
		}

		/*
		 * SECOND CHECK
		 *
		 * At least one of the following three conditions must be true, or a
		 * compile-time error occurs:
		 */
		if (!isUncheckedOrNull(type) && !isCatched(type, context) && !isPresentInThrowsClause(type, context))
			JAST.compilationUnit.reportSemanticError(line(),
					"the throwed type must be either unchecked (subtypes of java.lang.RuntimeException and java.lang.Error), catched by an enclosing try statement or listed in the throws clause of the method/constructor declaration");

		/**
		 * THIRD CHECK
		 *
		 * If a throw statement is contained in a static initializer (§8.7), then a
		 * compile-time check (§11.2.3) ensures that either its value is always an
		 * unchecked exception or its value is always caught by some try statement that
		 * contains it.
		 */
		if (context.methodContext().isStatic())
			if (isUncheckedException() || context.catchesException(type))
				JAST.compilationUnit.reportSemanticError(line(),
						"If a throw statement is contained in a static initializer (§8.7), then a compile-time check (§11.2.3) ensures that either its value is always an unchecked exception or its value is always caught by some try statement that contains it.");

		/*
		 * FOURTH CHECK
		 *
		 * If a throw statement is contained in an instance initializer (§8.6), then a
		 * compile-time check (§11.2.3) ensures that either its value is always an
		 * unchecked exception or its value is always caught by some try statement that
		 * contains it, or the type of the thrown exception (or one of its superclasses)
		 * occurs in the throws clause of every constructor of the class.
		 */

		if (context.methodContext().isConstructor())
			if (isUncheckedException() || context.catchesException(type))
				for (JConstructorDeclaration c : context.classContext().getConstructors())
					if (!c.context.catchesException(type))
						JAST.compilationUnit.reportSemanticError(line(),
								"If a throw statement is contained in an instance initializer (§8.6), then a compile-time check (§11.2.3) ensures that either its value is always an unchecked exception or its value is always caught by some try statement that contains it, or the type of the thrown exception (or one of its superclasses) occurs in the throws clause of every constructor of the class.");

		return this;
	}

	/**
	 * Code generation for a throw statement. Constructs, initializes, and throws
	 * the exception object.
	 *
	 * @param output the code emitter (basically an abstraction for producing the
	 *               .class file).
	 */
	public void codegen(CLEmitter output) {
		expr.codegen(output);

		// construct
		output.addReferenceInstruction(NEW, type.jvmName());
		output.addNoArgInstruction(DUP);
		// initialize
		output.addMemberAccessInstruction(INVOKESPECIAL, type.jvmName(), "<init>", "()V");
		// throw
		output.addNoArgInstruction(ATHROW);
	}

	/** {@inheritDoc} */
	public void writeToStdOut(PrettyPrinter p) {
		p.printf("<JThrowStatement line=\"%d\">\n", line());
		p.indentRight();
		p.printf("<Type=\"" + type.toDescriptor() + "\">");
		expr.writeToStdOut(p);
		p.indentLeft();
		p.printf("</JThrowStatement>\n");
	}
}
