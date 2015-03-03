package lex;
import errors.*;
import token.*;

/** The Lexical Analyzer Class */
public class LexicalAnalyzer {
	/** A table for identifying reserved words */
	public KeywordTable keywordTable;
	/** Charstream used to get characters from the input */
	public CharStream charStream;
	/** Token kept for lookbehind. Needed to identify unary operators */
	public Token previousToken;
	/** Buffer to store lexemes (characters encountered so far)*/
	private StringBuilder buffer;
	/** Maximum length of an identifier. */
	private static final int MAX_LENGTH = 64;
	
	/** Constructor for the lexical analyzer */
	public LexicalAnalyzer(String file){
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
	}
	
	/** Get the current line number */
	public int getLineNumber(){
		return charStream.lineNumber();
	}
	
	/** Get the current index number */
	public int getColumnNumber(){
		return charStream.indexNumber();
	}
	
	/** Get the current line */
	public String getLine(){
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
		// Initialize state
		int state = 0;
		// Int to make sure identifier or constant is less than or equal to 64 chars long
		int stringLength = 0;
		// Invalid token
		char currentChar = '@';
		// Buffer is cleared to start
		buffer = new StringBuilder();
		while(true){
			switch(state){
			case 0:{
				currentChar = getChar();
				// If we see a digit
				if (isDigit(currentChar)){
					state = 1;
					stringLength++;
				}
				else if(currentChar == '='){
					state = 10;
				}
				else if(currentChar == '>'){
					state = 11;
				}
				else if(currentChar == '<'){
					state = 14;
				}
				else if(currentChar == '-'){
					state = 18;
				}
				else if(currentChar == '+'){
					state = 21;
				}
				else if(currentChar == '*'){
					state = 24;
				}
				else if(currentChar == '/'){
					state = 25;
				}
				else if(currentChar == ':'){
					state = 26;
				}
				else if(currentChar == ','){
					state = 29;
				}
				else if(currentChar == ';'){
					state = 30;
				}
				else if(currentChar == ')'){
					state = 31;
				}
				else if(currentChar == '('){
					state = 32;
				}
				else if(currentChar == ']'){
					state = 33;
				}
				else if(currentChar == '['){
					state = 34;
				}
				else if(currentChar == '.'){
					state = 35;
				}
				// EOF
				else if(isEndOfInput(currentChar)){
					state = 38;
				}
				// Identifier
				else if(isLetter(currentChar)){
					state = 39;
					// Increment the length of the identifier
					stringLength++;
				}
				// Right curly: means we have a hanging comment brace. 
				// If there was a corresponding left curly, it would have been 
				// caught by the CharStream's skipwhitespace() method. 
				else if(currentChar == '}'){
					throw LexicalError.UnmatchedComment(getLineNumber(), getLine());
				}
				// If the current char is blank, read the next character
				else if(isBlank(currentChar)){
					state = 0;
				}
				// Not any of these characters: it must be illegal character!
				else{
					throw LexicalError.IllegalCharacter(currentChar, getLineNumber(), getLine());
				}
				// Append the character to the buffer, as long as it is not a blank,
				// illegal charcter, or a right curly (}). 
				if(!isBlank(currentChar)){
					buffer.append(currentChar);
				}
				break;
			}
			// Saw a 0
			case 1: {
				currentChar = getChar();
				// If the current char is a digit, add the digit to the buffer. 
				// Append the digit to the buffer. 
				if(isDigit(currentChar)){
					buffer.append(currentChar);
					stringLength++;
				}
				// If the current char is a dot, we will go to the next state
				else if(currentChar == '.'){
					buffer.append(currentChar);
					stringLength++;
					state = 2;
				}
				// If the current char is an E, we have an exponential number
				else if(currentChar == 'e' || currentChar == 'E'){
					buffer.append('E');
					stringLength++;
					state = 6;
				}
				// Throw an exception if we have a non-E letter in this constant
				else if(isLetter(currentChar)){
					throw LexicalError.BadConstant(getLineNumber(), getLine());
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
					stringLength++;
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
					throw LexicalError.IllegalFLoat(getLineNumber(), getLine());
				}
				break;
			}
			// We have an INTCONSTANT. Check if the length is too long. 
			// If a valid length, return a Constant token of type INTCONSTANT
			case 3: {
				if(stringLength > MAX_LENGTH){
					throw LexicalError.ConstantTooLong(getLineNumber(), getLine());
				}
				else{
					return new Constant(TokenType.INTCONSTANT, buffer.toString());
				}
			}
			// We have a float constant on our hands
			case 5: {
				currentChar = getChar();
				// If we see another digit, append the digit
				// to the buffer and stay in the same state. 
				if(isDigit(currentChar)){
					buffer.append(currentChar);
					stringLength++;
				}
				// If we see an E
				else if(currentChar == 'e' || currentChar == 'E'){
					buffer.append('E');
					stringLength++;
					state = 6;
				}
				// If we see a different letter, throw an exception
				else if(isLetter(currentChar)){
					throw LexicalError.BadConstant(getLineNumber(), getLine());
				}
				// Similarly, if we see another dot throw an exception
				else if(currentChar == '.'){
					throw LexicalError.IllegalFloat2(getLineNumber(), getLine());
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
					stringLength++;
					state = 7;
				}
				// Positive exponent
				else if(isDigit(currentChar)){
					buffer.append(currentChar);
					stringLength++;
					state = 8;
				}
				else{
					throw LexicalError.IllegalExponent(getLineNumber(), getLine());
				}
				break;
			}
			// Saw 'E' followed by '+' or '-'
			case 7: {
				currentChar = getChar();
				if(isDigit(currentChar)){
					buffer.append(currentChar);
					stringLength++;
					state = 8;
				}
				else{
					throw LexicalError.IllegalExponent(getLineNumber(), getLine());
				}
			}
			// Saw a valid number following the 'E'
			case 8: {
				currentChar = getChar();
				if(isDigit(currentChar)){
					buffer.append(currentChar);
					stringLength++;
				}
				else if(currentChar == '.'){
					throw LexicalError.IllegalExponent(getLineNumber(), getLine());
				}
				else if(isLetter(currentChar)){
					throw LexicalError.BadConstant(getLineNumber(), getLine());
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
			// '='
			case 10: {
				// Return the '=' operator token
				state = 15;
				break;
			}
			// Saw '>'
			case 11: {
				// Get next character
				currentChar = getChar();
				// If the next character is an equals, 
				// to to state 15. There, it returns >= token
				if(currentChar == '='){
					buffer.append(currentChar);
					state = 15;
				}
				// If it is another character, return the > token
				else{
					// Push the character back
					pushback(currentChar);
					state = 15;
				}
				break;
			}
			// Saw '<'
			case 14: {
				// Get next character
				currentChar = getChar();
				// If next character is '>', return '<>' token
				if(currentChar == '>'){
					buffer.append(currentChar);
					state = 15;
				}
				// Return '<=' token
				else if(currentChar == '='){
					buffer.append(currentChar);
					state = 15;
				}
				// Push back the character, and return '<' token
				else{
					pushback(currentChar);
					state = 15;
				}
				break;
			} // End case 14
			/* Found a RELOP. Return the appropriate RELOP Token. */
			case 15: {
				return new Operator(TokenType.RELOP, buffer.toString());
			}
			
			// Saw a '-'
			case 18: {
				// Check if it is a binary or unary operator by checking previous token
				if(isBinaryOperator()){
					state = 19;
				}
				else{
					state = 20;
				}
				break;
			}
			// Return a binary minus
			case 19: {
				return new Operator(TokenType.ADDOP, "-");
			}
			// Return a unary minus
			case 20: {
				return new Token(TokenType.UNARYMINUS);
			}
			// Saw a '+'
			case 21: {
				// Check if it is a binary or unary operator by checking previous token
				if(isBinaryOperator()){
					state = 22;
				}
				else{
					state = 23;
				}
				break;
			}
			// Return a binary plus
			case 22: {
				return new Operator(TokenType.ADDOP, "+");
			}
			// Return a unary plus
			case 23: {
				return new Token(TokenType.UNARYPLUS);
			}
			// Saw a '*': return token
			case 24: {
				return new Operator(TokenType.MULOP, "*");
			}
			// Saw a '/': return token
			case 25: {
				return new Operator(TokenType.MULOP, "/");
			}
			// Saw a ':'
			case 26: {
				currentChar = getChar();
				// Check if assignment operator
				if(currentChar == '='){
					buffer.append(currentChar);
					state = 27;
				}
				// Or colon
				else{
					pushback(currentChar);
					state = 28;
				}
				break;
			}
			// Return ASSIGNOP Token
			case 27: {
				return new Token(TokenType.ASSIGNOP);
			}
			// Return COLON Token
			case 28: {
				return new Token(TokenType.COLON);
			}
			// Saw a comma. Return COMMA Token
			case 29: {
				return new Token(TokenType.COMMA);
			}
			// Saw a ';'. Return SEMICOLON Token
			case 30: {
				return new Token(TokenType.SEMICOLON);
			}
			// Saw a ')'. Return RIGHTPAREN Token
			case 31: {
				return new Token(TokenType.RIGHTPAREN);
			}
			// Saw a '('. Return LEFTPAREN Token
			case 32: {
				return new Token(TokenType.LEFTPAREN);
			}
			// Saw a ']'. Return RIGHTBRACKET Token
			case 33: {
				return new Token(TokenType.RIGHTBRACKET);
			}
			// Saw a '['. Return LEFTBRACKET Token
			case 34: {
				return new Token(TokenType.LEFTBRACKET);
			}
			// Saw a '.' Check to see if doubleDot or endmarker
			case 35: {
				// Check next character
				currentChar = getChar();
				// Go to state 36 if next character is another dot
				if(currentChar == '.'){
					state = 36;
				}
				// Otherwise pushback and go to state 37 
				else{
					pushback(currentChar);
					state = 37;
				}
				break;
			}
			// Saw two consecutive dots. Return DOUBLEDOT token
			case 36: {
				return new Token(TokenType.DOUBLEDOT);
			}
			// Saw only one dot. Return ENDMARKER token
			case 37: {
				return new Token(TokenType.ENDMARKER);
			}
			// Saw an end of file marker. Return ENDOFFILE token
			case 38: {
				return new Token(TokenType.ENDOFFILE);
			}
			// Saw a letter. This will be an identifier or keyword
			case 39: {
				currentChar = getChar();
				// If the current Char is either a letter or digit
				// Don't change the state
				if(isLetterOrDigit(currentChar)){
					buffer.append(currentChar);
					stringLength++;
				}
				// The next character is something else
				else{
					// Push back the current character
					pushback(currentChar);
					state = 40;
				}
				break;
			}
			// Found an identifier
			case 40: {
				// If the identifier is too long, throw an exception
				if(stringLength > MAX_LENGTH){
					throw LexicalError.IdentifierTooLong(getLineNumber(), getLine());
				}
				// Otherwise the identifier is a correct length
				// Return a new identifier token
				else{
					// Make 
					String s = buffer.toString().toUpperCase();
					// Converts the string to all uppercase characters
					// Create a new Identifier Token
					return new Identifier(TokenType.IDENTIFIER, s);
				}
			}
			// Input is something else
			default:{
				System.out.println("Unknown State");
			}
			
			}
		}
	} // End function
	
	/** Gets simple tokens from <code>assemble()</code>, and assigns types to keywords.
	 *  Provides symbol table pointers for identifiers and constants. 
	 * @return A token
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
		} catch (LexicalError e){
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	
	// Functions for character classification
	public boolean isEndOfInput(char ch){
		return ch == (char)CharStream.EOF;
	}
	public boolean isLayout(char ch) {
		return (!isEndOfInput(ch)  && (ch) <= ' ');
	}
	public boolean isCommentStarter(char ch){
		return ch == '{';
	}
	public boolean isCommentStopper(char ch){
		return ch == '}';
	}
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
	public boolean isOperator(char ch){
		return ch=='+' || ch=='-' || ch=='*' || ch=='/'
				|| ch=='=' || ch=='<' || ch=='>';
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
