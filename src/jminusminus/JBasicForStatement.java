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

    private LocalContext context;
    
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
	// Create local context for for-loop
	this.context = new LocalContext(context);

	// Analyse forInt
	for (int i = 0; i < forInt.size(); i++) {
	    forInt.set(i, (JStatement) forInt.get(i).analyze(this.context));
	}

	// Analyse condition (must be boolean return type)
	if (expression != null) {
	    expression = (JExpression) expression.analyze(this.context);
	    expression.type().mustMatchExpected(line(), Type.BOOLEAN);
	}
	
	// Analyse forUpdate
	for (int i = 0; i < forUpdate.size(); i++) {
	    forUpdate.set(i, (JStatement) forUpdate.get(i).analyze(this.context));
	}

	// Analyse for-loop body
	statement = (JStatement) statement.analyze(this.context);
	
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
	String loopLabel = output.createLabel();
	String endLabel = output.createLabel();
	
	for (JStatement s : forInt) {
	    s.codegen(output);
	}

	output.addLabel(loopLabel);
	if (expression != null) {
	    expression.codegen(output, endLabel, false);
	}
	    
	// Evaluate loop body
	statement.codegen(output);

	// If statement has been excecuted correctly
	// evaluate the forUpdate statements...
	for (JStatement s : forUpdate) {
	    s.codegen(output);
	}

	// ...and perform another iteration.
	output.addBranchInstruction(GOTO, loopLabel);
	output.addLabel(endLabel);
	
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
