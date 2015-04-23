package parser;
import lex.*;
import semanticActions.SemanticActions;
import token.*;
import errors.*;
import grammarsymbols.*;

import java.util.ArrayDeque;
import java.util.Iterator;

public class Parser {
	
	private ArrayDeque<GrammarSymbol> stack;	// Stack of grammar symbols
	private RHSTable rhsTable;					// Table for right hand side productions
	private ParseTable parseTable;				// Parse Table
	private Tokenizer lexer;					// Lexical Analyzer
	private Token currentToken;					// Current Token
	private Token prevToken;
	private GrammarSymbol predicted;			// Next predicted grammar symbol
	private final boolean DUMPSTACK = false;		// Flag to dump the stack upon error
	private SemanticActions semanticActions;
	
	/** Private Constructor for the parser. 
	 * Initializes the stack of grammar symbols, the RHSTable, and the ParseTable.
	 * Will be called by the public constructor. 
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
		semanticActions = new SemanticActions(this);
	}
	
	/** Method to parse the file. Repeatedly retrieves tokens from the lexical analyzer.
	 * @throws CompilerError Throws an exception if the parser is not able to recover from a parse error, 
	 * or if there is a lexical error in the input file. 
	 */
	public void parse() throws CompilerError{
		currentToken = lexer.GetNextToken();	// Get first token from input
		// Clear stack at the start
		stack.clear();
		// Push the end marker and the start symbol on the stack
		stack.push(TokenType.ENDOFFILE);
		stack.push(NonTerminal.Goal);
		// Loop until the stack is empty:
		while(!stack.isEmpty()){
			// Pop the first element off of the stack
			predicted = stack.pop();
			// Check if there is a token (terminal) on the stack
			if(predicted.isToken()){
				// Try to match the current token with the non-terminal: 
				if(predicted == currentToken.getType()){
//					System.out.println(currentToken);
					// If they match, we get the next token from the input
					prevToken = currentToken;
					currentToken = lexer.GetNextToken();
				}
				// The terminals do not match: print error message.  
				// Start error recovery routine
				else{
					if(DUMPSTACK) { dumpStack(); }
					try {
						throw ParseError.UnmatchedTerminals(lexer.getLineNumber(), lexer.getCurrentLine(), predicted, currentToken.getType());
					}catch(ParseError e){
						System.out.println(e.getMessage());
						unMatchedTerminalRecovery();
					}
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
				// An error code is found: go into panic mode error recovery
				else if(index >= parseTable.ERRORCODE){
					if(DUMPSTACK) { dumpStack(); }
					try{
						throw ParseError.ErrorProduction(lexer.getLineNumber(), lexer.getCurrentLine(), 
								parseTable.getErrorMessage(index), currentToken.getType());
					}catch(ParseError e){
						System.out.println(e.getMessage());
						panicModeRecovery();
					}
					
				}
				// If the code is not negative or an error, we have a valid production
				else{
					// Retrieve productions to push onto the stack from the RHSTable
					GrammarSymbol[] productions = rhsTable.getRule(index);
					// Push onto the stack in reverse order, so they are popped off in the correct order
					for(int i = productions.length-1; i>=0; i--){
						stack.push(productions[i]);
					}
				}
			}
			// Otherwise if the symbol popped off is a semantic action, we ignore it and keep popping symbols off
			else if(predicted.isAction()){
//				System.out.println(predicted + ": " + prevToken);
				semanticActions.Execute((SemanticAction)predicted, prevToken);
				continue;
			}
			// This portion of code shouldn't execute! The symbol is neither a terminal, nonterminal,
			// or a semantic action. throw an error.
			else{
				throw ParseError.UnknownSymbolType(lexer.getLineNumber(), lexer.getCurrentLine(), predicted);
			}
		} // End While Loop
//		semanticActions.dumpGlobalTable();
//		semanticActions.dumpConstantTable();
		semanticActions.printGeneratedCode();
	}
	
	/** Method to print out the contents of the stack. 
	 *  Iterates through the grammar symbols from the top of the stack
	 *  to the bottom of the stack, as if we were continually popping elements off of the stack
	 */
	public void dumpStack(){
		System.out.println();
		// Print the symbol we are currently looking at: "top" of the stack
		System.out.println(predicted);
		// Print the rest of the stack
		Iterator<GrammarSymbol> iter = stack.iterator();
		while(iter.hasNext()){
			System.out.println(iter.next());
		}
	}
	
	/** The recovery method for when the current terminal and the terminal at 
	 * the top of the stack do not match. We simply ignore the current terminal 
	 * on the stack and continue with our parse
	 */
	private void unMatchedTerminalRecovery() throws ParseError{
		// Prevents cascading errors and false "compilation succcessful" messages.
		// Furthermore, since ENDMARKER should only be encountered at the end of the file, 
		// we should not have to parse any more to catch extra errors. 
		if(predicted == TokenType.ENDMARKER){
			throw ParseError.ParserQuit();
		}
		System.out.println("Error recovery: A " + predicted.toString() + " terminal was inserted into the file");
	}
	
	/** Panic mode recovery: Skips tokens until semicolon, end or EOF encountered. 
	 * Then does the same for the stack of grammar symbols. 
	 * Minimizes cascading errors. 
	 * @throws LexicalError */
	private void panicModeRecovery() throws CompilerError{
		System.out.println("Error recovery: Panic mode");
		// Skip over terminals from the input
		while(currentToken.getType() != TokenType.ENDOFFILE && 
				currentToken.getType() !=TokenType.SEMICOLON &&
				currentToken.getType() != TokenType.END)
		{
			//System.out.println("Skipping over " + currentToken.getType().toString());
			currentToken = lexer.GetNextToken();
		}
		// Skip over grammar symbols on the stack
		while(predicted != TokenType.ENDOFFILE && 
				predicted != TokenType.SEMICOLON &&
				predicted != TokenType.END)
		{
			predicted = stack.pop();
		}
		// Push the eof, semicolon, or end symbol back onto the stack
		stack.push(predicted);
		// If we couldn't recover fully, we quit execution. 
		// This prevents cascading errors. 
		if(predicted == TokenType.ENDOFFILE
			|| (currentToken.getType() == TokenType.SEMICOLON && predicted == TokenType.END)){
			throw ParseError.ParserQuit();
		}
	}
	
	public void printGlobalTable(){
		semanticActions.dumpGlobalTable();
	}
	public Tokenizer getLexer(){
		return lexer;
	}

}
