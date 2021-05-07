package jminusminus;

import static jminusminus.CLConstants.*;

abstract class JConditionalExpression extends JExpression{

    protected String operator;

    protected JExpression condition;
    protected JExpression thenPart;
    protected JExpression elsePart;
    
    protected JConditionalExpression(int line, String operator, JExpression condition,
                                 JExpression thenPart, JExpression elsePart) {
        super(line);
        
        this.operator = operator;
        this.condition = condition;
        this.thenPart = thenPart;
        this.elsePart = elsePart;
    }
}

class JTernaryOperator extends JConditionalExpression {

    public JTernaryOperator(int line, JExpression condition, JExpression thenPart, JExpression elsePart) {
        super(line, "?", condition, thenPart, elsePart);
        }


    public JExpression analyze(Context context) {
        condition = (JExpression) condition.analyze(context);
        thenPart = (JExpression) thenPart.analyze(context);
        elsePart = (JExpression) elsePart.analyze(context);
        
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        elsePart.type().mustMatchExpected(line(), thenPart.type());
        type = thenPart.type();
        return this;        
    }


    public void codegen(CLEmitter output) {
        String thenLabel = output.createLabel();
        String elseLabel = output.createLabel();

        condition.codegen(output, thenLabel, false);

        thenPart.codegen(output);

        if (elsePart != null) {
            output.addBranchInstruction(GOTO, elseLabel);
        }

        output.addLabel(thenLabel);
        
        if (elsePart != null) {
            elsePart.codegen(output);
            output.addLabel(elseLabel);
        }
    }

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JTernaryOperator line=\"%d\" type=\"%s\" "
                + "operator=\"%s\">\n", line(), ((type == null) ? "" : type
                .toString()), Util.escapeSpecialXMLChars(operator));
        p.indentRight();
        p.printf("<First>\n");
        p.indentRight();
        condition.writeToStdOut(p);
        p.indentLeft();
        p.printf("</First>\n");
        p.printf("<Second>\n");
        p.indentRight();
        thenPart.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Second>\n");
        p.printf("<Third>\n");
        p.indentRight();
        elsePart.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Third>\n");
        p.indentLeft();
        p.printf("</JTernaryOperator>\n");
    }
}