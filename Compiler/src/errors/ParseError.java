package errors;
import token.*;
import grammarsymbols.*;

import errors.CompilerError.Type;

public class ParseError extends CompilerError{
	
	public ParseError(Type errorNumber, String message)
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
	
	/** Parse error thrown when two terminals don't match
	 *  @param lineNumber lineNumber
	 *  @param lineContent The text in the line
	 *  @param expected The expected Token Type
	 *  @param actual The actual Token Type found*/
	public static ParseError UnmatchedTerminals(int lineNumber, String lineContent, 
												GrammarSymbol expected, GrammarSymbol actual){
		return new ParseError(Type.UNMATCHED_TERMINALS,
				errorStart(lineNumber, lineContent) +
				" Expected to find a " + expected.toString() + " but found a " + actual.toString());
	}

	/** Parse error thrown when the element found in the parse table is an error. 
	 * Takes in an error message as a parameter. This message will be stored in 
	 * the ParseTable class*/
	public static ParseError ErrorProduction(int lineNumber, String lineContent, 
			String message, GrammarSymbol s){
		return new ParseError(Type.ERROR_PRODUCTION,
				errorStart(lineNumber, lineContent) +
				message + "\n>>> Got a " + s.toString() + " instead");
	}
	
	/** Parse error thrown when a grammar symbol is neither a terminal, nonterminal, nor a semantic action */
	public static ParseError UnknownSymbolType(int lineNumber, String lineContent, GrammarSymbol s){
		return new ParseError(Type.UNKNOWN_SYMBOL_TYPE, 
				errorStart(lineNumber, lineContent) + 
				s.toString() + " is not a terminal, nonterminal, nor a semantic action");
	}
	
	/** Parse error thrown when, in panic mode error recovery, the parser reaches the EOF symbol 
	 * on the stack
	 */
	public static ParseError ParserQuit(){
		return new ParseError(Type.PARSER_QUIT, "Parse quit early due to error");
	}


}
