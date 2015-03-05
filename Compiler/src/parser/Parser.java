package parser;
import lex.*;
import token.*;
import errors.*;
import grammarsymbols.*;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Stack;

public class Parser {
	
	private ArrayDeque<GrammarSymbol> stack;	// Stack of grammar symbols
	private RHSTable rhsTable;					// Table for right hand side productions
	private ParseTable parseTable;				// Parse Table
	private Tokenizer lexer;					// Lexical Analyzer
	private final boolean DUMPSTACK = true;
	
	/** Constructor for the parser. 
	 * Initializes the stack of grammar symbols, the RHSTable, and the ParseTable. 
	 */
	private Parser(){
		stack = new ArrayDeque<GrammarSymbol>();
		rhsTable = new RHSTable();
		parseTable = new ParseTable();
	}
	
	/** Constructor for the parser. 
	 * Initializes the stack of grammar symbols, the RHSTable, and the ParseTable. 
	 * Takes a file name as a parameter. 
	 * Initializes the lexical analyzer to return tokens from the file given.
	 */
	public Parser(String fileName){
		this();
		lexer = new Tokenizer(fileName);
	}
	
	/** Method to parse the file. Repeatedly retrieves tokens from the lexical analyzer.
	 * @throws CompilerError
	 */
	public void parse() throws CompilerError{
		Token currentToken = lexer.GetNextToken();	// Current Token
		GrammarSymbol predicted;	// Next predicted grammar symbol
		// Clear stack at the start
		stack.clear();
		// Push the end marker and the start symbol on the stack
		stack.push(TokenType.ENDOFFILE);
		stack.push(NonTerminal.Goal);
		// Loop until the stack is empty:
		while(!stack.isEmpty()){
			/* If the token extracted from the input is an error token, 
			 * a lexical error has occured. Quit parsing. */
			if(currentToken.getType() == TokenType.ERROR){
				System.err.println(">>> Lexical Error. Quitting parser");
				if(DUMPSTACK) { dumpStack(); }
				return;
			}
			// Pop the first element off of the stack
			predicted = stack.pop();
			// Check if there is a token (terminal) on the stack
			if(predicted.isToken()){
				// Try to match the current token with the non-terminal: 
				if(predicted == currentToken.getType()){
					// If they match, we get the next token from the input
					currentToken = lexer.GetNextToken();
				}
				// The terminals do not match: throw an exception
				else{
					if(DUMPSTACK) { dumpStack(); }
					throw ParseError.UnmatchedTerminals(lexer.getLineNumber(), lexer.getCurrentLine(), predicted, currentToken.getType());
				}
			}
			// Otherwise, if the predicted grammar symbol is a non-terminal
			else if(predicted.isNonTerminal()){
				// Find the production in the parse table
				// We index into the table using the current token's type and the predicted 
				// grammarSymbol
				int index = parseTable.getCode(currentToken.getType(), predicted);
				// A negative value for the code represents the empty string
				if(index < 0){
					continue;
				}
				else if(index >= parseTable.ERRORCODE){
					if(DUMPSTACK) { dumpStack(); }
					throw ParseError.ErrorProduction(lexer.getLineNumber(), lexer.getCurrentLine(), parseTable.getErrorMessage(index));
				}
				// We have a valid production
				else{
					// The productions to push onto the stack
					GrammarSymbol[] productions = rhsTable.getRule(index);
					// Push onto the stack in reverse order, so they are popped off in the correct order
					for(int i = productions.length-1; i>=0; i--){
						// Push the grammar symbols onto the stack
						stack.push(productions[i]);
					}
				}
				
			}
			// Otherwise if the symbol popped off is a semantic action, we ignore it and keep popping symbols off
			else if(predicted.isAction()){
				continue;
			}
			// This portion of code shouldn't execute! The symbol is neither a terminal, nonterminal,
			// or a semantic action. throw an error
			else{
				throw ParseError.UnknownSymbolType(lexer.getLineNumber(), lexer.getCurrentLine(), predicted);
			}
		} // End While Loop
		
		System.out.println("Parse Successful");
	}
	
	/** Method to print out the contents of the stack. 
	 *  Iterates through the grammar symbols from the top of the stack
	 *  to the bottom of the stack, as if we were continually popping elements off of the stack
	 */
	public void dumpStack(){
		Iterator<GrammarSymbol> iter = stack.iterator();
		while(iter.hasNext()){
			System.out.println(iter.next());
		}
	}

}
