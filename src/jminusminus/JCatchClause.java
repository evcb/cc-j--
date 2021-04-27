package jminusminus;

public class JCatchClause extends JStatement {
	private JCatchFormalParameter param;
	private JBlock block;

	protected JCatchClause(int line, JCatchFormalParameter param, JBlock block) {
		super(line);
		this.param = param;
		this.block = block;
	}

	public JAST analyze(Context context) {
		for (TypeName t : param.types())
			context.addExceptionType(t.resolve(context));

		block.analyze(context);

		return this;
	}

	public JCatchFormalParameter getCatchFormalParameter() {
		return param;
	}

	public JBlock getBlock() {
		return block;
	}

	@Override
	public void codegen(CLEmitter output) {
		// TODO Auto-generated method stub

	}

	public void writeToStdOut(PrettyPrinter p) {
		p.printf("<CatchClause>\n");
		p.indentRight();
		param.writeToStdOut(p);
		p.indentLeft();

		p.indentRight();
		block.writeToStdOut(p);
		p.indentLeft();
		p.printf("</CatchClause>\n");
	}
}
