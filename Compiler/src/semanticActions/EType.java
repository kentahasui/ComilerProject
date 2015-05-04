package semanticActions;

/** Enumerated Class to keep track of an expression types. 
 * Expressions can be either arithmetic or relational*/
public enum EType {
	ARITHMETIC(1), RELATIONAL(2);
	
	private int n;
	private EType(int i) { n = i; }
	public int getIndex() { return n; }

}
