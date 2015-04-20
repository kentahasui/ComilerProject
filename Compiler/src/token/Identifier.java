package token;

import grammarsymbols.TokenType;

public class Identifier extends Token{
	// Will eventually be a pointer to location in the symbol table
	public String lexeme;
	
	/** Constructor for an Identifier Token. 
	 *  Initializes the token type
	 * @param type
	 * @param lexeme
	 */
	public Identifier(TokenType type, String lexeme){
		super(type);
		this.lexeme = lexeme;
	}
	
	public Identifier(String lexeme){
		super(TokenType.IDENTIFIER);
		this.lexeme = lexeme;
	}
	
	/** Returns the lexeme (string) representation of this Identifier. 
	 * Will eventually return a pointer to a location in the symbol table
	 */
	@Override
	public String getValue(){
		return lexeme;
	}
	

}
