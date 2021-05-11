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

    private JVariableDeclaration varDecs;
    private JVariableDeclarator varDec;
    private JVariable iterator;

    private int offsetCnt, offsetArrLen;
    
    /**
     * Constructs an AST node for an enhanced-statement given its line number,
     * iterator, expression, and loop body/
     *
     * @param line       line in which the enhanced for-statement occurs in the
     *                   source file.
     * @param type       type of the iterator.
     * @param name       iterator identifier.
     * @param expression expression to iterate over.
     * @param statement  for-loop body.
     */

    public JEnhancedForStatement(int line, Type type, String name, JExpression expression, JStatement statement) {
        super(line);
        this.type = type;
        this.name = name;
        this.expression = expression;
	this.statement = statement;
    }

    /**
     * Analyzing the enhanced for-statement means analyzing its components and
     * checking that the test is a boolean.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JStatement analyze(Context context) {
	//System.out.println("Creating iterator, line " + line + " name " + name);
	//iteratorVar = new JVariable(line, name);

	// Set up and allocate space for internal variables
	offsetCnt = ((LocalContext) context).nextOffset();                                                               
	LocalVariableDefn defnCnt = new LocalVariableDefn(Type.INT, offsetCnt);
	context.addEntry(line, "#cnt", defnCnt);
	offsetArrLen = ((LocalContext) context).nextOffset();   
	LocalVariableDefn defnArrLen = new LocalVariableDefn(Type.INT, offsetArrLen);
	context.addEntry(line, "#arrLen", defnArrLen);
	
	//ArrayList<JStatement> init = new ArrayList<JStatement>();
    
	//May have to take into account the type used so that we just don't set
	// it to an integer value... 
	JExpression init = (JExpression )new JLiteralInt(line, "0");
	varDec = new JVariableDeclarator(line, name, type, init);
	ArrayList<JVariableDeclarator> decs = new ArrayList<JVariableDeclarator>();
	decs.add(varDec);
	ArrayList<String> mods = new ArrayList<String>();
	
	varDecs = new JVariableDeclaration(line, mods, decs);
	//iterator = new JVariable(line, name);

	//this.context.addEntry(line, name, new LocalVariableDefn(type, this.context.nextOffset(), null ));
	
	System.out.println("Analyzing iterator...");
	varDecs = (JVariableDeclaration) varDecs.analyze(context);
	iterator = new JVariable(line, name);
	iterator = (JVariable) iterator.analyze(context);	
	//iterator = (JVariableDeclarator) iterator.analyze(context);	
	System.out.println("Iterator analysed!");
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
     * Code generation for an enhanced for-statement. We generate code to branch
     * over the consequent if !test; the consequent is followed by an unconditonal
     * branch over (any) alternate.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
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

	// Create labels
        String loopLabel = output.createLabel();
        String endLabel = output.createLabel();

	// Get and store array length
	expression.codegen(output);
	output.addNoArgInstruction(ARRAYLENGTH);
	codegenLocalStore(output, offsetArrLen);

	// Set counter varible to zero
	output.addNoArgInstruction(ICONST_0);
	codegenLocalStore(output, offsetCnt);
	
	// Start of loop
	output.addLabel(loopLabel); 
	
	// If the end of the array has been reached,
	// terminate the loop
	codegenLocalLoad(output, offsetCnt);
	codegenLocalLoad(output, offsetArrLen);
	output.addBranchInstruction(IF_ICMPGE, endLabel);
	
	// Load offset and get array element at this index
	expression.codegen(output); // Load array reference
	codegenLocalLoad(output, offsetCnt);
	codegenArrLoad(output);
	iterator.codegenStore(output);

	// Evaluate loop body                                                                                                
        statement.codegen(output);

	// Increment counter
	output.addIINCInstruction(offsetCnt, 1);
	
	// Preform another iteration
	output.addBranchInstruction(GOTO, loopLabel);                                                                        
        output.addLabel(endLabel); 
    }

    private void codegenArrLoad(CLEmitter output) {
	if (type == Type.INT) {                                                                                              
            output.addNoArgInstruction(IALOAD);                                                                              
        } else if (type == Type.BOOLEAN) {                                                                                   
            output.addNoArgInstruction(BALOAD);                                                                              
        } else if (type == Type.CHAR) {                                                                                      
            output.addNoArgInstruction(CALOAD);                                                                              
        } else if (!type.isPrimitive()) {                                                                                    
            output.addNoArgInstruction(AALOAD);                                                                              
        }                                                                                                                    
    }

    private void codegenLocalLoad(CLEmitter output, int offset) {
	switch (offset) {                                                                                             
	case 0:                                                                                                      
	    output.addNoArgInstruction(ILOAD_0);                                                                     
	    break;                                                                                                   
	case 1:                                                                                                      
	    output.addNoArgInstruction(ILOAD_1);                                                                     
	    break;                                                                                                   
	case 2:                                                                                                      
	    output.addNoArgInstruction(ILOAD_2);                                                                     
	    break;                                                                                                   
	case 3:                                                                                                    
	    output.addNoArgInstruction(ILOAD_3);                                                                     
	    break;                                                                                                   
	default:                                                                                                     
	    output.addOneArgInstruction(ILOAD, offset);                                                              
	    break;                                                                                                   
	}             
    }

    private void codegenLocalStore(CLEmitter output, int offset) {
	switch (offset) {                                                                                             
	case 0:                                                                                                      
	    output.addNoArgInstruction(ISTORE_0);                                                                     
	    break;                                                                                                   
	case 1:                                                                                                      
	    output.addNoArgInstruction(ISTORE_1);                                                                     
	    break;                                                                                                   
	case 2:                                                                                                      
	    output.addNoArgInstruction(ISTORE_2);                                                                     
	    break;                                                                                                   
	case 3:                                                                                                    
	    output.addNoArgInstruction(ISTORE_3);                                                                     
	    break;                                                                                                   
	default:                                                                                                     
	    output.addOneArgInstruction(ISTORE, offset);                                                              
	    break;                                                                                                   
	}             
    }

    /**
     * {@inheritDoc}
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JEnhancedForStatement line=\"%d\">\n", line());
        p.indentRight();

        p.printf("<ForIterator line=\"%d\" name=\"%s\" " + "type=\"%s\"/>\n", line(), name,
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
