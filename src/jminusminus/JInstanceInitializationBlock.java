package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class JInstanceInitializationBlock extends JAST implements JMember {
    
    /** Method modifiers. */
    protected ArrayList<String> mods;

    /** Is this block static? */
    protected boolean isStatic;

    /** Block body. */
    protected JBlock body;

    public JInstanceInitializationBlock(int line, ArrayList<String> mods, JBlock body) {
        super(line);
        this.mods = mods;
        this.isStatic = mods.contains("static");
        this.body = body;
    }

    public void preAnalyze(Context context, CLEmitter partial) {
        // @TODO: Implementation needed
        return;
    }
    
    public void codegen(CLEmitter output) {
        // @TODO: Implementation needed
        return;
    }

    public JAST analyze(Context context) {
        // @TODO: Implementation needed
        return this;
    }

    public void writeToStdOut(PrettyPrinter p) {
        // @TODO: Implementation needed
        return;
    }
}
