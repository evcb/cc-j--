// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

/**
 * The AST node for a formal parameter declaration. All analysis and code
 * generation is done in a parent AST.
 */
class JCatchFormalParameter extends JAST {
    /** Parameter name. */
    private String name;

    /** Parameter class types. */
    private ArrayList<TypeName> types;
    private ArrayList<Type> resolvedTypes;

    /**
     * Constructs an AST node for a formal parameter declaration given its line
     * number, name, and type.
     *
     * @param line  line in which the parameter occurs in the source file.
     * @param types parameter types.
     * @param name  parameter name.
     */
    public JCatchFormalParameter(int line, ArrayList<TypeName> types, String name) {
        super(line);
        this.types = types;
        this.name = name;
        resolvedTypes = new ArrayList();
    }

    /**
     * Returns the parameter's name.
     *
     * @return the parameter's name.
     */
    public String name() {
        return name;
    }

    /**
     * Returns the parameter's types.
     *
     * @return the parameter's types.
     */
    public ArrayList<Type> resolvedTypes() {
        return resolvedTypes;
    }

    /**
     * No analysis done here.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    public JAST analyze(Context context) {
        for (TypeName t : types)
            resolvedTypes.add(t.resolve(context));

        return this;
    }

    /**
     * No code generated here.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */
    public void codegen(CLEmitter output) {
        // Nothing to do
    }

    /** {@inheritDoc} */
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JCatchFormalParameter line=\"%d\" name=\"%s\" " + "types=\"%s\"/>\n", line(), name,
                types.toString());
    }
}
