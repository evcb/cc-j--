package jminusminus;

import static jminusminus.CLConstants.*;
import java.util.ArrayList;

/**
 * The AST node for a basic for-statement.
 */

class JBasicForStatement extends JStatement {

    /** For-loop initialiser list */
    private ArrayList<JStatement> forInt;

    /** For-loop expression */
    private JExpression expression;

    /** For-loop update list*/
    private ArrayList<JStatement> forUpdate;

    /** For-loop body */
    private JStatement statement;
    
    /**
     * Constructs an AST node for a basic-statement given its line number, the 
     * for initialiser, the expression, and the for update.
     * 
     * @param line
     *            line in which the basic for-statement occurs in the source file.
     * @param forInit
     *            for-loop initialiser statement(s).
     * @param expression
     *            test expression.
     * @param forUpdate
     *            for-loop update statement(s).
     * @param statement
     *            for-loop body.
     */

    public JBasicForStatement(int line, ArrayList<JStatement> forInt, JExpression expression, ArrayList<JStatement> forUpdate, JStatement statement) {
        super(line);
        this.forInt = forInt;
        this.expression = expression;
        this.forUpdate = forUpdate;
	this.statement = statement;
    }

    /**
     * Analyzing the basic for-statement means analyzing its components and checking
     * that the test is a boolean.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JStatement analyze(Context context) {
        // condition = (JExpression) condition.analyze(context);
        // condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        // thenPart = (JStatement) thenPart.analyze(context);
        // if (elsePart != null) {
        //     elsePart = (JStatement) elsePart.analyze(context);
        // }
        // return this;

	return this;
    }

    /**
     * Code generation for a basic for-statement. We generate code to branch over the
     * consequent if !test; the consequent is followed by an unconditonal branch
     * over (any) alternate.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        // String elseLabel = output.createLabel();
        // String endLabel = output.createLabel();
        // condition.codegen(output, elseLabel, false);
        // thenPart.codegen(output);
        // if (elsePart != null) {
        //     output.addBranchInstruction(GOTO, endLabel);
        // }
        // output.addLabel(elseLabel);
        // if (elsePart != null) {
        //     elsePart.codegen(output);
        //     output.addLabel(endLabel);
        // }
    }

    /**
     * {@inheritDoc}
     */

    public void writeToStdOut(PrettyPrinter p) {
	p.printf("<JBasicForStatement line=\"%d\">\n", line());
	p.indentRight();
	if (forInt != null) {
	    p.printf("<ForInt>\n");
	    p.indentRight();
	    for (JStatement statement : forInt) {
		p.indentRight();
		statement.writeToStdOut(p);
		p.indentLeft();
	    }
	    p.indentLeft();
	    p.printf("</ForInt>\n");
	}
	if (expression != null) {
	    p.printf("<JExpression>\n");
	    p.indentRight();
	    expression.writeToStdOut(p);
	    p.indentLeft();
	    p.printf("/JExpression\n");
	}
	if (forUpdate != null) {
	    p.printf("<ForUpdate>\n");
	    p.indentRight();
	    for (JStatement statement : forUpdate) {
		p.indentRight();
		statement.writeToStdOut(p);
		p.indentLeft();
	    }
	    p.indentLeft();
	    p.printf("</ForUpdate>\n");
	}
	p.printf("<JStatement>\n");
	p.indentRight();
	statement.writeToStdOut(p);
	p.indentLeft();
	p.printf("</JStatement>\n");
	p.indentLeft();
	p.printf("</JBasicForStatement>\n");	
    }
}
