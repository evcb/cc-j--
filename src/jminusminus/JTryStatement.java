package jminusminus;

import java.util.Map;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a try-statement.
 *
 * TryStatement: - try Block Catches - try Block [Catches] Finally -
 * TryWithResourcesStatement
 *
 * Catches: - CatchClause {CatchClause}
 *
 * CatchClause: - catch ( CatchFormalParameter ) Block
 *
 * CatchFormalParameter: - {VariableModifier} CatchType VariableDeclaratorId
 *
 * CatchType: - UnannClassType {| ClassType}
 *
 * Finally: - finally Block
 *
 * @see https://docs.oracle.com/javase/specs/jls/se7/html/jls-14.html#jls-14.20
 */
public class JTryStatement extends JStatement {
	/** Try clause. */
	private JBlock tryPart;

	/** Catch clauses. */
	private Map<JCatchFormalParameter, JBlock> catchPart;

	/** Finally clause. */
	private JBlock finallyPart;

	/**
	 * Constructs an AST node for a try-statement given its line number, the test
	 * expression, the consequent, and the alternate.
	 *
	 * @param line        line in which the try-statement occurs in the source file.
	 * @param tryPart     try clause.
	 * @param catchPart   catch clauses.
	 * @param finallyPart finally clause.
	 */
	public JTryStatement(int line, JBlock tryPart, Map<JCatchFormalParameter, JBlock> catchPart, JBlock finallyPart) {
		super(line);
		this.tryPart = tryPart;
		this.catchPart = catchPart;
		this.finallyPart = finallyPart;
	}

	/**
	 * Analyzing the try-statement means analyzing its components.
	 *
	 * @param context context in which names are resolved.
	 * @return the analyzed (and possibly rewritten) AST subtree.
	 */
	public JStatement analyze(Context context) {
		tryPart = (JBlock) tryPart.analyze(context);

		for (Map.Entry<JCatchFormalParameter, JBlock> _catch : catchPart.entrySet()) {
			for (Type t : _catch.getKey().types())
				context.addExceptionType(t);

			_catch.getValue().analyze(context);
		}

		if (finallyPart != null)
			finallyPart = (JBlock) finallyPart.analyze(context);

		return this;
	}

	/**
	 * Code generation for a try-statement.
	 *
	 * @param output the code emitter (basically an abstraction for producing the
	 *               .class file).
	 */
	public void codegen(CLEmitter output) {
		// CLEmitter + 198-203 in the book
		/*
		 * String elseLabel = output.createLabel(); String endLabel =
		 * output.createLabel(); condition.codegen(output, elseLabel, false);
		 * thenPart.codegen(output); if (elsePart != null) {
		 * output.addBranchInstruction(GOTO, endLabel); } output.addLabel(elseLabel); if
		 * (elsePart != null) { elsePart.codegen(output); output.addLabel(endLabel); }
		 */
	}

	/** {@inheritDoc} */
	public void writeToStdOut(PrettyPrinter p) {
		p.printf("<JTryStatement line=\"%d\">\n", line());

		p.indentRight();
		tryPart.writeToStdOut(p);
		p.indentLeft();

		for (Map.Entry<JCatchFormalParameter, JBlock> _catch : catchPart.entrySet()) {
			p.indentRight();
			p.printf("<CatchClause>\n");

			p.indentRight();
			_catch.getKey().writeToStdOut(p);
			p.indentLeft();

			p.indentRight();
			_catch.getValue().writeToStdOut(p);
			p.indentLeft();

			p.printf("</CatchClause>\n");
			p.indentLeft();
		}

		if (finallyPart != null) {
			p.printf("<FinallyClause>\n");
			p.indentRight();
			finallyPart.writeToStdOut(p);
			p.indentLeft();
			p.printf("</FinallyClause>\n");
		}
		p.indentLeft();

		p.printf("</JTryStatement>\n");
	}
}
