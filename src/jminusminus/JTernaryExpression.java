package jminusminus;

abstract class JTernaryExpression extends JExpression{

    protected String operator;

    protected JExpression condition;
    protected JExpression thenPart;
    protected JExpression elsePart;
    
    protected JTernaryExpression(int line, String operator, JExpression condition,
                                 JExpression thenPart, JExpression elsePart) {
        super(line);
        
        this.operator = operator;
        this.condition = condition;
        this.thenPart = thenPart;
        this.elsePart = elsePart;
    }
}

class JConditionalOperator extends JTernaryExpression {

    public JConditionalOperator(int line, JExpression condition, JExpression thenPart, JExpression elsePart) {
        super(line, "?", condition, thenPart, elsePart);
        }


    public JExpression analyze(Context context) {
        return this;
    }


    public void codegen(CLEmitter output) {
    }

    public void writeToStdOut(PrettyPrinter p) {
    }
}