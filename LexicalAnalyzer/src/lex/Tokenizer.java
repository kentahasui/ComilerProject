package lex;
import errors.*;
import token.*;

/** The Lexical Analyzer Class */
public class Tokenizer {
	/** A table for identifying reserved words */
	private KeywordTable keywordTable;
	/** Charstream used to get characters from the input */
	private CharStream charStream;
	/** Token kept for lookbehind. Needed to identify unary operators */
	private Token previousToken;
	/** Buffer to store lexemes (characters encountered so far)*/
	private StringBuilder buffer;
	/** Char to store current character */
	private char currentChar;
	/** State of the machine */
	private int state; 
	/** Maximum length of an identifier. */
	private static final int MAX_LENGTH = 64;
	
	/** Constructor for the lexical analyzer */
	public Tokenizer(String file){
		// Initializes and reserves words in the keyword table
		keywordTable = new KeywordTable();
		// Initializes the charstream, and opens a file
		charStream = new CharStream(file);
		if(!charStream.isOpen()){
			System.err.println("An error occurred. The file is not open.");
		}
		previousToken = null;
		// Initializes the buffer
		buffer = new StringBuilder();
		// Initializes current char
		currentChar = '@';
	}
	
	/** Get the current line number */
	public int getLineNumber(){
		return charStream.lineNumber();
	}
	
	/** Get the current index number */
	public int getIndexNumber(){
		return charStream.indexNumber();
	}
	
	/** Get the current line */
	public String getCurrentLine(){
		return charStream.getCurrentLine();
	}
	
	/** Pushes back character into input */
	public void pushback(int ch){
		charStream.pushBack(ch);
	}
	
	/** Pushes back two dots into input. Used when we see a doubledot directly after a constant*/
	public void doublePushback(){
		charStream.pushBack('.');
		charStream.pushBack('.');
	}
	
	/** Method to check if the string used for an identifier is reserved for a keyword. <br>
	 *  Checks if the given string is in the Keyword Table
	 * 
	 * @param lexeme An identifier's value
	 * @return true if the string is reserved for a keyword <br>
	 *         false otherwise
	 */
	public boolean isKeyword(String lexeme){
		return keywordTable.isKeyword(lexeme);
	}
	
	/** Returns the keyword token stored in the keyword table <br>
	 *  We only have to create one instance of each keyword token
	 * 
	 * @param lexeme The keyword string
	 * @return The corresponding keyword token (a Token with TokenType == KEYWORD)
	 */
	public Token getKeyword(String lexeme){
		return keywordTable.table.get(lexeme);
	}
	
	/** Upon seeing a '+' or '-', call this function to see if the operator is 
	 *  a binary operator or a unary operator. 
	 * @return True if the previous token is not null and either: a RIGHTPAREN, a RIGHTBRACKET, an IDENTIFIER, 
	 *         an INTCONSTANT, or a REALCONSTANT.
	 *         Returns false otherwise. 
	 */
	public boolean isBinaryOperator(){
		return previousToken != null && 
				(previousToken.type == TokenType.RIGHTPAREN || previousToken.type == TokenType.RIGHTBRACKET 
				|| previousToken.type == TokenType.IDENTIFIER || previousToken.type == TokenType.INTCONSTANT
				|| previousToken.type == TokenType.REALCONSTANT);
	}
	
	/** Gets the next significant character from the input file. 
	 *  Throws a LexicalError if there is an illegal character or a 
	 *  malformed comment */
	public char getChar() throws LexicalError{
		return charStream.currentChar();
	}
	
