package jminusminus;

import static jminusminus.CLConstants.*;
import java.util.ArrayList;

/**
 * The AST node for a basic for-statement.
 */

class JEnhancedForStatement extends JStatement {

    private Type type;

    private String name;

    private JExpression expression;

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

    public JEnhancedForStatement(int line, Type type, String name, JExpression expression, JStatement statement) {
        super(line);
        this.type = type;
	this.name = name;
        this.expression = expression;
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
	p.printf("<JEnhancedForStatement line=\"%d\">\n", line());
	p.indentRight();
	
	p.printf("<Local variable line=\"%d\" name=\"%s\" "
		 + " type=\"%s\"/>\n", line(), name,
		 (type == null) ? "" : type.toString());
	       
	p.printf("<expression>\n");
	p.indentRight();
	expression.writeToStdOut(p);
	p.indentLeft();
	p.printf("</expression>\n");
	
	p.printf("<statement>\n");
	p.indentRight();
	statement.writeToStdOut(p);
	p.indentLeft();
	p.printf("</statement>\n");
	p.indentLeft();
	p.printf("</JEnhancedForStatement>\n");	
    }
}
