package jminusminus;

import java.util.ArrayList;

/**
 * An interface declaration has a list of modifiers, a name, a list of extended interfaces and an
 * interface block; it has instance fields, and it defines a type. It also introduces its own
 * (interface) context.
 *
 * @see ClassContext
 */


public class JInterfaceDeclaration extends JAST implements JTypeDecl, JMember {

    /** Inteface modifiers. */
    private ArrayList<String> mods;

    /** Interface name. */
    private String name;

    /** Interface block. */
    private ArrayList<JMember> interfaceBlock;

    /** This interface type. */
    private Type thisType;

    /** Interfaces extended. */
    private ArrayList<TypeName> interfacesExtended;

    /** Context for this interface. */
    private ClassContext context;

    /** Whether this interface has an explicit constructor. */
    private boolean hasExplicitConstructor;

    /** Instance fields of this class. */
    private ArrayList<JFieldDeclaration> instanceFieldInitializations;

    /**
     * Constructs an AST node for an interface declaration given the line number, list
     * of interface modifiers, name of the interface, the interfaces it extends, and the
     * interface block.
     *
     * @param line
     *            line in which the class declaration occurs in the source file.
     * @param mods
     *            class modifiers.
     * @param name
     *            class name.
     * @param interfacesExtended
     *            list of interfaces extended.
     * @param interfaceBlock
     *            inteface block.
     */
    public JInterfaceDeclaration(int line, ArrayList<String> mods, String name,
                                 ArrayList<TypeName> interfacesExtended, ArrayList<JMember> interfaceBlock){
        super(line);
        this.mods=mods;
        this.name=name;
        this.interfacesExtended=interfacesExtended;
        this.interfaceBlock = interfaceBlock;
        hasExplicitConstructor = false;
        instanceFieldInitializations = new ArrayList<JFieldDeclaration>();
    }

    @Override
    public JAST analyze(Context context) {
        //TODO: complete method, this is inspired by JClassDeclaration

        // Analyze all members
        for (JMember member : interfaceBlock) {
            ((JAST) member).analyze(this.context);
        }

        // Copy declared fields for purposes of initialization.
        for (JMember member : interfaceBlock) {
            if (member instanceof JFieldDeclaration) {
                JFieldDeclaration fieldDecl = (JFieldDeclaration) member;
                instanceFieldInitializations.add(fieldDecl);
            }
        }
        return this;
    }

    @Override
    public void codegen(CLEmitter output) {
        //TODO: complete method

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JInterfaceDeclaration line=\"%d\" name=\"%s\">\n", line(), name);
        p.indentRight();
        if (context != null) {
            context.writeToStdOut(p);
        }
        if (mods != null) {
            p.println("<Modifiers>");
            p.indentRight();
            for (String mod : mods) {
                p.printf("<Modifier name=\"%s\"/>\n", mod);
            }
            p.indentLeft();
            p.println("</Modifiers>");
        }

        if (interfacesExtended != null) {
            p.println("<Extends>");
            p.indentRight();
            for (TypeName interfaceExtended : interfacesExtended) {
                p.printf("<Extends name=\"%s\"/>\n", interfaceExtended.toString());
            }
            p.indentLeft();
            p.println("</Extends>");
        }

        if (interfaceBlock != null) {
            p.println("<InterfaceBlock>");
            p.indentRight();
            for (JMember member : interfaceBlock) {
                ((JAST) member).writeToStdOut(p);
            }
            p.indentLeft();
            p.println("</InterfaceBlock>");
        }
        p.indentLeft();
        p.println("</JInterfaceDeclaration>");

    }

    @Override
    public void declareThisType(Context context) {
        //TODO: complete method

    }

    @Override
    public void preAnalyze(Context context) {
        //TODO: complete method

    }

    @Override
    public void preAnalyze(Context context, CLEmitter clEmitter) {
        //TODO: complete method for interface

    }

    /**
     * Returns the interface name.
     *
     * @return the interface name.
     */

    @Override
    public String name() {
        return name;
    }

    //there's no super type for interfaces, but mandatory to override
    @Override
    public Type superType() {
        return null;
    }

    /**
     * Returns the type that this interface declaration defines.
     *
     * @return the defined type.
     */

    @Override
    public Type thisType() {
        return thisType;
    }

    /**
     * Returns the initializations for instance fields (now expressed as
     * assignment statements).
     *
     * @return the field declarations having initializations.
     */

    public ArrayList<JFieldDeclaration> instanceFieldInitializations() {
        return instanceFieldInitializations;
    }
}
