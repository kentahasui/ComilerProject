package errors;

import errors.CompilerError.Type;
import grammarsymbols.TokenType;

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
	
	/** Semantic Error thrown when an array is referenced without subscripts */
	public static SemanticError MissingSubscripts(int lineNumber, String lineContent, String varName){
		return new SemanticError(Type.ARRAY_WITHOUT_SUBSCRIPTS,
				errorStart(lineNumber, lineContent) +
				"Missing subscripts for this array: " + varName);
	}
	
	/** Semantic Error thrown when a simple variable is indexed with subscripts 
	 * Should go into error recovery: change symbol table entry to be an array entry rather than simple variable*/
	public static SemanticError SimpleSubscripts(int lineNumber, String lineContent){
		return new SemanticError(Type.VARIABLE_WITH_SUBSCRIPTS,
				errorStart(lineNumber, lineContent) +
				"Simple variables cannot use subscripts");
	}
	
	/** Semantic Error thrown when a simple variable is indexed with subscripts 
	 * Should go into error recovery: change symbol table entry to be an array entry rather than simple variable*/
	public static SemanticError SimpleSubscripts(int lineNumber, String lineContent, String varName){
		return new SemanticError(Type.VARIABLE_WITH_SUBSCRIPTS,
				errorStart(lineNumber, lineContent) +
				"Subscripts cannot be used for non-array variables: " + varName);
	}
	
	/** Semantic Error thrown when an undeclared variable is referenced 
	 * Should go into error recovery: place variable into symbol table */
	public static SemanticError UndeclaredVariable(int lineNumber, String lineContent){
		return new SemanticError(Type.UNDECLARED_VARIABLE,
				errorStart(lineNumber, lineContent) +
				"Undeclared variable");
	}
	
	/** Semantic Error thrown when an undeclared variable is referenced 
	 * Should go into error recovery: place variable into symbol table */
	public static SemanticError UndeclaredVariable(int lineNumber, String lineContent, String variable){
		return new SemanticError(Type.UNDECLARED_VARIABLE,
				errorStart(lineNumber, lineContent) +
				"Undeclared variable: " + variable);
	}
	
	/** Semantic Error thrown when two variables with the same name are declared in the same scope */
	public static SemanticError MultiplyDeclaredVariable(int lineNumber, String lineContent){
		return new SemanticError(Type.MULTIPLY_DECLARED_VARIABLE,
				errorStart(lineNumber, lineContent) +
				"Multiply declared variable. Cannot have two variables with the same name in the same scope");
	}
	
	/** Semantic Error thrown when two variables with the same name are declared in the same scope */
	public static SemanticError MultiplyDeclaredVariable(int lineNumber, String lineContent, String varName){
		return new SemanticError(Type.MULTIPLY_DECLARED_VARIABLE,
				errorStart(lineNumber, lineContent) +
				"Multiply declared variable. Cannot have two variables with the same name in the same scope: " + varName);
	}
	
	/** Semantic Error thrown when a procedure is expected */
	public static SemanticError NonProcedure(int lineNumber, String lineContent, String varName){
		return new SemanticError(Type.NON_PROCEDURE,
				errorStart(lineNumber, lineContent) +
				"This variable is not a procedure: " + varName);
	}
	
	/** Semantic Error thrown when a function is expected */
	public static SemanticError NonFunction(int lineNumber, String lineContent, String varName){
		return new SemanticError(Type.NON_FUNCTION,
				errorStart(lineNumber, lineContent) +
				"This variable is not a function: " + varName);
	}
	
	/** Semantic Error thrown when the current function and the actual function do not match up */
	public static SemanticError WrongFunction(int lineNumber, String lineContent, String varName){
		return new SemanticError(Type.WRONG_FUNCTION,
				errorStart(lineNumber, lineContent) +
				"This function is not the current function : " + varName);
	}
	
	/** Semantic error thrown when the modulus operation is performed on non-integer operands */
	public static SemanticError ModError(int lineNumber, String lineContent, String id1, String id2){
		return new SemanticError(Type.MOD_ERROR,
				errorStart(lineNumber, lineContent) +
				"The modulus operation requires 2 integer operands. " 
				+ id1 + " and " + id2 + " are not both integers");
	}
	
	/** Semantic error thrown when processing array subscripts: the types must be integers */
	public static SemanticError IntegerExpected(int lineNumber, String lineContent, String varName){
		return new SemanticError(Type.INTEGER_EXPECTED,
				errorStart(lineNumber, lineContent) +
				"Subscripts for an array must be an integer. It cannot be a real number: " + varName);
	}
	
	/** Semantic Error thrown when an arithmetic type was expected but a relational type was encountered */
	public static SemanticError ArithmeticTypeError(int lineNumber, String lineContent){
		return new SemanticError(Type.ETYPE_ERROR_A,
				errorStart(lineNumber, lineContent) +
				"Expected an arithmetic expression but received a relational expression");
	}
	
	/** Semantic Error thrown when an arithmetic type was expected but a relational type was encountered */
	public static SemanticError RelationalTypeError(int lineNumber, String lineContent){
		return new SemanticError(Type.ETYPE_ERROR_R,
				errorStart(lineNumber, lineContent) +
				"Expected a relational expression but received an arithmetic expression");
	}
	
	/** Semantic Error thrown when a declared variable is already reserved */
	public static SemanticError ReservedVariable(int lineNumber, String lineContent, String varName){
		return new SemanticError(Type.RESERVED_VARIABLE, 
				errorStart(lineNumber, lineContent) +
				"The variable name " + varName + " is reserved. You must choose another name");
	}
	
	/** Semantic Error thrown when the number of parameters on parmCount stack is not equal to the 
	 * procedure entry's number of parameters
	 */
	public static SemanticError ParameterMiscount(int lineNumber, String lineContent, String procName){
		return new SemanticError(Type.PARAMETER_MISCOUNT, 
				errorStart(lineNumber, lineContent) +
				"The number of declared and actual parameters do not match up for procedure: " + procName);
	}
	
	/** Semantic Error thrown when a procedure or function is encountered instead of a constant, variable, 
	 * array, or function result
	 */
	public static SemanticError UnexpectedSubroutine(int lineNumber, String lineContent, String IDName){
		return new SemanticError(Type.UNEXPECTED_SUBROUTINE, 
				errorStart(lineNumber, lineContent) +
				"A constant, variable, array, or function result was expected for: " + IDName);
	}
	
	/** Semantic Error thrown when the parameter types for a function or procedure do not match */
	public static SemanticError UnmatchedParameterTypes(int lineNumber, String lineContent,
			String ProcName, String IDName, TokenType actual, TokenType expected){
		return new SemanticError(Type.UNMATCHED_PARAMETER_TYPES, 
				errorStart(lineNumber, lineContent) +
				"The actual parameter types for " + ProcName + " do not match its declaration." 
				+ "\n>>> For parameter " + IDName + ", " + expected.toString() + " was expected but "
				+ actual.toString() + "  was encountered");
	}
	
	/** Semantic Error thrown when an array parameter definition does not match its declaration */
	public static SemanticError ArrayParameterError(int lineNumber, String lineContent, String ProcName, String IDName){
		return new SemanticError(Type.ARRAY_PARAMETER_ERROR, 
				errorStart(lineNumber, lineContent) +
				"The actual parameter " + IDName + " for " + ProcName + " does not match its declaration.");
	}

}
