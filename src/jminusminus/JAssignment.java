// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * This abstract base class is the AST node for an assignment statement.
 */
abstract class JAssignment extends JBinaryExpression {
    /**
     * Constructs an AST node for an assignment operation.
     *
     * @param line     line in which the assignment operation occurs in the source
     *                 file.
     * @param operator the actual assignment operator.
     * @param lhs      the lhs operand.
     * @param rhs      the rhs operand.
     */
    public JAssignment(int line, String operator, JExpression lhs, JExpression rhs) {
        super(line, operator, lhs, rhs);
    }
}

/**
 * The AST node for an assignment (=) expression. The = operator has two
 * operands: a lhs and a rhs.
 */
class JAssignOp extends JAssignment {
    /**
     * Constructs the AST node for an assignment (=) expression given the lhs and
     * rhs operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  lhs operand.
     * @param rhs  rhs operand.
     */
    public JAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "=", lhs, rhs);
    }

    /**
     * Analyzes the lhs and rhs, checking that types match, and sets the result
     * type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs))
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");
        else
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);

        rhs = (JExpression) rhs.analyze(context);
        // promotion of int to double
        if (lhs.type() == Type.DOUBLE && rhs.type() == Type.INT) {
            promoteRhs();
            type = lhs.type();
        } else {
            rhs.type().mustMatchExpected(line(), lhs.type());
            type = rhs.type();
        }
        if (lhs instanceof JVariable) {
            IDefn defn = ((JVariable) lhs).iDefn();

            if (defn != null)
                // Local variable; consider it to be initialized now.
                ((LocalVariableDefn) defn).initialize();
        }

        return this;
    }

    /**
     * Code generation for an assignment involves, generating code for loading any
     * necessary Lvalue onto the stack, for loading the Rvalue, for (unless a
     * statement) copying the Rvalue to its proper place on the stack, and for doing
     * the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */
    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        rhs.codegen(output);

        if (!isStatementExpression)
            // Generate code to leave the Rvalue atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);

        ((JLhs) lhs).codegenStore(output);
    }
}

/**
 * The AST node for a += expression. A += expression has two operands: a lhs and
 * a rhs.
 */
class JPlusAssignOp extends JAssignment {
    /**
     * Constructs the AST node for a += expression given its lhs and rhs operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */
    public JPlusAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "+=", lhs, rhs);
    }

    /**
     * Analyzes the lhs and rhs, rewrites rhs as lhs + rhs (string concatenation) if
     * lhs is of type {@code String}, and sets the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");

            return this;
        } else
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);

        rhs = (JExpression) rhs.analyze(context);

        // promotion of int to double
        if (lhs.type() == Type.DOUBLE && rhs.type() == Type.INT) {
            promoteRhs();
            type = lhs.type();
        } else if (lhs.type().equals(Type.INT)) {
            rhs.type().mustMatchExpected(line(), Type.INT);
            type = Type.INT;
        } else if (lhs.type().equals(Type.STRING)) {
            rhs = (new JStringConcatenationOp(line, lhs, rhs)).analyze(context);
            type = Type.STRING;
        } else if (lhs.type().equals(Type.DOUBLE)) {
            rhs.type().mustMatchExpected(line(), Type.DOUBLE);
            type = Type.DOUBLE;
        } else {
            type = Type.INT;
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for +=: " + lhs.type());
        }

        return this;
    }

    /**
     * Code generation for += involves, generating code for loading any necessary
     * l-value onto the stack, for (unless a string concatenation) loading the
     * r-value, for (unless a statement) copying the r-value to its proper place on
     * the stack, and for doing the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */
    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);

        if (lhs.type().equals(Type.STRING))
            rhs.codegen(output);
        else if (lhs.type().equals(Type.INT)) {
            ((JLhs) lhs).codegenLoadLhsRvalue(output);
            rhs.codegen(output);
            output.addNoArgInstruction(IADD);
        } else { // double
            ((JLhs) lhs).codegenLoadLhsRvalue(output);
            rhs.codegen(output);
            output.addNoArgInstruction(DADD);
        }

        if (!isStatementExpression)
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);

        ((JLhs) lhs).codegenStore(output);
    }
}

/**
 * The AST node for a -= expression. A -= expression has two operands: a lhs and
 * a rhs.
 */
class JMinusAssignOp extends JAssignment {
    /**
     * Constructs the AST node for a -= expression given its lhs and rhs operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */
    public JMinusAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "-=", lhs, rhs);
    }

    /**
     * Analyzes the lhs and rhs, and sets the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed AST subtree.
     */
    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");

            return this;
        } else
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);

        rhs = (JExpression) rhs.analyze(context);

        lhs.type().mustMatchOneOf(line(), Type.INT, Type.DOUBLE);
        rhs.type().mustMatchOneOf(line(), Type.INT, Type.DOUBLE);

        if (lhs.type() == Type.DOUBLE && rhs.type() == Type.INT)
            promoteRhs(); // promotion of int to double
        else if (lhs.type() == Type.INT && rhs.type() == Type.DOUBLE)
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for -=: " + lhs.type());

        type = lhs.type();

        return this;
    }

    /**
     * Code generation for -= involves, generating code for loading any necessary
     * l-value onto the stack, for loading the r-value, and for doing the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */
    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);

        rhs.codegen(output);
        if (type == Type.INT) {
            output.addNoArgInstruction(ISUB);
        } else if (type == Type.DOUBLE) {
            output.addNoArgInstruction(DSUB);
        }

        if (!isStatementExpression)
            ((JLhs) lhs).codegenDuplicateRvalue(output); // Generate code to leave the r-value atop stack

        ((JLhs) lhs).codegenStore(output);
    }
}

