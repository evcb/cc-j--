package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for an {@code double} literal.
 */

public class JLiteralDouble extends JExpression {

    private String text;

    public JLiteralDouble(int line, String text){
        super(line);
        this.text=text;
    }

    public JExpression analyze(Context context) {
        type = Type.DOUBLE;
        return this;
    }


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