	/** A method to assemble simple tokens from the input. 
	 *  Calls <code>getChar()</code> repeatedly until we find a 
	 *  string that corresponds to a token or we find a lexical 
	 *  error. 
	 * @return Any Token (except for Keyword Tokens)
	 * @throws LexicalError
	 */
	public Token assemble() throws LexicalError{
		// Buffer is cleared to start
		buffer = new StringBuilder();
		// Get a new char from the input
		currentChar = getChar();
		// If we see a digit, return a constant token (either an INTCONSTANT or a REALCONSTANT)
		if(isDigit(currentChar)){
			return getConstantToken();
		}
		// If we see a letter, return an identifier token
		else if(isLetter(currentChar)){
			return getIdentifierToken();
		}
		// If we see an operator (=<>+-*/), return an operator token.
		// This can also return a unary minus or a unary plus
		else if(isOperator(currentChar)){
			return getOperatorToken();
		}
		// If we see a colon, we look ahead to see if it is indeed a colon
		// or if it is part of an assignment operator
		else if(currentChar == ':'){
			currentChar = getChar();
			if(currentChar == '='){
				return new Token(TokenType.ASSIGNOP);
			}
			else{
				pushback(currentChar);
				return new Token(TokenType.COLON);
			}
		}
		else if(currentChar == ','){
			return new Token(TokenType.COLON);
		}
		else if(currentChar == ';'){
			return new Token(TokenType.SEMICOLON);
		}
		else if(currentChar == ')'){
			return new Token(TokenType.RIGHTPAREN);
		}
		else if(currentChar == '('){
			return new Token(TokenType.LEFTPAREN);
		}
		else if(currentChar == ']'){
			return new Token(TokenType.RIGHTBRACKET);
		}
		else if(currentChar == '['){
			return new Token(TokenType.LEFTBRACKET);
		}
		else if(currentChar == '.'){
			currentChar = getChar();
			if(currentChar == '.'){
				return new Token(TokenType.DOUBLEDOT);
			}
			else if(isLetterOrDigit(currentChar)){
				throw LexicalError.IllegalDecimal(getLineNumber(), getCurrentLine());
			}
			else{
				pushback(currentChar);
				return new Token(TokenType.ENDMARKER);
			}
		}
		// EOF
		else if(isEndOfInput(currentChar)){
			return new Token(TokenType.ENDOFFILE);
		}
		// If the current char is blank, read the next character (makes a recursive call)
		else if(isBlank(currentChar)){
			return assemble();
		}
		else{
			throw LexicalError.IllegalCharacter(currentChar, getLineNumber(), getCurrentLine());
		}
	} // End function
	
