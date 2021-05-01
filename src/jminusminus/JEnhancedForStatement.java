package jminusminus;

import static jminusminus.CLConstants.*;
import java.util.ArrayList;

/**
 * The AST node for an enhanced for-statement.
 */

class JEnhancedForStatement extends JStatement {

    /** Iterator type */
    private Type type;

    /** Iterator identifier */
    private String name;

    /** Expression to iterate over */
    private JExpression expression;

    /** For-loop body */
    private JStatement statement;

    private LocalContext context;

    private JVariable iterator;
    
    /**
     * Constructs an AST node for an enhanced-statement given its line number, 
     * iterator, expression, and loop body/
     * 
     * @param line
     *            line in which the enhanced for-statement occurs in the source file.
     * @param type
     *            type of the iterator.
     * @param name
     *            iterator identifier.
     * @param expression
     *            expression to iterate over.
     * @param statement
     *            for-loop body.
     */

    public JEnhancedForStatement(int line, Type type, String name, JExpression expression, JStatement statement) {
        super(line);
        this.type = type;
	this.name = name;
        this.expression = expression;
	this.statement = statement;
	iterator = new JVariable(line, name);
    }

    /**
     * Analyzing the enhanced for-statement means analyzing its components and checking
     * that the test is a boolean.
     * 
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JStatement analyze(Context context) {
	iterator = new JVariable(line, name);
	iterator = (JVariable) iterator.analyze(context);
	expression = (JExpression) expression.analyze(context);
	if (!expression.type().isArray()) {
	    JAST.compilationUnit.reportSemanticError(line(),
	        "expression must be array or iterable");
	} // Must be an array or iterable (iterables not impelmented)
	expression.type().componentType().mustMatchExpected(line(), type);
	statement = (JStatement) statement.analyze(context);
        
	return this;
    }

    /**
     * Code generation for an enhanced for-statement. We generate code to branch over the
     * consequent if !test; the consequent is followed by an unconditonal branch
     * over (any) alternate.
     * 
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
	/*
	 * The Enhanced For-Statement creates a varible with value 0 as the
	 * counter and calculates the length of the array and stores both of
	 * these. At the loop-label they are loaded onto the stack and compared.
	 * If the counter is greater than or equal to the length of the array,
	 * the statement jumps to the end-label and terminates. Otherwise it 
	 * performs one loop iterations (fetching the value at index pointed to by the counter),
	 * executes the statement body and increments the counter, before jumping back
	 * to the loop-label.
	 *
	 */

	// Create local context for for-loop                                                                                 
        //this.context = new LocalContext(context);

	// Create counter variable
	//counter = new JVariable();
	
	// Create labels
        String loopLabel = output.createLabel();
        String endLabel = output.createLabel();

	output.addNoArgInstruction(ICONST_1);
	iterator.codegenStore(output);

	// Start of loop
	output.addLabel(loopLabel); 
	
	// If the end of the array has been reached,
	// terminate the loop
	output.addBranchInstruction(IF_ICMPGE, endLabel);
	
	// Load value from iterable type
	// Can this be done by means of a method or class???
	// if (type == Type.INT) {                                                                                                           output.addNoArgInstruction(IALOAD);                                                                                      } else if (type == Type.BOOLEAN) {                                                                                               output.addNoArgInstruction(BALOAD);                                                                                      } else if (type == Type.CHAR) {                                                                                                  output.addNoArgInstruction(CALOAD);                                                                                      } else if (!type.isPrimitive()) {                                                                                                output.addNoArgInstruction(AALOAD);                                                                                      }

	expression.codegen(output); // Load array value

	// Evaluate loop body                                                                                                
        statement.codegen(output);

	// Preform another iteration
	output.addBranchInstruction(GOTO, loopLabel);                                                                        
        output.addLabel(endLabel); 
	
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
	
	p.printf("<ForIterator line=\"%d\" name=\"%s\" "
		 + "type=\"%s\"/>\n", line(), name,
		 (type == null) ? "" : type.toString());
	       
	p.printf("<JExpression>\n");
	p.indentRight();
	expression.writeToStdOut(p);
	p.indentLeft();
	p.printf("</JExpression>\n");
	
	p.printf("<JStatement>\n");
	p.indentRight();
	statement.writeToStdOut(p);
	p.indentLeft();
	p.printf("</JStatement>\n");
	p.indentLeft();
	p.printf("</JEnhancedForStatement>\n");	
    }
}
