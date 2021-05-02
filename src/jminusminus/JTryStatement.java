package jminusminus;

import java.util.ArrayList;
import java.util.Map;

import javax.lang.model.util.ElementScanner6;

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
	private ArrayList<JCatchClause> catchClauses;

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
	public JTryStatement(int line, JBlock tryPart, ArrayList<JCatchClause> catchClauses, JBlock finallyPart) {
		super(line);
		this.tryPart = tryPart;
		this.catchClauses = catchClauses;
		this.finallyPart = finallyPart;
	}

	/**
	 * Analyzing the try-statement means analyzing its components.
	 *
	 * @param context context in which names are resolved.
	 * @return the analyzed (and possibly rewritten) AST subtree.
	 */
	public JStatement analyze(Context context) {
		tryPart = tryPart.analyze(context);

		if (catchClauses != null)
			for (JCatchClause _catch : catchClauses)
				_catch.analyze(context);

		if (finallyPart != null)
			finallyPart = finallyPart.analyze(context);

		return this;
	}

	/**
	 * Code generation for a try-statement.
	 *
	 * @param output the code emitter (basically an abstraction for producing the
	 *               .class file).
	 * @see "Introduction to Compiler Constructionin a Java World" pp. 198-203
	 */
	public void codegen(CLEmitter output) {
		String start = output.createLabel(), end = output.createLabel();

		output.addLabel(start);
		tryPart.codegen(output);
		output.addLabel(end);

		if (catchClauses != null)
			for (JCatchClause _catch : catchClauses)
				for (Type t : _catch.getCatchFormalParameter().resolvedTypes()) {
					String handler = output.createLabel();
					output.addLabel(handler);
					output.addExceptionHandler(start, end, handler, t.jvmName());

					_catch.getBlock().codegen(output);

					if (finallyPart != null)
						finallyPart.codegen(output);
				}
		else
			finallyPart.codegen(output);

	}

	/** {@inheritDoc} */
	public void writeToStdOut(PrettyPrinter p) {
		p.printf("<JTryStatement line=\"%d\">\n", line());

		p.indentRight();
		tryPart.writeToStdOut(p);
		p.indentLeft();

		if (catchClauses != null)
			for (JCatchClause _catch : catchClauses) {
				p.indentRight();
				_catch.writeToStdOut(p);
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