	/** Gets simple tokens from <code>assemble()</code>, and assigns types to keywords.
	 *  Provides symbol table pointers for identifiers and constants. 
	 * @return A token with the proper type associated with it
	 */
	public Token GetNextToken(){
		// Call the assemble function
		try{
			Token newToken = assemble();
			// Must check if token is an identifier or a keyword
			if(newToken.getType() == TokenType.IDENTIFIER){
				// Get the lexeme value for the Token
				String lexeme = newToken.getValue();
				// If the string is indeed a keyword, 
				// return the new Token
				if(isKeyword(lexeme)){
					newToken = getKeyword(lexeme); 
				}
			}
			// Update the previous token. Used for distinguishing unary operators
			previousToken = newToken;
			return newToken;
	    // If an error is thrown, stop execution and print out the error message
		} catch (LexicalError e){
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	/** This subroutine is called when <code>assemble()</code> encounters a letter. 
	 *  @return An identifier token 
	 *  @throws LexicalError IdentifierTooLong*/
	private Token getIdentifierToken() throws LexicalError{
		// Initialize the state, and append the previously seen letter to the buffer
		state = 0;
		buffer.append(currentChar);
		// Length of current identifier. If this number is greater than MAX_LENGTH, throw an error
		int IDLength = 1;
		// Loop infinitely
		while(true){
			switch(state){
			case 0: {
				currentChar = getChar();
				// If the current Char is either a letter or digit
				// Don't change the state
				if(isLetterOrDigit(currentChar)){
					buffer.append(currentChar);
					IDLength++;
				}
				// The next character is something else
				else{
					// Push back the current character
					pushback(currentChar);
					state = 1;
				}
				break;
			}
			// Found an identifier
			case 1: {
				// If the identifier is too long, throw an exception
				if(IDLength > MAX_LENGTH){
					throw LexicalError.IdentifierTooLong(getLineNumber(), getCurrentLine());
				}
				// Otherwise the identifier is a correct length
				// Return a new identifier token
				else{
					// Converts the string to all uppercase characters
					// Create a new Identifier Token
					String s = buffer.toString().toUpperCase();
					return new Identifier(TokenType.IDENTIFIER, s);
				}
			}
			}
		}
	}
	
	/** This subroutine is called when an operator is read. 
	 *  It calls <code>getRelopToken</code>, <code>getPlusMinusToken</code>, 
	 *  or <code>getMulopToken</code> depending on which operator was seen
	 *  @return an Operator token, a UNARYPLUS token, or a UNARYMINUS token*/
	private Token getOperatorToken() throws LexicalError{
		if(isRelop(currentChar)){
			return getRelopToken();
		}
		else if(isPlusMinus(currentChar)){
			return getPlusMinusToken();
		}
		else if(isMulop(currentChar)){
			return getMulopToken();
		}
		else{
			return unexpectedInput();
		}
	}
	
	/** This subroutine determines the proper RELOP token to return. Matches the longest possible RELOP
	 * @return A RELOP Operator Token
	 * @throws LexicalError
	 */
	private Token getRelopToken() throws LexicalError{
		// If the character is an =, return the equals Operator
		if(currentChar == ('=')){
			return new Operator(TokenType.RELOP, "=");
		}
		// If the character is a >, we look ahead
		else if(currentChar == '>'){
			currentChar = getChar();
			// Check if the operator is a ">="
			if(currentChar == '='){
				return new Operator(TokenType.RELOP, ">=");
			}
			// Otherwise it is a ">". Push the character back
			else{
				pushback(currentChar);
				return new Operator(TokenType.RELOP, ">");
			}
		}
		// If the character is a <, we look ahead
		else if(currentChar == '<'){
			currentChar = getChar();
			// Check if it is a "<>"
			if(currentChar == '>'){
				return new Operator(TokenType.RELOP, "<>");
			}
			// Check if it is "<="
			else if(currentChar == '='){
				return new Operator(TokenType.RELOP, "<=");
			}
			// Otherwise it is a "<". Push the character back
			else{
				pushback(currentChar);
				return new Operator(TokenType.RELOP, "<");
			}
		}
		else{
			return unexpectedInput();
		}
	}
	
	/** This subroutine is called when a '+' or '-' is read from the input. 
	 *  It determines whether to return a binary or unary operator
	 *  @return An ADDOP (+ or -), a UNARYPLUS, or UNARYMINUS Token
	 */
	private Token getPlusMinusToken() throws LexicalError{
		// If we saw a -
		if(currentChar == '-'){
			// Return an ADDOP Token if binary operator
			if(isBinaryOperator()){
				return new Operator(TokenType.ADDOP, "-");
			}
			// Otherwise return a UNARYMINUS token
			else{
				return new Token(TokenType.UNARYMINUS);
			}
		}
		// If we saw a +
		else if(currentChar == '+'){
			// Return an ADDOP Token if binary operator
			if(isBinaryOperator()){
				return new Operator(TokenType.ADDOP, "+");
			}
			// Otherwise return a UNARYMINUS token
			else{
				return new Token(TokenType.UNARYPLUS);
			}
		}
		else{
			return unexpectedInput();
		}
	}
	
	/** Subroutine called when a '*' or '/' is read from the input 
	 *  @return A MULOP token
	 * */
	private Token getMulopToken() throws LexicalError{
		if(currentChar == '*'){
			return new Operator(TokenType.MULOP, "*");
		}
		else if(currentChar == '/'){
			return new Operator(TokenType.MULOP, "/");
		}
		else{
			return unexpectedInput();
		}
	}
	
	/** This function is called when a digit is encountered when <code> assemble() </code> was called. 
	 *  The digit is not part of an identifier
	 * @return Either an INTCONSTANT token or a REALCONSTANT token
	 * @throws LexicalError
	 */
	private Token getConstantToken() throws LexicalError{
		state = 1;
		buffer.append(currentChar);
		// Loop infinitely, until either a token is found or an error is thrown
		while(true){
			switch(state){
			case 1: {
				currentChar = getChar();
				// If the current char is a digit, add the digit to the buffer. 
				// Append the digit to the buffer. 
				if(isDigit(currentChar)){
					buffer.append(currentChar);
				}
				// If the current char is a dot, we will go to the next state
				else if(currentChar == '.'){
					buffer.append(currentChar);
					state = 2;
				}
				// If the current char is an E, we have an exponential number
				else if(currentChar == 'e' || currentChar == 'E'){
					buffer.append('E');
					state = 6;
				}
				// Throw an exception if we have a non-E letter in this constant
				else if(isLetter(currentChar)){
					throw LexicalError.BadConstant(getLineNumber(), getCurrentLine());
				}
				// Otherwise we see a delimeter. Return push back the character, move on to the int state
				else{
					state = 3;
					pushback(currentChar);
				}
				break;
			}
			// Saw digits, then a '.'
			case 2: {
				currentChar = getChar();
				// If the next character is a digit, this constant is a real
				if(isDigit(currentChar)){
					buffer.append(currentChar);
					state = 5;
				}
				// If we see two dots in a row, push both dots back. Return an intconstant
				else if(currentChar == '.'){
					buffer.deleteCharAt(buffer.length()-1);
					doublePushback();
					state = 3;
				}
				// Otherwise, we have an error! A digit must follow a decimal
				else{
					throw LexicalError.IllegalFLoat(getLineNumber(), getCurrentLine());
				}
				break;
			}
			// We have an INTCONSTANT. Check if the length is too long. 
			// If a valid length, return a Constant token of type INTCONSTANT
			case 3: {
				return new Constant(TokenType.INTCONSTANT, buffer.toString());
			}
			// We have a float constant on our hands
			case 5: {
				currentChar = getChar();
				// If we see another digit, append the digit
				// to the buffer and stay in the same state. 
				if(isDigit(currentChar)){
					buffer.append(currentChar);
				}
				// If we see an E
				else if(currentChar == 'e' || currentChar == 'E'){
					buffer.append('E');
					state = 6;
				}
				// If we see a different letter, throw an exception
				else if(isLetter(currentChar)){
					throw LexicalError.BadConstant(getLineNumber(), getCurrentLine());
				}
				// Similarly, if we see another dot throw an exception
				else if(currentChar == '.'){
					throw LexicalError.IllegalFloat2(getLineNumber(), getCurrentLine());
				}
				// Otherwise we've seen a delimeter. We can now return a realconstant
				else{
					pushback(currentChar);
					state = 9;
				}
				break;
			}
			// We have seen some digits, followed by an "E"
			case 6: {
				currentChar = getChar();
				// Positive or negative exponent
				if(currentChar == '+'  || currentChar == '-'){
					buffer.append(currentChar);
					state = 7;
				}
				// Positive exponent
				else if(isDigit(currentChar)){
					buffer.append(currentChar);
					state = 8;
				}
				else{
					throw LexicalError.IllegalExponent(getLineNumber(), getCurrentLine());
				}
				break;
			}
			// Saw 'E' followed by '+' or '-'
			case 7: {
				currentChar = getChar();
				if(isDigit(currentChar)){
					buffer.append(currentChar);
					state = 8;
				}
				else{
					throw LexicalError.IllegalExponent(getLineNumber(), getCurrentLine());
				}
			}
			// Saw a valid number following the 'E'
			case 8: {
				currentChar = getChar();
				if(isDigit(currentChar)){
					buffer.append(currentChar);
				}
				else if(currentChar == '.'){
					throw LexicalError.IllegalExponent(getLineNumber(), getCurrentLine());
				}
				else if(isLetter(currentChar)){
					throw LexicalError.BadConstant(getLineNumber(), getCurrentLine());
				}
				else{
					pushback(currentChar);
					state = 9;
				}
				break;
			}
			// Found a realConstant! 
			case 9: {
				return new Constant(TokenType.REALCONSTANT, buffer.toString());
			}
			default: {
				return unexpectedInput();
			}
			}
		}
	}
	
	/** This method is mainly used for debugging. It is called if a character is wrongly classified 
	 * during the <code>assemble()</code> method
	 * @return an ERROR token
	 */
	private Token unexpectedInput(){
		System.out.println("Unexpected Input for currentChar: " + currentChar);
		return new Token(TokenType.ERROR);
	}
	
	
	// Methods for character classification
	public boolean isEndOfInput(char ch){
		return ch == (char)CharStream.EOF;
	}
	public boolean isLayout(char ch) {
		return (!isEndOfInput(ch)  && (ch) <= ' ');
		}
//	public boolean isCommentStarter(char ch){
//		return ch == '{';
//	}
//	public boolean isCommentStopper(char ch){
//		return ch == '}';
//	}
	public boolean isUCLetter(char ch){
		return ('A' <= (ch) && (ch) <= 'Z');
	}
	public boolean isLCLetter(char ch){
		return ('a' <= (ch) && (ch) <= 'z');
	}
	public boolean isLetter(char ch){
		return isUCLetter(ch) || isLCLetter(ch);
	}
	public boolean isDigit(char ch){
		return ('0' <= (ch) && (ch) <= '9');
	}
	public boolean isLetterOrDigit(char ch){
		return isLetter(ch) || isDigit(ch);
	}
	public boolean isPlusMinus(char ch){
		return ch=='+' || ch=='-';
	}
	public boolean isMulop(char ch){
		return ch=='*' || ch=='/';
	}
	public boolean isRelop(char ch){
		return ch=='=' || ch=='<' || ch=='>';
	}
	public boolean isOperator(char ch){
		return isPlusMinus(ch) || isMulop(ch) || isRelop(ch);
	}
	public boolean isSeparator(char ch){
		return ch==';' || ch==',' || ch=='(' || ch==')';
	}
	public boolean isBlank(char ch){
		return ch == CharStream.BLANK;
	}
	public boolean isDelim(char ch){
		return isOperator(ch) || isSeparator(ch) || isBlank(ch);
	}
	
	

}