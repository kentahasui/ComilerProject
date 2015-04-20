package errors;

import errors.CompilerError.Type;

public class SemanticError extends CompilerError{
	
	public SemanticError(Type errorNumber, String message)
	{
		super(errorNumber, message);
	}
	
	/** The header for the error message. 
	 * @param lineNumber  line number where error occurs 
	 * @param lineContent the line itself
	 * 
	 * @return
	 */
	private static String errorStart(int lineNumber, String lineContent){
		return ">>> ERROR AT LINE " + lineNumber + ": " + lineContent + "\n>>> ";
	}
	
	/** Semantic Error thrown when an array is declared without subscripts */
	public static SemanticError MissingSubscripts(int lineNumber, String lineContent){
		return new SemanticError(Type.ARRAY_WITHOUT_SUBSCRIPTS,
				errorStart(lineNumber, lineContent) +
				"Missing subscripts for this array declaration");
	}
	
	/** Semantic Error thrown when a simple variable is indexed with subscripts 
	 * Should go into error recovery: change symbol table entry to be an array entry rather than simple variable*/
	public static SemanticError SimpleSubscripts(int lineNumber, String lineContent){
		return new SemanticError(Type.VARIABLE_WITH_SUBSCRIPTS,
				errorStart(lineNumber, lineContent) +
				"Simple variables cannot use subscripts");
	}
	
	/** Semantic Error thrown when an undeclared variable is referenced 
	 * Should go into error recovery: place variable into symbol table */
	public static SemanticError UndeclaredVariable(int lineNumber, String lineContent){
		return new SemanticError(Type.UNDECLARED_VARIABLE,
				errorStart(lineNumber, lineContent) +
				"Undeclared variable");
	}
	
	/** Semantic Error thrown when two variables with the same name are declared in the same scope */
	public static SemanticError MultiplyDeclaredVariable(int lineNumber, String lineContent){
		return new SemanticError(Type.MULTIPLY_DECLARED_VARIABLE,
				errorStart(lineNumber, lineContent) +
				"Multiply declared variable. Cannot have two variables with the same name in the same scope");
	}

}
