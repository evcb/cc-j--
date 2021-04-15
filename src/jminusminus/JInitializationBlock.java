package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;

public class JInitializationBlock extends JAST implements JMember {
    
    /** Is this block static? */
    protected boolean isStatic;

    /** Block body. */
    protected JBlock body;

    private ArrayList<String> mods = new ArrayList<String>();

    public JInitializationBlock(int line, boolean isStatic, JBlock body) {
        super(line);
        this.isStatic = isStatic;
        this.body = body;
    }

    public void preAnalyze(Context context, CLEmitter partial) {
        // @TODO: Implementation needed
        return;
    }

    public JAST analyze(Context context) {
        // Analyze all members
        body.analyze(context);
        return this;
    }
    
    public void codegen(CLEmitter output) {
        body.codegen(output);
    }

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JInitializationBlock line=\"%d\">\n", this.line);
        p.indentRight();
        if (isStatic) {
            p.println("<Modifiers>");
            p.indentRight();
            p.printf("<Modifier name=\"static\"/>\n");
            p.indentLeft();
            p.println("</Modifiers>");
        }

        body.writeToStdOut(p);

        p.indentLeft();
        p.printf("</JInitializationBlock>\n");
    }
}
