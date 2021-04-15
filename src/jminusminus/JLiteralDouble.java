package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for an {@code double} literal.
 */

public class JLiteralDouble extends JExpression {

    /** String representation of the double. */
    private String text;

    /**
     * Constructs an AST node for an {@code double} literal given its line number
     * and string representation.
     *
     * @param line
     *            line in which the literal occurs in the source file.
     * @param text
     *            string representation of the literal.
     */
    public JLiteralDouble(int line, String text){
        super(line);
        this.text=text;
    }

    /**
     * Analyzing a double literal is trivial.
     *
     * @param context
     *            context in which names are resolved (ignored here).
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        type = Type.DOUBLE;
        return this;
    }

    /**
     * Generating code for a double literal means generating code to push it onto
     * the stack.
     *
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */
    public void codegen(CLEmitter output) {
        double d = Double.parseDouble(text);

        //don't really know what these cases do..
        //they just existed in the CLConstants file so I added them
        //to have something similar to int
        if(d==0){
            output.addNoArgInstruction(DCONST_0);
        } else if (d==1){
            output.addNoArgInstruction(DCONST_1);
        } else {
            output.addLDCInstruction(d);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JLiteralDouble line=\"%d\" type=\"%s\" " + "value=\"%s\"/>\n",
                line(), ((type == null) ? "" : type.toString()), text);
    }
}


