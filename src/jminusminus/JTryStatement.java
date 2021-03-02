package jminusminus;

import java.util.ArrayList;
import java.util.Map.Entry;

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
 */
public class JTryStatement extends JStatement {
	/** Try clause. */
	private JBlock tryPart;

	/** Catch clauses. */
	private ArrayList<Entry<JCatchFormalParameter, JBlock>> catchPart;

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
	public JTryStatement(int line, JBlock tryPart, ArrayList<Entry<JCatchFormalParameter, JBlock>> catchPart,
			JBlock finallyPart) {
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

		/*
		 * for (Entry<JCatchFormalParameter, JBlock> _catch : catchPart)
		 * catchPart.add((JCatchClause) _catch.analyze(context)); // must be class
		 * Throwable or a subclass of Throwable
		 */

		if (finallyPart != null)
			finallyPart = (JBlock) finallyPart.analyze(context);

		return this;
	}

	/**
	 * Code generation for a try-statement. We generate code to branch over the
	 * consequent if !test; the consequent is followed by an unconditonal branch
	 * over (any) alternate.
	 *
	 * @param output the code emitter (basically an abstraction for producing the
	 *               .class file).
	 */
	public void codegen(CLEmitter output) {
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

		for (Entry<JCatchFormalParameter, JBlock> catchClause : catchPart) {
			p.indentRight();
			p.printf("<CatchClause>\n");

			p.indentRight();
			catchClause.getKey().writeToStdOut(p);
			p.indentLeft();

			p.indentRight();
			catchClause.getValue().writeToStdOut(p);
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