/**
 * The AST node for a *= expression. A *= expression has two operands: a lhs and
 * a rhs.
 */
class JStarAssignOp extends JAssignment {
    /**
     * Constructs the AST node for a *= expression given its lhs and rhs operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */
    public JStarAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "*=", lhs, rhs);
    }

    /**
     * Analyzes the lhs and rhs, and sets the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed AST subtree.
     */
    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");

            return this;
        } else
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);

        rhs = (JExpression) rhs.analyze(context);

        lhs.type().mustMatchOneOf(line(), Type.INT, Type.DOUBLE);
        rhs.type().mustMatchOneOf(line(), Type.INT, Type.DOUBLE);

        if (lhs.type() == Type.DOUBLE && rhs.type() == Type.INT)
            promoteRhs(); // promotion of int to double
        else if (lhs.type() == Type.INT && rhs.type() == Type.DOUBLE)
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for *=: " + lhs.type());

        return this;
    }

    /**
     * Code generation for *= involves, generating code for loading any necessary
     * l-value onto the stack, for loading the r-value, and for doing the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */
    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);

        rhs.codegen(output);
        if (type == Type.INT) {
            output.addNoArgInstruction(IMUL);
        } else if (type == Type.DOUBLE) {
            output.addNoArgInstruction(DMUL);
        }

        if (!isStatementExpression)
            ((JLhs) lhs).codegenDuplicateRvalue(output); // Generate code to leave the r-value atop stack

        ((JLhs) lhs).codegenStore(output);
    }
}

/**
 * The AST node for a /= expression. A /= expression has two operands: a lhs and
 * a rhs.
 */
class JSlashAssignOp extends JAssignment {
    /**
     * Constructs the AST node for a /= expression given its lhs and rhs operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */
    public JSlashAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "/=", lhs, rhs);
    }

    /**
     * Analyzes the lhs and rhs, and sets the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed AST subtree.
     */
    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");

            return this;
        } else
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);

        rhs = (JExpression) rhs.analyze(context);

        lhs.type().mustMatchOneOf(line(), Type.INT, Type.DOUBLE);
        rhs.type().mustMatchOneOf(line(), Type.INT, Type.DOUBLE);

        if (lhs.type() == Type.DOUBLE && rhs.type() == Type.INT)
            promoteRhs(); // promotion of int to double
        else if (lhs.type() == Type.INT && rhs.type() == Type.DOUBLE)
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for /=: " + lhs.type());

        return this;
    }

    /**
     * Code generation for /= involves, generating code for loading any necessary
     * l-value onto the stack, for loading the r-value, and for doing the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */
    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);

        rhs.codegen(output);
        if (type == Type.INT) {
            output.addNoArgInstruction(IDIV);
        } else if (type == Type.DOUBLE) {
            output.addNoArgInstruction(DDIV);
        }

        if (!isStatementExpression)
            ((JLhs) lhs).codegenDuplicateRvalue(output); // Generate code to leave the r-value atop stack

        ((JLhs) lhs).codegenStore(output);
    }
}

/**
 * The AST node for a %= expression. A %= expression has two operands: a lhs and
 * a rhs.
 */
class JModAssignOp extends JAssignment {
    /**
     * Constructs the AST node for a %= expression given its lhs and rhs operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */
    public JModAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "%=", lhs, rhs);
    }

    /**
     * Analyzes the lhs and rhs, and sets the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed AST subtree.
     */
    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "Illegal lhs for assignment");

            return this;
        } else
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);

        rhs = (JExpression) rhs.analyze(context);

        lhs.type().mustMatchOneOf(line(), Type.INT, Type.DOUBLE);
        rhs.type().mustMatchOneOf(line(), Type.INT, Type.DOUBLE);

        if (lhs.type() == Type.DOUBLE && rhs.type() == Type.INT)
            promoteRhs(); // promotion of int to double
        else if (lhs.type() == Type.INT && rhs.type() == Type.DOUBLE)
            JAST.compilationUnit.reportSemanticError(line(), "Invalid lhs type for %=: " + lhs.type());

        return this;
    }

    /**
     * Code generation for %= involves, generating code for loading any necessary
     * l-value onto the stack, for loading the r-value, and for doing the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */
    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        ((JLhs) lhs).codegenLoadLhsRvalue(output);

        rhs.codegen(output);
        if (type == Type.INT) {
            output.addNoArgInstruction(IREM);
        } else if (type == Type.DOUBLE) {
            output.addNoArgInstruction(DREM);
        }

        if (!isStatementExpression)
            ((JLhs) lhs).codegenDuplicateRvalue(output); // Generate code to leave the r-value atop stack

        ((JLhs) lhs).codegenStore(output);
    }
}
