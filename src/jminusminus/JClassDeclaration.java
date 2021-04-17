// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import static jminusminus.CLConstants.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * A class declaration has a list of modifiers, a name, a super class and a
 * class block; it distinguishes between instance fields and static (class)
 * fields for initialization, and it defines a type. It also introduces its own
 * (class) context.
 *
 * @see ClassContext
 */

class JClassDeclaration extends JAST implements JTypeDecl {

    /** Class modifiers. */
    private ArrayList<String> mods;

    /** Class name. */
    private String name;

    /** Class block. */
    private ArrayList<JMember> classBlock;

    /** Super class type. */
    private Type superType;

    /** Interfaces implemented */
    private ArrayList<Type> interfacesImplemented;

    /** Interfaces implemented jvm names */
    private ArrayList<String> interfacesImplementedNames;

    /** This class type. */
    private Type thisType;

    /** Context for this class. */
    private ClassContext context;

    /** Whether this class has an explicit constructor. */
    private boolean hasExplicitConstructor;

    /** Instance fields of this class. */
    private ArrayList<JFieldDeclaration> instanceFieldInitializations;

    /** Static (class) fields of this class. */
    private ArrayList<JFieldDeclaration> staticFieldInitializations;

    private ArrayList<Field> receivedFields;

    /**
     * Constructs an AST node for a class declaration given the line number, list
     * of class modifiers, name of the class, its super class type, and the
     * class block.
     *
     * @param line
     *            line in which the class declaration occurs in the source file.
     * @param mods
     *            class modifiers.
     * @param name
     *            class name.
     * @param superType
     *            super class type.
     * @param implementations
     *             class implemented interfaces.
     * @param classBlock
     *            class block.
     */

    public JClassDeclaration(int line, ArrayList<String> mods, String name,
                             Type superType, ArrayList<Type> implementations, ArrayList<JMember> classBlock) {
        super(line);
        this.mods = mods;
        this.name = name;
        this.superType = superType;
        this.interfacesImplemented = implementations;
        this.classBlock = classBlock;
        hasExplicitConstructor = false;
        instanceFieldInitializations = new ArrayList<JFieldDeclaration>();
        staticFieldInitializations = new ArrayList<JFieldDeclaration>();
        this.interfacesImplementedNames = new ArrayList<String>();
        receivedFields = new ArrayList<>();


    }

    /**
     * Returns the class name.
     *
     * @return the class name.
     */

    public String name() {
        return name;
    }

    /**
     * Returns the class' super class type.
     *
     * @return the super class type.
     */

    public Type superType() {
        return superType;
    }

    /**
     * Returns the type that this class declaration defines.
     *
     * @return the defined type.
     */

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

    /**
     * Declares this class in the parent (compilation unit) context.
     *
     * @param context
     *            the parent (compilation unit) context.
     */

