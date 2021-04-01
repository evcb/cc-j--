package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;

/**
 * An interface declaration has a list of modifiers, a name, a list of extended interfaces and an
 * interface block; it has instance fields, and it defines a type. It also introduces its own
 * (interface) context.
 *
 * @see ClassContext
 */


public class JInterfaceDeclaration extends JAST implements JTypeDecl{

    /** Inteface modifiers. */
    private ArrayList<String> mods;

    /** Interface name. */
    private String name;

    /** Interface block. */
    private ArrayList<JMember> interfaceBlock;

    /** This interface type. */
    private Type thisType;

    /** Interfaces extended. */
    private ArrayList<Type> interfacesExtended;

    /** Interfaces extended jvm names */
    private ArrayList<String> interfacesExtendedNames;

    /** Static (interface) fields of this interface. */
    private ArrayList<JFieldDeclaration> staticFieldInitializations;

    /** Context for this interface. */
    private ClassContext context;

    private Type superType;
    

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
                                 ArrayList<Type> interfacesExtended, ArrayList<JMember> interfaceBlock){
        super(line);
        this.mods=mods;
        this.name=name;
        this.interfacesExtended=interfacesExtended;
        this.interfaceBlock = interfaceBlock;
        this.staticFieldInitializations = new ArrayList<JFieldDeclaration>();

        if(!mods.contains(TokenKind.PUBLIC.image())){
            mods.add(TokenKind.PUBLIC.image());
        }
        if(!mods.contains(TokenKind.ABSTRACT.image())){
            mods.add(TokenKind.ABSTRACT.image());
        }
        this.superType=Type.OBJECT;

        for (Type interfaceExtended : interfacesExtended) {
            interfacesExtendedNames.add(interfaceExtended.jvmName());
        }
    }

    @Override
    public JAST analyze(Context context) {

        // Analyze all members
        for (JMember member : interfaceBlock) {

            ((JAST) member).analyze(this.context);
        }

        // Copy declared fields for purposes of initialization.
        for (JMember member : interfaceBlock) {
            if (member instanceof JFieldDeclaration) {
                JFieldDeclaration fieldDecl = (JFieldDeclaration) member;
                //in the pre-analyze step, we have made sure to add "static" in the modifiers of all field declarations
                staticFieldInitializations.add(fieldDecl);
            }
        }

        return this;
    }

    @Override
    public void codegen(CLEmitter output) {
        // The class header
        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;

        output.addClass(mods, qualifiedName, superType.jvmName(), interfacesExtendedNames, false);


        // The members
        for (JMember member : interfaceBlock) {
            ((JAST) member).codegen(output);
        }

        // Generate a class initialization method?
        if (staticFieldInitializations.size() > 0) {
            codegenInterfaceInit(output);
        }

    }

    /**
     * Generates code for interface initialization, in j-- this means static field
     * initializations.
     *
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    private void codegenInterfaceInit(CLEmitter output) {
        ArrayList<String> mods = new ArrayList<String>();
        mods.add("public");
        mods.add("static");
        output.addMethod(mods, "<clinit>", "()V", null, false);

        // If there are instance initializations, generate code
        // for them
        for (JFieldDeclaration staticField : staticFieldInitializations) {
            staticField.codegenInitializations(output);
        }

        // Return
        output.addNoArgInstruction(RETURN);
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
            for (Type interfaceExtended : interfacesExtended) {
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
        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        CLEmitter partial = new CLEmitter(false);
        partial.addClass(mods, qualifiedName, superType.jvmName(), interfacesExtendedNames, false);
        thisType = Type.typeFor(partial.toClass());
        context.addType(line, thisType);

    }

    @Override
    public void preAnalyze(Context context) {
        // Construct a class context
        this.context = new ClassContext(this, context);

        // Resolve superclass
        superType = superType.resolve(this.context);
        thisType.checkAccess(line, superType);
        if (superType.isFinal()) {
            JAST.compilationUnit.reportSemanticError(line,
                    "Cannot extend a final type: %s", superType.toString());
        }

        // Resolve interfaces implemented
        for (int i=0; i<interfacesExtended.size(); i++){
            interfacesExtended.set(i, interfacesExtended.get(i).resolve(this.context));
        }

        // Creating a partial class in memory can result in a
        // java.lang.VerifyError if the semantics below are
        // violated, so we can't defer these checks to analyze()
        for (Type interfaceExtended: interfacesExtended){
            thisType.checkAccess(line, interfaceExtended);
            if(interfaceExtended.isFinal()){
                JAST.compilationUnit.reportSemanticError(line,
                        "Cannot extend a final type: %s", interfaceExtended.toString());
            }
        }

        // Create the (partial) class
        CLEmitter partial = new CLEmitter(false);


        // Add the class header to the partial class
        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;

        partial.addClass(mods, qualifiedName, superType.jvmName(), interfacesExtendedNames, false);

        // Pre-analyze the members and add them to the partial
        for (JMember member : interfaceBlock) {
            if (member instanceof JMethodDeclaration) {
                ((JMethodDeclaration) member).makeAbstractAndPublic();
            }
            if (member instanceof JFieldDeclaration) {
                ((JFieldDeclaration) member).makeForInterface();
            }
            member.preAnalyze(this.context, partial);
        }


        // Get the Class rep for the (partial) class and make it
        // the
        // representation for this type
        Type id = this.context.lookupType(name);
        if (id != null && !JAST.compilationUnit.errorHasOccurred()) {
            id.setClassRep(partial.toClass());
        }

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

    @Override
    public Type superType() {
        return this.superType;
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


}
