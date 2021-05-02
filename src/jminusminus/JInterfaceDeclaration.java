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

    /** Static (interface) fields of this interface. */
    private ArrayList<JFieldDeclaration> staticFieldInitializations;

    /** Context for this interface. */
    private ClassContext context;

    /** Interfaces extended */
    private ArrayList<Type> superInterfaces;

    /** Interfaces implemented jvm names */
    private ArrayList<String> superInterfacesNames;

    /** Interface supertype */
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
     * @param superType
     *            super interface type.
     * @param interfaceBlock
     *            inteface block.
     */
    public JInterfaceDeclaration(int line, ArrayList<String> mods, String name,
                                 ArrayList<Type> superInterfaces, ArrayList<JMember> interfaceBlock){
        super(line);
        this.mods=mods;
        this.name=name;
        this.interfaceBlock = interfaceBlock;
        this.staticFieldInitializations = new ArrayList<JFieldDeclaration>();
        this.superInterfaces=superInterfaces;
        this.superInterfacesNames = new ArrayList<String>();
        this.superType = Type.OBJECT;

        if(!mods.contains(TokenKind.PUBLIC.image())){
            mods.add(TokenKind.PUBLIC.image());
        }
        if(!mods.contains(TokenKind.ABSTRACT.image())){
            mods.add(TokenKind.ABSTRACT.image());
        }
       if(!mods.contains(TokenKind.INTERFACE.image())){
            mods.add(TokenKind.INTERFACE.image());
        }


    }

    /**
     * Declares this interface in the parent (compilation unit) context.
     *
     * @param context
     *            the parent (compilation unit) context.
     */

    @Override
    public void declareThisType(Context context) {
        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        CLEmitter partial = new CLEmitter(false);
        partial.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), null, false);
        thisType = Type.typeFor(partial.toClass());
        context.addType(line, thisType);

    }

    /**
     * Pre-analyzes the members of this declaration in the parent context.
     * Pre-analysis extends to the member headers (including method headers) but
     * not into the bodies.
     *s
     * @param context
     *            the parent (compilation unit) context.
     */

    @Override
    public void preAnalyze(Context context) {
        // Construct an interface context
        this.context = new ClassContext(this, context);

        // Resolve superinterfaces
        for(int i=0; i<superInterfaces.size(); i++){
            superInterfaces.set(i, superInterfaces.get(i).resolve(this.context));
        }

        // Creating a partial interface in memory can result in a
        // java.lang.VerifyError if the semantics below are
        // violated, so we can't defer these checks to analyze()
        for (Type superInterface : superInterfaces){
            thisType.checkAccess(line, superInterface);
            if(superInterface.isFinal()){
                JAST.compilationUnit.reportSemanticError(line,
                        "Cannot extend a final type: %s", superInterface.toString());
            }
        }

        for (Type superInterface : superInterfaces) {
            superInterfacesNames.add(superInterface.jvmName());
        }

        // Create the (partial) interface
        CLEmitter partial = new CLEmitter(false);

        // Add the interface header to the partial interface
        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        partial.addClass(mods, qualifiedName, Type.OBJECT.jvmName(),superInterfacesNames, false);

        // Pre-analyze the members and add them to the partial
        for (JMember member : interfaceBlock) {
            if (member instanceof JMethodDeclaration) {
                ((JMethodDeclaration) member).checkForForbiddenModifiers();
                ((JMethodDeclaration) member).makeAbstractAndPublic();
            }
            if (member instanceof JFieldDeclaration) {
                ((JFieldDeclaration) member).makeForInterface();
            }
            member.preAnalyze(this.context, partial);
        }


        // Get the Interface rep for the (partial) interface and make it
        // the representation for this type
        Type id = this.context.lookupType(name);
        if (id != null && !JAST.compilationUnit.errorHasOccurred()) {
            id.setClassRep(partial.toClass());
        }

    }

    /**
     * Performs semantic analysis on the interface and all of its members within the
     * given context. Analysis includes field initializations
     *
     * @param context
     *            the parent (compilation unit) context. Ignored here.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

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

    /**
     * Generates code for the interface declaration.
     *
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    @Override
    public void codegen(CLEmitter output) {
        // The interface header
        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;

        output.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), superInterfacesNames, false);


        // The members
        for (JMember member : interfaceBlock) {
            ((JAST) member).codegen(output);
        }

        // Generate an interface initialization method?
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
        p.printf("<JInterfaceDeclaration line=\"%d\" name=\"%s\"");
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
        p.printf("<SuperInterfaces>");
        p.indentRight();
        for(String superInterfaceName : superInterfacesNames){
            p.printf("<Interface name =\"%s\"/>\n",superInterfaceName);
        }
        p.indentLeft();
        p.println("</SuperInterfaces");
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

    /**
     * Returns the interface name.
     *
     * @return the interface name.
     */

    @Override
    public String name() {
        return name;
    }


    /**
     * Returns the interface's supertype.
     *
     * @return the interface's supertype.
     */

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