    public void declareThisType(Context context) {
        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        CLEmitter partial = new CLEmitter(false);
        partial.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), null,
                false); // Object for superClass, just for now, also null for implemented interfaces for now
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
        //TODO: complete method for interfaces
        // Construct a class context
        this.context = new ClassContext(this, context);

        // Resolve superclass
        superType = superType.resolve(this.context);

        // Creating a partial class in memory can result in a
        // java.lang.VerifyError if the semantics below are
        // violated, so we can't defer these checks to analyze()
        thisType.checkAccess(line, superType);
        if (superType.isFinal()) {
            JAST.compilationUnit.reportSemanticError(line,
                    "Cannot extend a final type: %s", superType.toString());
        }

        for (int i=0; i<interfacesImplemented.size(); i++){
            interfacesImplemented.set(i, interfacesImplemented.get(i).resolve(this.context));
        }
        // Creating a partial class in memory can result in a
        // java.lang.VerifyError if the semantics below are
        // violated, so we can't defer these checks to analyze()
        for (Type interfaceImplemented: interfacesImplemented){
            thisType.checkAccess(line, interfaceImplemented);
            if(interfaceImplemented.isFinal()){
                JAST.compilationUnit.reportSemanticError(line,
                        "Cannot extend a final type: %s", interfaceImplemented.toString());
            }
        }

        for (Type interfaceImplemented : interfacesImplemented) {
            interfacesImplementedNames.add(interfaceImplemented.jvmName());
        }



        // Create the (partial) class
        CLEmitter partial = new CLEmitter(false);

        // Add the class header to the partial class
        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        partial.addClass(mods, qualifiedName, superType.jvmName(), interfacesImplementedNames, false);

        // Pre-analyze the members and add them to the partial
        // class
        for (JMember member : classBlock) {
            member.preAnalyze(this.context, partial);
            if (member instanceof JConstructorDeclaration
                    && ((JConstructorDeclaration) member).params.size() == 0) {
                hasExplicitConstructor = true;
            }
        }

        //check that the methods of the interface have been implemented
        if(!interfacesImplemented.isEmpty() && !mods.contains(TokenKind.ABSTRACT.image())) {
            checkInterfaceMethodsImplemented();
        }

        //checkInterfaceVariables(partial);
        addInterfaceVariableAccess(partial);

        // Add the implicit empty constructor?
        if (!hasExplicitConstructor) {
            codegenPartialImplicitConstructor(partial);
        }

        // Get the Class rep for the (partial) class and make it
        // the
        // representation for this type
        Type id = this.context.lookupType(name);
        if (id != null && !JAST.compilationUnit.errorHasOccurred()) {
            id.setClassRep(partial.toClass());
        }
    }

    public void checkInterfaceMethodsImplemented(){
        //get class methods
        HashSet<String> classMethods = new HashSet<>();
        for(JMember classMember : classBlock){
            if(classMember instanceof JMethodDeclaration){
                classMethods.add(((JMethodDeclaration) classMember).methodDeclString());
            }
        }

        HashSet<String> interfaceMethods = new HashSet<>();
        String methods = "";
        for(Type intImpl: interfacesImplemented){
            ArrayList<Method> intMethods = intImpl.abstractMethods();
            for (Method method : intMethods) {
                interfaceMethods.add(method.methodDeclString());
            }
        }

        if(!classMethods.containsAll(interfaceMethods)){
            JAST.compilationUnit.reportSemanticError(line,
                    "Class must define all methods declared in the implemented interfaces");
        }
    }

    public void checkInterfaceVariables(CLEmitter partial) {

        HashMap<String, Class<?>> classFields = new HashMap<>();
        for (JMember member : classBlock) {
            if (member instanceof JFieldDeclaration) {
                ArrayList<JVariableDeclarator> declarators = ((JFieldDeclaration) member).getDecls();
                for (JVariableDeclarator decl : declarators){
                    classFields.put(decl.name(), decl.type().classRep());
                }
            }
        }

        HashMap<String, Class<?>> interfaceFields = new HashMap<>();
        for(Type intImpl: interfacesImplemented){
            Class<?> cls = intImpl.classRep();
            java.lang.reflect.Field[] fields = cls.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                interfaceFields.put(field.getName(), field.getType());
            }
        }


        Iterator it = interfaceFields.entrySet().iterator();
        Iterator itClass;
        Map.Entry pair;
        Boolean found = false;
        while (it.hasNext()) {
            pair = (Map.Entry)it.next();
            String variableName = (String) pair.getKey();
            Class<?> variableType = (Class<?>) pair.getValue();
            itClass = classFields.entrySet().iterator();
            found = false;

            while(itClass.hasNext()){
                pair = (Map.Entry)itClass.next();
                if(pair.getKey().equals(variableName) && pair.getValue().equals(variableType)){
                    found = true;
                    break;
                }

                if(!found){
                    Field field = null;
                    java.lang.reflect.Field internalField = null;
                    for(Type intImpl: interfacesImplemented){
                        if(intImpl.fieldFor(variableName)!=null){
                            field = intImpl.fieldFor(variableName);
                            receivedFields.add(field);
                            break;
                        }
                    }

                }
            }
        }

    }

    /**
     * Performs semantic analysis on the class and all of its members within the
     * given context. Analysis includes field initializations and the method
     * bodies.
     *
     * @param context
     *            the parent (compilation unit) context. Ignored here.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JAST analyze(Context context) {
        // Analyze all members
        for (JMember member : classBlock) {
            ((JAST) member).analyze(this.context);
        }

        // Copy declared fields for purposes of initialization.
        for (JMember member : classBlock) {
            if (member instanceof JFieldDeclaration) {
                JFieldDeclaration fieldDecl = (JFieldDeclaration) member;
                if (fieldDecl.mods().contains("static")) {
                    staticFieldInitializations.add(fieldDecl);
                } else {
                    instanceFieldInitializations.add(fieldDecl);
                }
            }
        }

        // Finally, ensure that a non-abstract class has
        // no abstract methods.
        if (!thisType.isAbstract() && thisType.abstractMethods().size() > 0) {
            String methods = "";
            for (Method method : thisType.abstractMethods()) {
                methods += "\n" + method;
            }
            JAST.compilationUnit.reportSemanticError(line,
                    "Class must be declared abstract since it defines "
                            + "the following abstract methods: %s", methods);

        }
        return this;
    }

    /**
     * Generates code for the class declaration.
     *
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {

        // The class header
        String qualifiedName = JAST.compilationUnit.packageName() == "" ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        output.addClass(mods, qualifiedName, superType.jvmName(), interfacesImplementedNames, false);

        // The implicit empty constructor?
        if (!hasExplicitConstructor) {
            codegenImplicitConstructor(output);
        }

        // The members
        for (JMember member : classBlock) {
            if (!(member instanceof JInitializationBlock)) {
                ((JAST) member).codegen(output);
            }
        }

        // Generate a class initialization method?
        if (staticFieldInitializations.size() > 0) {
            codegenClassInit(output);
        }
      /*  ArrayList<String> fieldModifiers = new ArrayList<>();
        fieldModifiers.add("final");
        for(Field field : receivedFields){
            addField(fieldModifiers, field.name(),
                    field.type().toDescriptor(), false, field.get());
        }*/
    }

    /**
     * {@inheritDoc}
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JClassDeclaration line=\"%d\" name=\"%s\""
                + " super=\"%s\">\n", line(), name, superType.toString());
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
        if (interfacesImplemented != null) {
            p.println("<Implements>");
            p.indentRight();
            for (Type interfaceImplemented : interfacesImplemented) {
                p.printf("<Implements name=\"%s\"/>\n", interfaceImplemented.toString());
            }
            p.indentLeft();
            p.println("</Implements>");
        }
        if (classBlock != null) {
            p.println("<ClassBlock>");
            p.indentRight();
            for (JMember member : classBlock) {
                ((JAST) member).writeToStdOut(p);
            }
            p.indentLeft();
            p.println("</ClassBlock>");
        }
        p.indentLeft();
        p.println("</JClassDeclaration>");
    }

    /**
     * Generates code for an implicit empty constructor. (Necessary only if there
     * is not already an explicit one.)
     *
     * @param partial
     *            the code emitter (basically an abstraction for producing a
     *            Java class).
     */

    private void codegenPartialImplicitConstructor(CLEmitter partial) {
        // Invoke super constructor
        ArrayList<String> mods = new ArrayList<String>();
        mods.add("public");
        partial.addMethod(mods, "<init>", "()V", null, false);
        partial.addNoArgInstruction(ALOAD_0);
        partial.addMemberAccessInstruction(INVOKESPECIAL, superType.jvmName(),
                "<init>", "()V");

        // Return
        partial.addNoArgInstruction(RETURN);
    }


    private void addInterfaceVariableAccess(CLEmitter partial){
        //HashMap<String, Class<?>> interfaceFields = new HashMap<>();
        for(Type intImpl: interfacesImplemented){
            Class<?> cls = intImpl.classRep();
            java.lang.reflect.Field[] fields = cls.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                //interfaceFields.put(field.getName(), field.getType());
                //JAST.compilationUnit.reportSemanticError(line, "name : %s et type : %s",field.getName(), descriptorFor(field.getType()));
                //partial.addMemberAccessInstruction(GETSTATIC, intImpl.jvmName(), field.getName(), descriptorFor(field.getType()));
            }
        }
    }

    private static String descriptorFor(Class<?> cls) {
        return cls == null ? "V" : cls == void.class ? "V"
                : cls.isArray() ? "[" + descriptorFor(cls.getComponentType())
                : cls.isPrimitive() ? (cls == int.class ? "I"
                : cls == char.class ? "C"
                : cls == boolean.class ? "Z"
                : cls == double.class ? "D" : "?")
                : "L" + cls.getName().replace('.', '/') + ";";
    }
    /**
     * Generates code for an implicit empty constructor. (Necessary only if there
     * is not already an explicit one.
     *
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    private void codegenImplicitConstructor(CLEmitter output) {
        // Invoke super constructor
        ArrayList<String> mods = new ArrayList<String>();
        mods.add("public");
        output.addMethod(mods, "<init>", "()V", null, false);
        output.addNoArgInstruction(ALOAD_0);
        output.addMemberAccessInstruction(INVOKESPECIAL, superType.jvmName(),
                "<init>", "()V");

        // If there are instance field initializations, generate
        // code for them
        for (JFieldDeclaration instanceField : instanceFieldInitializations) {
            instanceField.codegenInitializations(output);
        }
        
        // Instance block before return is added
        for (JMember member : classBlock) {
            if (member instanceof JInitializationBlock) {
                if (!((JInitializationBlock) member).isStatic) {
                    ((JInitializationBlock) member).codegen(output);
                }
            }
        }

        // Return
        output.addNoArgInstruction(RETURN);
    }

    /**
     * Generates code for class initialization, in j-- this means static field
     * initializations.
     *
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    private void codegenClassInit(CLEmitter output) {
        ArrayList<String> mods = new ArrayList<String>();
        mods.add("public");
        mods.add("static");
        output.addMethod(mods, "<clinit>", "()V", null, false);

        // If there are instance initializations, generate code
        // for them
        for (JFieldDeclaration staticField : staticFieldInitializations) {
            staticField.codegenInitializations(output);
        }

        // Static block before the construction
        for (JMember member : classBlock) {
            if (member instanceof JInitializationBlock) {
                if (((JInitializationBlock) member).isStatic) {
                    ((JInitializationBlock) member).codegen(output);
                }
            }
        }

        // Return
        output.addNoArgInstruction(RETURN);
    }

}
